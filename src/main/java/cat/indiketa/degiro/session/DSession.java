package cat.indiketa.degiro.session;

import cat.indiketa.degiro.model.DClient;
import cat.indiketa.degiro.model.DConfig;
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
    protected String vwdSession;
    @Expose
    protected long lastVwdSessionUsed;
    @Expose
    protected List<BasicClientCookie> cookies;

    public DConfig getConfig() {
        return config;
    }

    public DClient getClient() {
        return client;
    }

    public String getVwdSession() {
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

    public void clearSession() {
        config = null;
        client = null;
        vwdSession = null;
        cookies = null;
    }

    public void setConfig(DConfig config) {
        this.config = config;
    }

    public void setClient(DClient client) {
        this.client = client;
    }

    public void setVwdSession(String vwdSession) {
        this.vwdSession = vwdSession;
    }

    public void setCookies(List<BasicClientCookie> cookies) {
        this.cookies = cookies;
    }

    public long getLastVwdSessionUsed() {
        return lastVwdSessionUsed;
    }

    public void setLastVwdSessionUsed(long lastVwdSessionUsed) {
        this.lastVwdSessionUsed = lastVwdSessionUsed;
    }

}
