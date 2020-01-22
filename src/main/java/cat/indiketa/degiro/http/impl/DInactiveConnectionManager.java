package cat.indiketa.degiro.http.impl;

import java.util.concurrent.TimeUnit;

import org.apache.http.conn.HttpClientConnectionManager;

import cat.indiketa.degiro.log.DLog;

/**
 *
 * @author indiketa
 */
class DInactiveConnectionManager extends Thread {

    private final HttpClientConnectionManager connectionManager;
    private volatile boolean shutdown;

    public DInactiveConnectionManager(HttpClientConnectionManager connectionManager) {
        super("INACTIVE-CONNECTION-MANAGER");
        setDaemon(true);
        this.connectionManager = connectionManager;
    }

    @Override
    public void run() {
        while (!shutdown) {
            synchronized (this) {
                try {
                    wait(5000);
                    connectionManager.closeExpiredConnections();
                    connectionManager.closeIdleConnections(30, TimeUnit.SECONDS);
                } catch (Exception e) {
                    DLog.HTTP.error("Exception closing expired connections", e);
                }
            }
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }

}
