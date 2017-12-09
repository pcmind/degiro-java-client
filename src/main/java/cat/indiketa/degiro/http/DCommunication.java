package cat.indiketa.degiro.http;

import cat.indiketa.degiro.session.DSession;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.BasicHttpContext;

/**
 *
 * @author indiketa
 */
public class DCommunication extends DHttpManager {

    private final Gson gson;
    private final BasicHttpContext context;

    public DCommunication(DSession session) {
        super(session);
        this.gson = new Gson();
        this.context = new BasicHttpContext();
    }

    public DResponse getUrlData(String base, String uri, Object data) throws UnsupportedEncodingException, IOException {
        return getUrlData(base, uri, data, null, null);
    }

    public DResponse getUrlData(String base, String uri, Object data, List<Header> headers) throws UnsupportedEncodingException, IOException {
        return getUrlData(base, uri, data, headers, null);
    }

    public DResponse getUrlData(String base, String uri, Object data, List<Header> headers, final String method) throws UnsupportedEncodingException, IOException {

        String url = base + uri;
        HttpRequestBase request = null;

        if (data == null) {
            request = new HttpRequestBase() {
                @Override
                public String getMethod() {
                    return Strings.isNullOrEmpty(method) ? "GET" : method;
                }
            };
        } else {
            request = new HttpEntityEnclosingRequestBase() {
                @Override
                public String getMethod() {
                    return Strings.isNullOrEmpty(method) ? "POST" : method;
                }
            };
            request.addHeader("Content-Type", "application/json");
            String jsonData = gson.toJson(data);
            ((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(jsonData));
        }

        request.setURI(URI.create(url));

        if (headers != null) {
            for (Header header : headers) {
                request.addHeader(header);
            }
        }

        DResponse dResponse;

        try (CloseableHttpResponse response = httpClient.execute(request, context)) {
            dResponse = new DResponse();
            dResponse.setStatus(response.getStatusLine().getStatusCode());
            dResponse.setText(CharStreams.toString(new InputStreamReader(response.getEntity().getContent(), Charsets.UTF_8)));
            dResponse.setMethod(request.getMethod());
            dResponse.setUrl(url);
        }

        return dResponse;

    }

    public DResponse getData(DSession session, String params, Object data) throws IOException {
        return getUrlData(session.getConfig().getTradingUrl() + "v5/update/" + session.getClient().getIntAccount() + ";jsessionid=" + session.getJSessionId(), "?" + params, data);
    }

    public static class DResponse {

        private int status;
        private String url;
        private String method;
        private String text;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

    }

}
