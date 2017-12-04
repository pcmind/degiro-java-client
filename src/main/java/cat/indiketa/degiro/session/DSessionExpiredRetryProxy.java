package cat.indiketa.degiro.session;

import cat.indiketa.degiro.DeGiro;
import cat.indiketa.degiro.DeGiroImpl;
import cat.indiketa.degiro.exceptions.DUnauthorizedException;
import cat.indiketa.degiro.log.DLog;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *
 * @author indiketa
 */
public class DSessionExpiredRetryProxy implements InvocationHandler {

    private final DeGiroImpl degiro;

    public static DeGiro newInstance(DeGiroImpl manager) {
        return (DeGiro) Proxy.newProxyInstance(manager.getClass().getClassLoader(),
                manager.getClass().getInterfaces(),
                new DSessionExpiredRetryProxy(manager));
    }

    private DSessionExpiredRetryProxy(DeGiroImpl degiro) {
        this.degiro = degiro;
    }

    @Override
    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
        Object result;
        try {
            result = m.invoke(degiro, args);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof DUnauthorizedException) {
                try {
                    result = m.invoke(degiro, args);
                } catch (InvocationTargetException e2) {
                    throw e2.getTargetException();
                }
            } else {
                throw e.getTargetException();
            }
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException("Unexpected invocation exception: " + e.getMessage());
        }
        return result;
    }

}
