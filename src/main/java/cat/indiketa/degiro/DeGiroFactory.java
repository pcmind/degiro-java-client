package cat.indiketa.degiro;

import cat.indiketa.degiro.http.IDCommunication;
import cat.indiketa.degiro.http.impl.DCommunication;
import cat.indiketa.degiro.http.impl.DCookieStore;
import cat.indiketa.degiro.session.DSession;
import cat.indiketa.degiro.session.DSessionExpiredRetryProxy;
import cat.indiketa.degiro.utils.DCredentials;

/**
 * @author indiketa
 */
public class DeGiroFactory {

    public static DeGiro newInstance(DCredentials credentials) {
        return newInstance(credentials, new DSession());
    }

    public static DeGiro newInstance(DeGiroHost degiroHost, DCredentials credentials, DSession session) {
        final IDCommunication comm = DCommunication.create(new DCookieStore(session));
        return newInstance(degiroHost, credentials, session, comm);
    }

    public static DeGiro newInstance(DeGiroHost degiroHost, DCredentials credentials, DSession session, IDCommunication comm) {
        DeGiroImpl dmanager = new DeGiroImpl(
                degiroHost,
                credentials,
                session,
                comm
        );
        return DSessionExpiredRetryProxy.newInstance(dmanager);
    }

    public static DeGiro newInstance(DCredentials credentials, DSession session) {
        return newInstance(DeGiroHost.defaultEndPoint(), credentials, session);
    }

    public static DeGiro newTestInstance(DCredentials credentials, DSession session) {
        return newInstance(DeGiroHost.testEndPoint(), credentials, session);
    }
}
