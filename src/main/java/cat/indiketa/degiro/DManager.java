package cat.indiketa.degiro;

import cat.indiketa.degiro.http.DCommunication;
import cat.indiketa.degiro.http.DCommunication.DResponse;
import cat.indiketa.degiro.log.DLog;
import cat.indiketa.degiro.model.DCashFunds;
import cat.indiketa.degiro.model.DClient;
import cat.indiketa.degiro.model.DConfig;
import cat.indiketa.degiro.model.DLogin;
import cat.indiketa.degiro.model.DOrders;
import cat.indiketa.degiro.model.DPortfolio;
import cat.indiketa.degiro.model.DLastTransactions;
import cat.indiketa.degiro.model.DPriceListener;
import cat.indiketa.degiro.model.DProducts;
import cat.indiketa.degiro.model.DTransactions;
import cat.indiketa.degiro.model.raw.DRawCashFunds;
import cat.indiketa.degiro.model.raw.DRawOrders;
import cat.indiketa.degiro.model.raw.DRawPortfolio;
import cat.indiketa.degiro.model.raw.DRawTransactions;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 *
 * @author indiketa
 */
public class DManager {

    private final DCredentials credentials;
    private final DCommunication comm;
    private final DSession session;
    private final Gson gson;
    private DPriceListener priceListener;
    private long pollingIntervalMillis = TimeUnit.SECONDS.toMillis(15);
    private Timer pricePoller = null;
    private static final String BASE_TRADER_URL = "https://trader.degiro.nl";

    public DManager(DCredentials credentials) {
        this(credentials, new DSession());
    }

    public DManager(DCredentials credentials, DSession session) {
        this.session = session;
        this.credentials = credentials;
        this.comm = new DCommunication(this.session);
        this.gson = new Gson();

    }

    public DPortfolio getPortfolio() throws DegiroException {

        DPortfolio portfolio = null;
        ensureLogged();

        try {

            DResponse response = comm.getData(session, "portfolio=0", null);

            if (response.getStatus() != 200 && response.getStatus() != 201) {
                throw new DegiroException("Bad portfolio HTTP status " + response.getStatus());
            }

            DRawPortfolio rawPortfolio = gson.fromJson(response.getText(), DRawPortfolio.class);
            portfolio = DUtils.convert(rawPortfolio);

        } catch (IOException e) {
            throw new DegiroException("IOException while retrieving portfolio", e);
        }
        return portfolio;
    }

    public DCashFunds getCashFunds() throws DegiroException {

        DCashFunds cashFunds = null;
        ensureLogged();

        try {

            DResponse response = comm.getData(session, "cashFunds=0", null);

            if (response.getStatus() != 200 && response.getStatus() != 201) {
                throw new DegiroException("Bad cash funds HTTP status " + response.getStatus());
            }

            DRawCashFunds rawCashFunds = gson.fromJson(response.getText(), DRawCashFunds.class);
            cashFunds = DUtils.convert(rawCashFunds);

        } catch (IOException e) {
            throw new DegiroException("IOException while retrieving cash funds", e);
        }
        return cashFunds;
    }

    public DOrders getOrders() throws DegiroException {

        DOrders orders = null;
        ensureLogged();

        try {

            DResponse response = comm.getData(session, "orders=0", null);

            if (response.getStatus() != 200 && response.getStatus() != 201) {
                throw new DegiroException("Bad orders HTTP status " + response.getStatus());
            }

            DRawOrders rawOrders = gson.fromJson(response.getText(), DRawOrders.class);
            orders = DUtils.convert(rawOrders);

        } catch (IOException e) {
            throw new DegiroException("IOException while retrieving orders", e);
        }
        return orders;
    }

    public DLastTransactions getLastTransactions() throws DegiroException {

        DLastTransactions transactions = null;
        ensureLogged();

        try {

            DResponse response = comm.getData(session, "transactions=0", null);

            if (response.getStatus() != 200 && response.getStatus() != 201) {
                throw new DegiroException("Bad transactions HTTP status " + response.getStatus());
            }

            DRawTransactions rawTransactions = gson.fromJson(response.getText(), DRawTransactions.class);
            transactions = DUtils.convert(rawTransactions);

        } catch (IOException e) {
            throw new DegiroException("IOException while retrieving transactions", e);
        }
        return transactions;
    }

