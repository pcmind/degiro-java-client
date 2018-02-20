package cat.indiketa.degiro.engine;

import cat.indiketa.degiro.DeGiro;
import cat.indiketa.degiro.DeGiroFactory;
import cat.indiketa.degiro.engine.event.DProductChanged;
import cat.indiketa.degiro.engine.event.DSummaryChanged;
import cat.indiketa.degiro.exceptions.DeGiroException;
import cat.indiketa.degiro.log.DLog;
import cat.indiketa.degiro.model.DPortfolioProducts;
import cat.indiketa.degiro.model.DPortfolioProducts.DPortfolioProduct;
import cat.indiketa.degiro.model.DPortfolioSummary;
import cat.indiketa.degiro.model.DPrice;
import cat.indiketa.degiro.model.DPriceListener;
import cat.indiketa.degiro.model.DProductDescription;
import cat.indiketa.degiro.model.DProductDescriptions;
import cat.indiketa.degiro.session.DPersistentSession;
import cat.indiketa.degiro.utils.DCredentials;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.eventbus.AsyncEventBus;
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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author indiketa
 */
public class DEngine {

    private final PathManager pathManager;
    private final DeGiro degiro;
    private DEngineConfig config;

    private final Map<Long, Product> productMap;
    private final Map<String, Product> productMapByIssue;

    private Timer portfolioTimer;
    private Timer portfolioSummaryTimer;
    private final AsyncEventBus eventBus;

    private DPortfolioSummary lastSummary;

    private final static String PORTFOLIO = "PF";
    private final static String SUMMARY = "SM";
    private final static String DESCRIPTION = "DS";
    private final static String PRICES = "PR";
    private final Set<String> inactiveComponents;

    public DEngine(DCredentials credentials) throws IOException {
        this(new DEngineConfig(), credentials);
    }

    public DEngine(DEngineConfig config, DCredentials credentials) throws IOException {
        this.config = config;
        this.pathManager = new PathManager(config.getDataDirectory());
        this.productMap = new HashMap<>();
        this.productMapByIssue = new HashMap<>();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 5, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        this.eventBus = new AsyncEventBus(executor);
        DLog.ENGINE.info("Creating DeGiro manager instance...");
        File sessionFile = pathManager.getSessionFile(credentials.getUsername());
        degiro = DeGiroFactory.newInstance(credentials, new DPersistentSession(sessionFile));
        degiro.setPriceListener(new DPriceListener() {
            @Override
            public void priceChanged(DPrice price) {
                DEngine.this.inactiveComponents.remove(PRICES);
                DEngine.this.productMapByIssue.get(price.getIssueId()).adopt(price);
            }
        });
        this.inactiveComponents = new HashSet<>();
        this.inactiveComponents.add(PORTFOLIO);
//        this.inactiveComponents.add(SUMMARY);
        this.inactiveComponents.add(DESCRIPTION);
        this.inactiveComponents.add(PRICES);
    }

    public void startEngine() {

        DLog.ENGINE.info("Initializing control plane...");
        startPortfolioTimer();
//        startPortfolioSummaryTimer();

        while (!inactiveComponents.isEmpty()) {
            DLog.ENGINE.info("API client created, waiting for the control plane to become ready. Remaining: [" + Joiner.on(",").join(inactiveComponents) + "]");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {

            }
        }

        DLog.ENGINE.info("Control plane initialized");

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

    private boolean mergeProducts(List<DPortfolioProduct> products) throws DeGiroException {

        boolean oneRegistered = false;

        if (products != null) {
            for (DPortfolioProduct product : products) {
                if (productMap.containsKey(product.getId())) {
                    productMap.get(product.getId()).adopt(product);
                } else {
                    registerProduct(product);
                    oneRegistered = true;
                }

            }
        }

        return oneRegistered;
    }

    private void registerProduct(DPortfolioProduct add) throws DeGiroException {
        Product pro = new Product(this);
        pro.adopt(add);
        productMap.put(add.getId(), pro);
        eventBus.post(new DProductChanged(pro));
    }

    private void fetchDescriptions() throws DeGiroException {

        List<Long> productIds = new LinkedList<>();
        for (Product product : productMap.values()) {
            if (product.getId() != 0) {
                productIds.add(product.getId());
            }
        }

        if (!productIds.isEmpty()) {
            DLog.ENGINE.info("Fetching " + productIds.size() + " product descriptions");
            DProductDescriptions descriptions = degiro.getProducts(productIds);
            Map<Long, DProductDescription> data = descriptions.getData();
            List<String> vwdIssueId = new LinkedList<>();
            for (Long productId : data.keySet()) {
                DProductDescription description = data.get(productId);
                productMap.get(productId).adopt(description);
                if (!Strings.isNullOrEmpty(description.getVwdId())) {
                    productMapByIssue.put(description.getVwdId(), productMap.get(productId));
                    if (productMap.get(productId).getQty() != 0) {
                        vwdIssueId.add(description.getVwdId());
                    }
                }
            }

            inactiveComponents.remove(DESCRIPTION);
            if (!vwdIssueId.isEmpty()) {
                degiro.subscribeToPrice(vwdIssueId);
            }
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

                    if (lastSummary != null && lastSummary.hashCode() != summary.hashCode()) {
                        eventBus.post(new DSummaryChanged(summary));
                    }
                    lastSummary = summary;
                } catch (Exception e) {
                    DLog.ENGINE.error("Exception while refreshing portfolio", e);
                }
            }
        }, config.getPortfolioRefreshInterval() * 1000 / 2, config.getPortfolioRefreshInterval() * 1000);

    }

    public DEngineConfig getConfig() {
        return config;
    }

    public DPortfolioSummary getLastSummary() {
        return lastSummary;
    }

    public List<Product> getPortfolio() {
        List<Product> products = new LinkedList<>();
        for (Product value : productMap.values()) {
            products.add(value);
        }
        System.out.println(products.size());
        return products;
    }

    AsyncEventBus getEventBus() {
        return eventBus;
    }

    public void register(Object eventReceiver) {
        eventBus.register(eventReceiver);
    }

    public DeGiro getDegiro() {
        return degiro;
    }

}
