package cat.indiketa.degiro.http;

import cat.indiketa.degiro.DSession;
import java.util.List;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

/**
 *
 * @author indiketa
 */
public class DCookieStore extends BasicCookieStore {

    private final DSession permanentSession;

    public DCookieStore(DSession permanentSession) {
        this.permanentSession = permanentSession;

        if (this.permanentSession != null && this.permanentSession.getCookies() != null) {
            for (Cookie cookie : this.permanentSession.getCookies()) {
                BasicClientCookie bcc = new BasicClientCookie(cookie.getName(), cookie.getValue());
                bcc.setDomain(((BasicClientCookie)cookie).getDomain());
                bcc.setPath(((BasicClientCookie)cookie).getPath());
                bcc.setVersion(((BasicClientCookie)cookie).getVersion());
                super.addCookie(bcc);
            }
        }
    }

    @Override
    public synchronized void addCookie(Cookie cookie) {
        super.addCookie(cookie);
        if (permanentSession != null) {
            permanentSession.setCookies((List)getCookies());
        }
    }

}