    public DTransactions getTransactions(Calendar from, Calendar to) throws DegiroException {

        DTransactions transactions = null;

        ensureLogged();
        try {
            String fromStr = from.get(Calendar.DATE) + "%2F" + (from.get(Calendar.MONTH) + 1) + "%2F" + from.get(Calendar.YEAR);
            String toStr = to.get(Calendar.DATE) + "%2F" + (to.get(Calendar.MONTH) + 1) + "%2F" + to.get(Calendar.YEAR);

            DResponse response = comm.getUrlData(session.getConfig().getReportingUrl(), "v4/transactions?orderId=&product=&fromDate=" + fromStr + "&toDate=" + toStr + "&groupTransactionsByOrder=false&intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), null);

            if (response.getStatus() != 200) {
                throw new DegiroException("Bad getTransactions HTTP status " + response.getStatus());
            } else {
                transactions = gson.fromJson(response.getText(), DTransactions.class);
            }
        } catch (IOException e) {
            throw new DegiroException("IOException while retrieving transactions", e);
        }

        return transactions;

    }

    private void ensureLogged() throws DegiroException {
        if (Strings.isNullOrEmpty(session.getJSessionId())) {
            login();
        }
    }

    private void login() throws DegiroException {

        try {
            DLogin login = new DLogin();
            login.setUsername(credentials.getUsername());
            login.setPassword(credentials.getPassword());

            DResponse response = comm.getUrlData(BASE_TRADER_URL, "/login/secure/login", login);

            if (response.getStatus() != 200) {
                if (response.getStatus() == 400) {
                    throw new DInvalidCredentialsException();
                } else {
                    throw new DegiroException("Bad login HTTP status " + response.getStatus());
                }
            }

            response = comm.getUrlData(BASE_TRADER_URL, "/login/secure/config", null);

            if (response.getStatus() != 200) {
                throw new DegiroException("Bad config HTTP status " + response.getStatus());
            } else {
                session.setConfig(gson.fromJson(response.getText(), DConfig.class));
            }

            response = comm.getUrlData(session.getConfig().getPaUrl(), "client?sessionId=" + session.getJSessionId(), null);

            if (response.getStatus() != 200) {
                throw new DegiroException("Bad client info HTTP status " + response.getStatus());
            } else {
                session.setClient(gson.fromJson(response.getText(), DClient.class));
            }

        } catch (IOException e) {
            throw new DegiroException("IOException while retrieving user information", e);
        }

    }

    private void ensureVwdSession() throws DegiroException {
        ensureLogged();
        if (session.getVwdSession() == null) {
            getVwdSession();
        }
    }

    private void getVwdSession() throws DegiroException {

        try {
            List<Header> headers = new ArrayList<>(1);
            headers.add(new BasicHeader("Origin", session.getConfig().getTradingUrl()));
            HashMap<String, String> data = new HashMap();
            data.put("referrer", "https://trader.degiro.nl");
            DResponse response = comm.getUrlData("https://degiro.quotecast.vwdservices.com/CORS", "/request_session?version=1.0.20170315&userToken=" + session.getClient().getId(), data, headers);
            if (response.getStatus() != 200) {
                throw new DegiroException("Bad vwd get session HTTP status " + response.getStatus());
            } else {
                HashMap map = gson.fromJson(response.getText(), HashMap.class);
                session.setVwdSession((String) map.get("sessionId"));
            }

        } catch (IOException e) {
            throw new DegiroException("IOException while retrieving vwd session", e);
        }
    }

    public void setPricePollingInterval(int duration, TimeUnit unit) throws DegiroException {
        if (pricePoller != null) {
            throw new DegiroException("Price polling interval must be set before adding price watches");
        }
        pollingIntervalMillis = unit.toMillis(duration);
    }

    public void setPriceListener(DPriceListener priceListener) {
        this.priceListener = priceListener;
    }

    public synchronized void subscribeToPrice(List<Long> vwdIssueId) throws DegiroException {
        ensureVwdSession();

        if (priceListener == null) {
            throw new DegiroException("PriceListener not set");
        }

        try {
            List<Header> headers = new ArrayList<>(1);
            headers.add(new BasicHeader("Origin", session.getConfig().getTradingUrl()));

            String requestedIssues = "";
            for (Long issueId : vwdIssueId) {
                requestedIssues += "req(XXX.BidPrice);req(XXX.AskPrice);req(XXX.LastPrice);req(XXX.LastTime);".replace("XXX", issueId + "");
            }
            HashMap<String, String> data = new HashMap();
            data.put("controlData", requestedIssues);

            DResponse response = comm.getUrlData("https://degiro.quotecast.vwdservices.com/CORS", "/" + session.getVwdSession(), data, headers);
            if (response.getStatus() != 200) {
                throw new DegiroException("Bad http status " + response.getStatus() + ", inserting watch for issues " + Joiner.on(", ").join(vwdIssueId));
            } else {
                DLog.MANAGER.info("Subscribed successfully for issues " + Joiner.on(", ").join(vwdIssueId));
            }

        } catch (IOException e) {
            throw new DegiroException("IOException while subscribing to issues", e);
        }

        if (pricePoller == null) {
            pricePoller = new Timer("PRICE-POLLER", true);
            pricePoller.scheduleAtFixedRate(new DPriceTimerTask(), 0, pollingIntervalMillis);
        }

    }

    private void checkPriceChanges() {

    }

    public synchronized void clearPriceWatchs() {
        session.setVwdSession(null);
        pricePoller.cancel();
        pricePoller = null;
    }

    public DProducts getProducts(List<String> productIds) throws DegiroException {

        DProducts products = null;

        ensureLogged();
        try {
            List<Header> headers = new ArrayList<>(1);
            DResponse response = comm.getUrlData(session.getConfig().getProductSearchUrl(), "v5/products/info?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), productIds, headers);
            if (response.getStatus() != 200) {
                throw new DegiroException("Bad product information HTTP status " + response.getStatus());

            } else {
                products = gson.fromJson(response.getText(), DProducts.class
                );
            }

        } catch (IOException e) {
            throw new DegiroException("IOException while retrieving product information", e);
        }

        return products;
    }

    private class DPriceTimerTask extends TimerTask {

        @Override
        public void run() {
            try {
                DManager.this.checkPriceChanges();
            } catch (Exception e) {
                DLog.MANAGER.error("Exception while updating prices", e);
            }
        }

    }


    /*


    const getAskBidPrice = (issueId, timesChecked = 0) =>
        requestVwdSession().then(vwdSession => {
            const checkData = res => {
                timesChecked++;
                const prices = {};

                //sanity check
                if (!Array.isArray(res)) {
                    throw Error('Bad result: ' + JSON.stringify(res));
                }

                //retry needed?
                if (res.length == 1 && res[0].m == 'h') {
                    if (timesChecked <= 3) {
                        return getAskBidPrice(issueId, timesChecked);
                    } else {
                        throw Error('Tried 3 times to get data, but nothing was returned: ' + JSON.stringify(res));
                    }
                }

                //process incoming data
                var keys = [];
                res.forEach(row => {
                    if (row.m == 'a_req') {
                        if (row.v[0].startsWith(issueId)) {
                            var key = lcFirst(row.v[0].slice(issueId.length + 1));
                            prices[key] = null;
                            keys[row.v[1]] = key;
                        }
                    } else if (row.m == 'un' || row.m == 'us') {
                        prices[keys[row.v[0]]] = row.v[1];
                    }
                });

                //check if everything is there
                if (
                    typeof prices.bidPrice == 'undefined' ||
                    typeof prices.askPrice == 'undefined' ||
                    typeof prices.lastPrice == 'undefined' ||
                    typeof prices.lastTime == 'undefined'
                ) {
                    throw Error("Couldn't find all requested info: " + JSON.stringify(res));
                }

                return prices;
            };

            return fetch(`https://degiro.quotecast.vwdservices.com/CORS/${vwdSession.sessionId}`, {
                method: 'POST',
                headers: {Origin: 'https://trader.degiro.nl'},
                body: JSON.stringify({
                    controlData: `req(${issueId}.BidPrice);req(${issueId}.AskPrice);req(${issueId}.LastPrice);req(${issueId}.LastTime);`,
                }),
            })
                .then(() => fetch(`https://degiro.quotecast.vwdservices.com/CORS/${vwdSession.sessionId}`))
                .then(res => res.json())
                .then(checkData);
        });

     */
}
