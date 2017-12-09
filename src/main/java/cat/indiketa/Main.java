package cat.indiketa;

import cat.indiketa.degiro.DeGiro;
import cat.indiketa.degiro.DeGiroFactory;
import cat.indiketa.degiro.utils.DCredentials;
import cat.indiketa.degiro.log.DLog;
import cat.indiketa.degiro.model.DNewOrder;
import cat.indiketa.degiro.model.DOrderAction;
import cat.indiketa.degiro.model.DOrderTime;
import cat.indiketa.degiro.model.DOrderType;
import cat.indiketa.degiro.model.DPrice;
import cat.indiketa.degiro.model.DPriceListener;
import cat.indiketa.degiro.model.DProduct;
import cat.indiketa.degiro.model.DProductSearch;
import cat.indiketa.degiro.model.DProductType;
import cat.indiketa.degiro.model.DProducts;
import cat.indiketa.degiro.session.DPersistentSession;
import com.google.gson.GsonBuilder;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author indiketa
 */
public class Main {

    public static void main(String[] args) throws Exception {
        DCredentials creds = new DCredentials() {

            private Properties props = null;

            private synchronized void checkProps() {
                if (props == null) {
                    props = new Properties();
                    try {
                        try (InputStream is = new FileInputStream("/home/casa/dg.properties")) {
                            props.load(is);
                        }
                    } catch (IOException e) {
                        DLog.MANAGER.error("Error cred", e);
                    }
                }
            }

            @Override
            public String getUsername() {
                checkProps();
                return props.getProperty("username");
            }

            @Override
            public String getPassword() {
                checkProps();
                return props.getProperty("password");
            }
        };

        DeGiro degiro = DeGiroFactory.newInstance(creds, new DPersistentSession("/home/casa/session.txt"));
//        DeGiro degiro = DeGiroFactory.newInstance(creds);

        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(degiro.getOrders()));
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(degiro.getPortfolio()));
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(degiro.getCashFunds()));
        Calendar c = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
//        degiro.getTransactions(c, c2);
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(degiro.getTransactions(c, c2)));
//        degiro.getPricce();
        List<Long> productIds = new ArrayList<>();
        productIds.add(1482366L); //dia

        DProducts products = degiro.getProducts(productIds);
        
        for (DProduct value : products.getData().values()) {
            
        }

        degiro.setPriceListener(new DPriceListener() {
            @Override
            public void priceChanged(DPrice price) {
                System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(price));
            }
        });

        List<Long> vwdIssueIds = new ArrayList<>();
        vwdIssueIds.add(280099308L); //dia
        degiro.subscribeToPrice(vwdIssueIds);
        degiro.setPricePollingInterval(1, TimeUnit.MINUTES);
        degiro.clearPriceSubscriptions();
//        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(degiro.searchProducts("sab", DProductType.ALL, 10, 0)));
        //
        DNewOrder order = new DNewOrder(DOrderAction.SELL, DOrderType.LIMITED, DOrderTime.DAY, 1482366, 20, new BigDecimal("4.5"), null);
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(degiro.deleteOrder(degiro.confirmOrder(order, degiro.checkOrder(order).getConfirmationId()).getOrderId())));
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(degiro.deleteOrder(UUID.randomUUID().toString())));

        //        while (true) {
        //            Thread.sleep(1000);
        //        }
        
        DProductSearch ps = degiro.searchProducts("sab", DProductType.ALL, 10, 0);
        for (DProduct product : ps.getProducts()) {
            System.out.println(product.getId() + " " + product.getName());
        }
    }

}
