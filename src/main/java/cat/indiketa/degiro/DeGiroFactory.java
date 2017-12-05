package cat.indiketa.degiro;

import cat.indiketa.degiro.session.DSession;
import cat.indiketa.degiro.session.DSessionExpiredRetryProxy;
import cat.indiketa.degiro.utils.DCredentials;

/**
 *
 * @author indiketa
 */
public class DeGiroFactory {

    public static DeGiro newInstance(DCredentials credentials) {
        return newInstance(credentials, new DSession());
    }

    public static DeGiro newInstance(DCredentials credentials, DSession session) {
        DeGiroImpl dmanager = new DeGiroImpl(credentials, session);
        return DSessionExpiredRetryProxy.newInstance(dmanager);
    }

}
