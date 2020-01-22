package cat.indiketa.degiro.http.impl;

import cat.indiketa.degiro.http.DResponse;
import cat.indiketa.degiro.http.IDCommunication;
import cat.indiketa.degiro.log.DLog;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

/**
 * @author indiketa
 */
public class DCommunication implements IDCommunication {

    private final DHttpManager client;
    private final Gson gson;
    private final BasicHttpContext context;

    private DCommunication(BasicCookieStore cookieStore) {
        this.client = new DHttpManager(cookieStore);
        this.gson = new Gson();
        this.context = new BasicHttpContext();
    }

    public static IDCommunication create(BasicCookieStore cookieStore) {
        return new DCommunication(cookieStore);
    }

    @Override
    public DResponse getUrlData(String base, String uri, Object data) throws IOException {
        return getUrlData(base, uri, data, null, null);
    }

    @Override
    public DResponse getUrlData(String base, String uri, Object data, List<Header> headers) throws IOException {
        return getUrlData(base, uri, data, headers, null);
    }

    @Override
    public DResponse getUrlData(String base, String uri, Object data, List<Header> headers, final String method) throws IOException {

        String url = base + uri;
        HttpRequestBase request = null;

        if (data == null) {
            final String method1 = Strings.isNullOrEmpty(method) ? "GET" : method;
            request = new HttpRequestBase() {
                @Override
                public String getMethod() {
                    return method1;
                }
            };
            DLog.HTTP.debug(uri + " " + method1);
        } else {
            final String method1 = Strings.isNullOrEmpty(method) ? "POST" : method;
            request = new HttpEntityEnclosingRequestBase() {
                @Override
                public String getMethod() {
                    return method1;
                }
            };
            request.addHeader("Content-Type", "application/json");
            String jsonData = gson.toJson(data);
            if(!jsonData.contains("password")) {
                DLog.HTTP.debug(uri + " " + method1 + " application/json body: " + jsonData);
            }else{
                DLog.HTTP.debug(uri + " " + method1 + " application/json body: ********");
            }
            ((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(jsonData));
        }

        request.setURI(URI.create(url));

        if (headers != null) {
            for (Header header : headers) {
                request.addHeader(header);
            }
        }

        DResponse dResponse;

        try (CloseableHttpResponse response = client.getClient().execute(request, context)) {
            dResponse = new DResponse(
                    response.getStatusLine().getStatusCode(),
                    url,
                    request.getMethod(),
                    CharStreams.toString(new InputStreamReader(response.getEntity().getContent(), Charsets.UTF_8))
            );
        }

        return dResponse;

    }
}
