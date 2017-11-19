package cat.eduard.degiro.http;

import cat.eduard.degiro.log.Log;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;

/**
 *
 * @author indiketa
 */
public class DHttpManager {

    protected final CloseableHttpClient httpClient;
    protected final CookieStore cookieStore;
    private static DInactiveConnectionManager inactiveConnections = null;
    private static final int DEFAULT_KEEP_ALIVE_TIMEOUT = 90;
    private static final int CONNECTION_SYN_TIMEOUT_SECONDS = 10;
    private static final int REPLY_TIMEOUT_SECONDS = 60;
    private static final int MAX_CONNECTIONS_PER_ROUTE = 20;
    private static final int TOTAL_MAX_CONNECTIONS = MAX_CONNECTIONS_PER_ROUTE * 27;

    protected DHttpManager() {

        SSLConnectionSocketFactory sslSocketFactory = null;
        try {
            SSLContextBuilder contextSSL = new SSLContextBuilder();
            contextSSL.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            sslSocketFactory = new SSLConnectionSocketFactory(contextSSL.build(), new DefaultHostnameVerifier());
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            Log.API.fatal("Error creant la factoria SSL", e);
        }

        Registry<ConnectionSocketFactory> tipusConnexions = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(tipusConnexions);
        cm.setMaxTotal(TOTAL_MAX_CONNECTIONS);
        cm.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
        cm.setDefaultConnectionConfig(ConnectionConfig.DEFAULT);

        inactiveConnections = new DInactiveConnectionManager(cm);
        inactiveConnections.start();

        ConnectionKeepAliveStrategy estrategiaKeepAlive = new ConnectionKeepAliveStrategy() {

            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase("timeout")) {
                        try {
                            return Long.parseLong(value) * 1000;
                        } catch (NumberFormatException ignore) {
                        }
                    }
                }
                return DEFAULT_KEEP_ALIVE_TIMEOUT * 1000;
            }

        };

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECTION_SYN_TIMEOUT_SECONDS * 1000)
                .setSocketTimeout(REPLY_TIMEOUT_SECONDS * 1000)
                .setCookieSpec(CookieSpecs.DEFAULT)
                .build();

        SocketConfig sc = SocketConfig.custom()
                .setSoKeepAlive(true)
                .setSoTimeout(REPLY_TIMEOUT_SECONDS * 1000)
                .setTcpNoDelay(true)
                .build();

        cookieStore = new BasicCookieStore();

        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig)
                .setKeepAliveStrategy(estrategiaKeepAlive)
                .setDefaultSocketConfig(sc)
                .setDefaultCookieStore(cookieStore)
                .build();

    }

    public HttpClient getClient() {
        return httpClient;
    }

    public void shutdown() throws IOException {
        inactiveConnections.shutdown();
        httpClient.close();
    }

}
