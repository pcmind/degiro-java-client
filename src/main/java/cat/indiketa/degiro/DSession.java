package cat.indiketa.degiro;

import cat.indiketa.degiro.model.DClient;
import cat.indiketa.degiro.model.DConfig;
import cat.indiketa.degiro.model.DVwdSession;
import com.google.gson.annotations.Expose;
import java.util.List;
import org.apache.http.impl.cookie.BasicClientCookie;

/**
 *
 * @author indiketa
 */
public class DSession {

    @Expose
    protected DConfig config;
    @Expose
    protected DClient client;
    @Expose
    protected DVwdSession vwdSession;
    @Expose
    protected List<BasicClientCookie> cookies;

    public DConfig getConfig() {
        return config;
    }

    public DClient getClient() {
        return client;
    }

    public DVwdSession getVwdSession() {
        return vwdSession;
    }

    public List<BasicClientCookie> getCookies() {
        return cookies;
    }

    public String getJSessionId() {
        String value = null;

        if (cookies != null) {
            int i = 0;
            while (i < cookies.size() && !cookies.get(i).getName().equalsIgnoreCase("JSESSIONID")) {
                i++;
            }

            if (i < cookies.size()) {
                value = cookies.get(i).getValue();
            }
        }

        return value;
    }

    public void setConfig(DConfig config) {
        this.config = config;
    }

    public void setClient(DClient client) {
        this.client = client;
    }

    public void setVwdSession(DVwdSession vwdSession) {
        this.vwdSession = vwdSession;
    }

    public void setCookies(List<BasicClientCookie> cookies) {
        this.cookies = cookies;
    }

}
