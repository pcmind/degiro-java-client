package cat.indiketa;

import cat.indiketa.degiro.DeGiro;
import cat.indiketa.degiro.DeGiroFactory;
import cat.indiketa.degiro.utils.DCredentials;
import cat.indiketa.degiro.session.DPersistentSession;
import cat.indiketa.degiro.log.DLog;
import cat.indiketa.degiro.model.DPrice;
import cat.indiketa.degiro.model.DPriceListener;
import cat.indiketa.degiro.model.DProductType;
import com.google.gson.GsonBuilder;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

//        DeGiro degiro = DeGiroFactory.newInstance(creds, new DPersistentSession("/home/casa/session.txt"));
        DeGiro degiro = DeGiroFactory.newInstance(creds);

//        degiro.getOrders();
//        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(degiro.getPortfolio()));
//        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(degiro.getCashFunds()));
//        Calendar c = Calendar.getInstance();
//        Calendar c2 = Calendar.getInstance();
//        c.add(Calendar.MONTH, -1);
//        degiro.getTransactions(c, c2);
//        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(degiro.getTransactions(c, c2)));
//        degiro.getPricce();
//        List<String> productIds = new ArrayList<>();
//        productIds.add("1482366"); //dia
//
//        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(degiro.getProducts(productIds)));
//
//        degiro.setPriceListener(new DPriceListener() {
//            @Override
//            public void priceChanged(DPrice price) {
//                System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(price));
//            }
//        });
//
//        List<Long> vwdIssueIds = new ArrayList<>();
//        vwdIssueIds.add(280099308L); //dia
//        degiro.subscribeToPrice(vwdIssueIds);

 System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(degiro.searchProducts("sab", DProductType.ALL, 10, 0)));

        while (true) {
            Thread.sleep(1000);
        }

    }

}
