package cat.indiketa.degiro.engine;

import cat.indiketa.degiro.DeGiro;
import cat.indiketa.degiro.DeGiroFactory;
import cat.indiketa.degiro.exceptions.DeGiroException;
import cat.indiketa.degiro.log.DLog;
import cat.indiketa.degiro.model.DPortfolioProducts;
import cat.indiketa.degiro.model.DPortfolioProducts.DPortfolioProduct;
import cat.indiketa.degiro.model.DPortfolioSummary;
import cat.indiketa.degiro.model.DProductDescription;
import cat.indiketa.degiro.model.DProductDescriptions;
import cat.indiketa.degiro.session.DPersistentSession;
import cat.indiketa.degiro.utils.DCredentials;
import com.google.common.base.Joiner;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author indiketa
 */
public class DEngine extends Thread {

    private final PathManager pathManager;
    private final DeGiro degiro;
    private DEngineConfig config;

    private final Map<Long, DProduct> productMap;

    private Timer portfolioTimer;
    private Timer portfolioSummaryTimer;

    private final static String PORTFOLIO = "PF";
    private final static String SUMMARY = "SM";
    private final static String DESCRIPTION = "DS";
    private final static String PRICES = "PR";
    private final Set<String> inactiveComponents;

    public DEngine(DCredentials credentials) throws IOException {
        this(new DEngineConfig(), credentials);
    }

    public DEngine(DEngineConfig config, DCredentials credentials) throws IOException {
        super("engine");
        setDaemon(false);
        this.config = config;
        this.pathManager = new PathManager(config.getDataDirectory());
        this.productMap = new HashMap<>();
        DLog.ENGINE.info("Creating DeGiro manager instance...");
        File sessionFile = pathManager.getSessionFile(credentials.getUsername());
        degiro = DeGiroFactory.newInstance(credentials, new DPersistentSession(sessionFile));
        this.inactiveComponents = new HashSet<>();
        this.inactiveComponents.add(PORTFOLIO);
        this.inactiveComponents.add(SUMMARY);
        this.inactiveComponents.add(DESCRIPTION);
        this.inactiveComponents.add(PRICES);
    }

    @Override
    public void run() {

        DLog.ENGINE.info("Starting control plane...");
        startPortfolioTimer();
        startPortfolioSummaryTimer();

        while (!inactiveComponents.isEmpty()) {
            DLog.ENGINE.info("API client created, waiting for the control plane to become ready. Remaining: [" + Joiner.on(",").join(inactiveComponents) + "]");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {

            }
        }

    }

    private void startPortfolioTimer() {
        portfolioTimer = new Timer("portfolio", false);
        portfolioTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    DPortfolioProducts portfolio = degiro.getPortfolio();
                    boolean newActiveProducts = mergeProducts(portfolio.getActive());
                    boolean newInactiveProducts = mergeProducts(portfolio.getInactive());

                    inactiveComponents.remove(PORTFOLIO);

                    if (portfolio.getActive().size() + portfolio.getInactive().size() == 0) {
                        inactiveComponents.remove(DESCRIPTION);
                        inactiveComponents.remove(PRICES);
                    }

                    DLog.ENGINE.info("Portfolio refresh completed: " + portfolio.getActive().size() + " active, " + portfolio.getInactive().size() + " inactive.");

                    if (newActiveProducts || newInactiveProducts) {
                        fetchDescriptions();
                    }

                } catch (Exception e) {
                    DLog.ENGINE.error("Exception while refreshing portfolio", e);
                }
            }
        }, config.getPortfolioRefreshInterval() * 1000, config.getPortfolioRefreshInterval() * 1000);

    }

    private boolean mergeProducts(List<DPortfolioProduct> products) {

        boolean oneRegistered = false;

        if (products != null) {
            for (DPortfolioProduct product : products) {
                if (productMap.containsKey(product.getId())) {
                    productMap.get(product.getId()).setProductStatus(product);
                } else {
                    registerProduct(product);
                    oneRegistered = true;
                }

            }
        }

        return oneRegistered;
    }

    private void registerProduct(DPortfolioProduct add) {
        DProduct product = new DProduct(this);
        product.setProductStatus(add);
        productMap.put(add.getId(), product);

    }

    private void fetchDescriptions() throws DeGiroException {

        List<Long> productIds = new LinkedList<>();
        for (DProduct product : productMap.values()) {
            if (product.getDescription() == null) {
                productIds.add(product.getProductStatus().getId());
            }
        }

        if (!productIds.isEmpty()) {
            DLog.ENGINE.info("Fetching " + productIds.size() + " product descriptions");
            DProductDescriptions descriptions = degiro.getProducts(productIds);
            Map<Long, DProductDescription> data = descriptions.getData();
            for (Long productId : data.keySet()) {
                productMap.get(productId).setDescription(data.get(productId));
            }
            inactiveComponents.remove(DESCRIPTION);
        }

    }

    private void startPortfolioSummaryTimer() {
        portfolioSummaryTimer = new Timer("portSumm", false);
        portfolioSummaryTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    DPortfolioSummary summary = degiro.getPortfolioSummary();
                    DLog.ENGINE.info("Portfolio summary refreshed (" + summary.toString() + ").");
                    inactiveComponents.remove(SUMMARY);
                } catch (Exception e) {
                    DLog.ENGINE.error("Exception while refreshing portfolio", e);
                }
            }
        }, config.getPortfolioRefreshInterval() * 1000 / 2, config.getPortfolioRefreshInterval() * 1000);

    }

    public DEngineConfig getConfig() {
        return config;
    }

}
