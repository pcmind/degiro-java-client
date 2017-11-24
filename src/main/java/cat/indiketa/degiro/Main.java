package cat.eduard.degiro;

import cat.eduard.degiro.log.DLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author indiketa
 */
public class Main {

    public static void main(String[] args) throws Exception {
        DCredentials creds = new DCredentials() {

            Properties props = null;

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

        DManager degiro = new DManager(creds);

        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(degiro.getPortfolio()));
    }

}
