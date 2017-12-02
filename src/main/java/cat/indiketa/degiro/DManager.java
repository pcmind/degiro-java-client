package cat.indiketa.degiro;

import cat.indiketa.degiro.http.DCommunication;
import cat.indiketa.degiro.http.DCommunication.DResponse;
import cat.indiketa.degiro.http.DWebsocket;
import cat.indiketa.degiro.model.DCashFunds;
import cat.indiketa.degiro.model.DClient;
import cat.indiketa.degiro.model.DConfig;
import cat.indiketa.degiro.model.DLogin;
import cat.indiketa.degiro.model.DOrders;
import cat.indiketa.degiro.model.DPortfolio;
import cat.indiketa.degiro.model.DLastTransactions;
import cat.indiketa.degiro.model.DProducts;
import cat.indiketa.degiro.model.DTransactions;
import cat.indiketa.degiro.model.DVwdSession;
import cat.indiketa.degiro.model.raw.DRawCashFunds;
import cat.indiketa.degiro.model.raw.DRawOrders;
import cat.indiketa.degiro.model.raw.DRawPortfolio;
import cat.indiketa.degiro.model.raw.DRawTransactions;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 *
 * @author indiketa
 */
public class DManager {

    private final DCredentials credentials;
    private final DCommunication comm;
    private DConfig config;
    private DClient client;
    private DVwdSession vwdSession;

    private final Gson gson;
    private static final String BASE_TRADER_URL = "https://trader.degiro.nl";

    public DManager(DCredentials credencials) {
        this.credentials = credencials;
        this.comm = new DCommunication();
        this.gson = new Gson();

    }

    public DPortfolio getPortfolio() throws DegiroException {

        DPortfolio portfolio = null;
        ensureLogged();

        try {

            DResponse response = comm.getData(client, config, "portfolio=0", null);

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

            DResponse response = comm.getData(client, config, "cashFunds=0", null);

            if (response.getStatus() != 200 && response.getStatus() != 201) {
                throw new DegiroException("Bad cash funds HTTP status " + response.getStatus());
            }

            System.out.println(response.getText());

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

            DResponse response = comm.getData(client, config, "orders=0", null);

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

            DResponse response = comm.getData(client, config, "transactions=0", null);

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

            DResponse response = comm.getUrlData(config.getReportingUrl(), "v4/transactions?orderId=&product=&fromDate=" + fromStr + "&toDate=" + toStr + "&groupTransactionsByOrder=false&intAccount=" + client.getIntAccount() + "&sessionId=" + comm.getJSessionId(), null);

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
        if (Strings.isNullOrEmpty(comm.getJSessionId())) {
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
                System.err.println(response.getText());
                config = gson.fromJson(response.getText(), DConfig.class);
            }

            response = comm.getUrlData(config.getPaUrl(), "client?sessionId=" + comm.getJSessionId(), null);

            if (response.getStatus() != 200) {
                throw new DegiroException("Bad client info HTTP status " + response.getStatus());
            } else {
                client = gson.fromJson(response.getText(), DClient.class);
            }

        } catch (IOException e) {
            throw new DegiroException("IOException while retrieving user information", e);
        }

    }

    private void ensureVwdSession() throws DegiroException {

        if (vwdSession == null) {
            renewVwdSession();

        }

    }

    private void renewVwdSession() throws DegiroException {
        ensureLogged();
        try {
            List<Header> headers = new ArrayList<>(1);
            headers.add(new BasicHeader("Origin", config.getTradingUrl()));
            System.out.println(config.getVwdQuotecastServiceUrl());
            DResponse response = comm.getUrlData(config.getVwdQuotecastServiceUrl(), "info", null);
            if (response.getStatus() != 200) {
                throw new DegiroException("Bad vwd HTTP status " + response.getStatus());
            } else {
                System.out.println(response.getText());
            }
            DWebsocket ws = new DWebsocket(new URI(config.getVwdQuotecastServiceUrl().replace("https", "wss")));

        } catch (IOException | URISyntaxException e) {
            throw new DegiroException("IOException while retrieving vwd session", e);
        }
    }

    public void getPrice() throws DegiroException {
        ensureVwdSession();
    }

    public DProducts getProducts(List<String> productIds) throws DegiroException {

        DProducts products = null;

        ensureLogged();
        try {
            List<Header> headers = new ArrayList<>(1);
//            headers.add(new BasicHeader("Content-Type", "application/json"));

            DResponse response = comm.getUrlData(config.getProductSearchUrl(), "v5/products/info?intAccount=" + client.getIntAccount() + "&sessionId=" + comm.getJSessionId(), productIds, headers);
            if (response.getStatus() != 200) {
                throw new DegiroException("Bad product information HTTP status " + response.getStatus());
            } else {
                products = gson.fromJson(response.getText(), DProducts.class);
            }

        } catch (IOException e) {
            throw new DegiroException("IOException while retrieving product information", e);
        }

        return products;
    }

    /*
 
    const requestVwdSession = () => {
        return fetch(
            `https://degiro.quotecast.vwdservices.com/CORS/request_session?version=1.0.20170315&userToken=${session.userToken}`,
            {
                method: 'POST',
                headers: {Origin: 'https://trader.degiro.nl'},
                body: JSON.stringify({referrer: 'https://trader.degiro.nl'}),
            }
        ).then(res => res.json());
    };


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
