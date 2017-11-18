/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.eduard.degiro.http;

import cat.eduard.degiro.log.Log;
import java.util.concurrent.TimeUnit;
import org.apache.http.conn.HttpClientConnectionManager;

/**
 *
 * @author casa
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
                    Log.CONNECTOR.error(e);
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
