package cat.eduard.degiro.http;

import cat.eduard.degiro.log.DLog;
import cat.eduard.degiro.model.DClient;
import cat.eduard.degiro.model.DConfig;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import static com.oracle.util.Checksums.update;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.BasicHttpContext;

/**
 *
 * @author indiketa
 */
public class DCommunication extends DHttpManager {

    private final Gson gson;
    private final BasicHttpContext context;
    private static long call;

    public DCommunication() {
        this.gson = new Gson();
        this.context = new BasicHttpContext();
    }

    public DResponse getUrlData(String base, String uri, Object data) throws UnsupportedEncodingException, IOException {

        long callId = ++call;
        String url = base + uri;
        DLog.WIRE.trace(String.format("[%d] Call %s", callId, url));
        HttpUriRequest request = null;

        if (data != null) {
            HttpPost post = new HttpPost(url);
            post.addHeader("Content-Type", "application/json");
            String jsonData = gson.toJson(data);
            post.setEntity(new StringEntity(jsonData));
            request = post;
        } else {
            request = new HttpGet(url);
        }

        DResponse dResponse;

        try (CloseableHttpResponse response = httpClient.execute(request, context)) {
            dResponse = new DResponse();
            dResponse.setStatus(response.getStatusLine().getStatusCode());
            dResponse.setText(CharStreams.toString(new InputStreamReader(response.getEntity().getContent(), Charsets.UTF_8)));

        }

        return dResponse;

    }

    public DResponse getData(DClient client, DConfig config, String params, Object data) throws IOException {

        long callId = ++call;

//         `${urls.tradingUrl}v5/update/${session.account};jsessionid=${session.id}?${params}`
        String url = config.getTradingUrl() + "v5/update/" + client.getIntAccount() + ";jsessionid=" + getJSessionId() + "?" + params;
        DLog.WIRE.trace(String.format("[%d] Call %s", callId, url));
        HttpUriRequest request = null;

        if (data != null) {
            HttpPost post = new HttpPost(url);
            post.addHeader("Content-Type", "application/json");
            String jsonData = gson.toJson(data);
            post.setEntity(new StringEntity(jsonData));
            request = post;
        } else {
            request = new HttpGet(url);
        }

        DResponse dResponse;

        try (CloseableHttpResponse response = httpClient.execute(request, context)) {
            dResponse = new DResponse();
            dResponse.setStatus(response.getStatusLine().getStatusCode());
            dResponse.setText(CharStreams.toString(new InputStreamReader(response.getEntity().getContent(), Charsets.UTF_8)));

        }

        return dResponse;

    }

    public static class DResponse {

        private int status;
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

    }

    public String getJSessionId() {
        String value = null;
        List<Cookie> cookies = cookieStore.getCookies();

        int i = 0;
        while (i < cookies.size() && !cookies.get(i).getName().equalsIgnoreCase("JSESSIONID")) {
            i++;
        }

        if (i < cookies.size()) {
            value = cookies.get(i).getValue();
        }

        return value;
    }

}
