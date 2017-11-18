/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.eduard.degiro.http;

import cat.eduard.degiro.log.Log;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.BasicHttpContext;

/**
 *
 * @author casa
 */
public class DCommunication extends DHttpManager {

    
    private final Gson GSON;
    private final BasicHttpContext CONTEXT;
    private static long call;

    public DCommunication() {
        this.GSON = new Gson();
        this.CONTEXT = new BasicHttpContext();
    }

    
        public DResponse getData(String base, String uri, Object data) throws UnsupportedEncodingException, IOException {

        long callId = ++call;
        String url = base + uri;
        Log.WIRE.trace(String.format("[%d] Call %s", callId, url));
        HttpUriRequest request = null;

        if (data != null) {
            HttpPost post = new HttpPost(url);
            post.addHeader("Content-Type", "application/json");
            String jsonData = GSON.toJson(data);
            post.setEntity(new StringEntity(jsonData));
            request = post;
        } else {
            request = new HttpGet(url);
        }

        DResponse dResponse;
        
        try (CloseableHttpResponse response = HTTP_CLIENT.execute(request, CONTEXT)) {
            dResponse = new DResponse();
            dResponse.setStatus(response.getStatusLine().getStatusCode());
            dResponse.setText(CharStreams.toString(new InputStreamReader(response.getEntity().getContent(), Charsets.UTF_8)));

    
        }

        return dResponse;

    }

    public DResponse getData(Object options) throws UnsupportedEncodingException, IOException {
//
//        long callId = ++call;
//        String url = BASE_TRADER_URL + "/v5/update/XXX;jsessionid=" + getJSessionId() + "?";
//        Log.WIRE.trace(String.format("[%d] Call %s", callId, url));
//        HttpPost post = new HttpPost(url);
//        post.addHeader("Content-Type", "application/json");
//
//        if (data != null) {
//            String jsonData = GSON.toJson(data);
//            Log.WIRE.trace(String.format("[%d] Payload %s", callId, jsonData));
//            post.setEntity(new StringEntity(jsonData));
//        }
//        DResponse dResponse = null;
//        try (CloseableHttpResponse response = HTTP_CLIENT.execute(post, CONTEXT)) {
//            dResponse = new DResponse();
//            Log.WIRE.trace(String.format("[%d] Status %d", callId, response.getStatusLine().getStatusCode()));
//            dResponse.setStatus(response.getStatusLine().getStatusCode());
//
//            for (Header header : response.getAllHeaders()) {
//                Log.WIRE.trace(String.format("[%d] Header %s: %s", callId, header.getName(), header.getValue()));
//            }
//        }
//
//        return dResponse;

        return null;

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
        List<Cookie> cookies = COOKIE_STORE.getCookies();

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
