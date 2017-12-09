package cat.indiketa.degiro;

import cat.indiketa.degiro.utils.DUtils;
import cat.indiketa.degiro.utils.DCredentials;
import cat.indiketa.degiro.session.DSession;
import cat.indiketa.degiro.exceptions.DUnauthorizedException;
import cat.indiketa.degiro.exceptions.DeGiroException;
import cat.indiketa.degiro.exceptions.DInvalidCredentialsException;
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
import cat.indiketa.degiro.model.DNewOrder;
import cat.indiketa.degiro.model.DOrderConfirmation;
import cat.indiketa.degiro.model.DOrderTime;
import cat.indiketa.degiro.model.DOrderType;
import cat.indiketa.degiro.model.DPlacedOrder;
import cat.indiketa.degiro.model.DPrice;
import cat.indiketa.degiro.model.DPriceListener;
import cat.indiketa.degiro.model.DProductSearch;
import cat.indiketa.degiro.model.DProductType;
import cat.indiketa.degiro.model.DProducts;
import cat.indiketa.degiro.model.DTransactions;
import cat.indiketa.degiro.model.raw.DRawCashFunds;
import cat.indiketa.degiro.model.raw.DRawOrders;
import cat.indiketa.degiro.model.raw.DRawPortfolio;
import cat.indiketa.degiro.model.raw.DRawTransactions;
import cat.indiketa.degiro.model.raw.DRawVwdPrice;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 *
 * @author indiketa
 */
public class DeGiroImpl implements DeGiro {

    private final DCredentials credentials;
    private final DCommunication comm;
    private final DSession session;
    private final Gson gson;
    private DPriceListener priceListener;
    private long pollingInterval = TimeUnit.SECONDS.toMillis(15);
    private Timer pricePoller = null;
    private static final String BASE_TRADER_URL = "https://trader.degiro.nl";
    private final Set<Long> subscribedVwdIssues;
    private final Type rawPriceData = new TypeToken<List<DRawVwdPrice>>() {
    }.getType();

    protected DeGiroImpl(DCredentials credentials, DSession session) {
        this.session = session;
        this.credentials = credentials;
        this.comm = new DCommunication(this.session);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(DProductType.class, new DUtils.ProductTypeAdapter());
        builder.registerTypeAdapter(DOrderTime.class, new DUtils.OrderTimeTypeAdapter());
        builder.registerTypeAdapter(DOrderType.class, new DUtils.OrderTypeTypeAdapter());
        builder.registerTypeAdapter(Date.class, new DUtils.DateTypeAdapter());
        this.gson = builder.create();
        this.subscribedVwdIssues = new HashSet<>(500);

    }

    @Override
    public DPortfolio getPortfolio() throws DeGiroException {

        DPortfolio portfolio = null;
        ensureLogged();

        try {
            DResponse response = comm.getData(session, "portfolio=0", null);
            DRawPortfolio rawPortfolio = gson.fromJson(getResponseData(response), DRawPortfolio.class);
            portfolio = DUtils.convert(rawPortfolio);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving portfolio", e);
        }
        return portfolio;
    }

    @Override
    public DCashFunds getCashFunds() throws DeGiroException {

        DCashFunds cashFunds = null;
        ensureLogged();

        try {
            DResponse response = comm.getData(session, "cashFunds=0", null);
            DRawCashFunds rawCashFunds = gson.fromJson(getResponseData(response), DRawCashFunds.class);
            cashFunds = DUtils.convert(rawCashFunds);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving cash funds", e);
        }
        return cashFunds;
    }

    @Override
    public DOrders getOrders() throws DeGiroException {

        DOrders orders = null;
        ensureLogged();

        try {
            DResponse response = comm.getData(session, "orders=0", null);
            DRawOrders rawOrders = gson.fromJson(getResponseData(response), DRawOrders.class);
            orders = DUtils.convert(rawOrders);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving orders", e);
        }
        return orders;
    }

    @Override
    public DLastTransactions getLastTransactions() throws DeGiroException {

        DLastTransactions transactions = null;
        ensureLogged();

        try {
            DResponse response = comm.getData(session, "transactions=0", null);
            DRawTransactions rawTransactions = gson.fromJson(getResponseData(response), DRawTransactions.class);
            transactions = DUtils.convert(rawTransactions);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving transactions", e);
        }
        return transactions;
    }

    @Override
    public DTransactions getTransactions(Calendar from, Calendar to) throws DeGiroException {

        DTransactions transactions = null;
        ensureLogged();

        try {
            String fromStr = from.get(Calendar.DATE) + "%2F" + (from.get(Calendar.MONTH) + 1) + "%2F" + from.get(Calendar.YEAR);
            String toStr = to.get(Calendar.DATE) + "%2F" + (to.get(Calendar.MONTH) + 1) + "%2F" + to.get(Calendar.YEAR);

            DResponse response = comm.getUrlData(session.getConfig().getReportingUrl(), "v4/transactions?orderId=&product=&fromDate=" + fromStr + "&toDate=" + toStr + "&groupTransactionsByOrder=false&intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), null);
            transactions = gson.fromJson(getResponseData(response), DTransactions.class);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving transactions", e);
        }
        return transactions;

    }

    private void ensureLogged() throws DeGiroException {
        if (Strings.isNullOrEmpty(session.getJSessionId())) {
            login();
        }
    }

    private void login() throws DeGiroException {

        try {
            DLogin login = new DLogin();
            login.setUsername(credentials.getUsername());
            login.setPassword(credentials.getPassword());

            DResponse response = comm.getUrlData(BASE_TRADER_URL, "/login/secure/login", login);

            if (response.getStatus() != 200) {
                if (response.getStatus() == 400) {
                    throw new DInvalidCredentialsException();
                } else {
                    throw new DeGiroException("Bad login HTTP status " + response.getStatus());
                }
            }

            response = comm.getUrlData(BASE_TRADER_URL, "/login/secure/config", null);
            session.setConfig(gson.fromJson(getResponseData(response), DConfig.class));

            response = comm.getUrlData(session.getConfig().getPaUrl(), "client?sessionId=" + session.getJSessionId(), null);
            session.setClient(gson.fromJson(getResponseData(response), DClient.class));

        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving user information", e);
        }

    }

    private void ensureVwdSession() throws DeGiroException {
        ensureLogged();
        if (session.getVwdSession() == null) {
            getVwdSession();
            if (!subscribedVwdIssues.isEmpty()) {
                subscribeToPrice(subscribedVwdIssues);
            }

        }
    }

    private void getVwdSession() throws DeGiroException {

        try {
            List<Header> headers = new ArrayList<>(1);
            headers.add(new BasicHeader("Origin", session.getConfig().getTradingUrl()));
            HashMap<String, String> data = new HashMap();
            data.put("referrer", "https://trader.degiro.nl");
            DResponse response = comm.getUrlData("https://degiro.quotecast.vwdservices.com/CORS", "/request_session?version=1.0.20170315&userToken=" + session.getClient().getId(), data, headers);
            HashMap map = gson.fromJson(getResponseData(response), HashMap.class);
            session.setVwdSession((String) map.get("sessionId"));
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving vwd session", e);
        }
    }

    @Override
    public void setPricePollingInterval(int duration, TimeUnit unit) throws DeGiroException {
        if (pricePoller != null) {
            throw new DeGiroException("Price polling interval must be set before adding price watches");
        }
        pollingInterval = unit.toMillis(duration);
    }

    @Override
    public void setPriceListener(DPriceListener priceListener) {
        this.priceListener = priceListener;
    }

    @Override
    public synchronized void subscribeToPrice(Collection<Long> vwdIssueId) throws DeGiroException {
        ensureVwdSession();

        if (priceListener == null) {
            throw new DeGiroException("PriceListener not set");
        }

        try {
            List<Header> headers = new ArrayList<>(1);
            headers.add(new BasicHeader("Origin", session.getConfig().getTradingUrl()));

            String requestedIssues = "";
            for (Long issueId : vwdIssueId) {
                requestedIssues += "req(XXX.BidPrice);req(XXX.AskPrice);req(XXX.LastPrice);req(XXX.LastTime);".replace("XXX", issueId + "");
            }

            if (vwdIssueId.hashCode() != subscribedVwdIssues.hashCode()) {
                subscribedVwdIssues.addAll(vwdIssueId);
            }

            HashMap<String, String> data = new HashMap();
            data.put("controlData", requestedIssues);

            DResponse response = comm.getUrlData("https://degiro.quotecast.vwdservices.com/CORS", "/" + session.getVwdSession(), data, headers);
            getResponseData(response);

            DLog.MANAGER.info("Subscribed successfully for issues " + Joiner.on(", ").join(vwdIssueId));

        } catch (IOException e) {
            throw new DeGiroException("IOException while subscribing to issues", e);
        }

        if (pricePoller == null) {
            pricePoller = new Timer("PRICE-POLLER", true);
            pricePoller.scheduleAtFixedRate(new DPriceTimerTask(), 0, pollingInterval);
        }

    }

    private void checkPriceChanges() throws DeGiroException {
        ensureVwdSession();

        try {
            List<Header> headers = new ArrayList<>(1);
            headers.add(new BasicHeader("Origin", session.getConfig().getTradingUrl()));

            DResponse response = comm.getUrlData("https://degiro.quotecast.vwdservices.com/CORS", "/" + session.getVwdSession(), null, headers);
            List<DRawVwdPrice> data = gson.fromJson(getResponseData(response), rawPriceData);

            List<DPrice> prices = DUtils.convert(data);

            if (priceListener != null) {
                for (DPrice price : prices) {
                    priceListener.priceChanged(price);
                }
            }

        } catch (IOException e) {
            throw new DeGiroException("IOException while subscribing to issues", e);
        }

        if (pricePoller == null) {
            pricePoller = new Timer("Prices", true);
            pricePoller.scheduleAtFixedRate(new DPriceTimerTask(), 0, pollingInterval);
        }
    }

    @Override
    public synchronized void clearPriceWatchs() {
        session.setVwdSession(null);
        subscribedVwdIssues.clear();
        pricePoller.cancel();
        pricePoller = null;
    }

    @Override
    public DProducts getProducts(List<String> productIds) throws DeGiroException {

        DProducts products = null;

        ensureLogged();
        try {
            List<Header> headers = new ArrayList<>(1);
            DResponse response = comm.getUrlData(session.getConfig().getProductSearchUrl(), "v5/products/info?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), productIds, headers);
            products = gson.fromJson(getResponseData(response), DProducts.class);

        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving product information", e);
        }

        return products;
    }

    @Override
    public DProductSearch searchProducts(String text, DProductType type, int limit, int offset) throws DeGiroException {

        if (Strings.isNullOrEmpty(text)) {
            throw new DeGiroException("Nothing to search");
        }

        DProductSearch productSearch = null;

        ensureLogged();
        try {

            String qs = "&searchText=" + text;

            if (type != null && type.getTypeCode() != 0) {
                qs += "&productTypeId=" + type.getTypeCode();
            }
            qs += "&limit=" + limit;
            if (offset > 0) {
                qs += "&offset=" + offset;
            }

            DResponse response = comm.getUrlData(session.getConfig().getProductSearchUrl(), "v5/products/lookup?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId() + qs, null);
            productSearch = gson.fromJson(getResponseData(response), DProductSearch.class);

        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving product information", e);
        }

        return productSearch;
    }

    @Override
    public DOrderConfirmation checkOrder(DNewOrder order) throws DeGiroException {

        if (order == null) {
            throw new DeGiroException("Order was null (no order to check)");
        }

        DOrderConfirmation orderConfirmation = null;
        ensureLogged();
        try {
            DResponse response = comm.getUrlData(session.getConfig().getTradingUrl(), "v5/checkOrder;jsessionid=" + session.getJSessionId() + "?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), orderToMap(order));
            orderConfirmation = gson.fromJson(getResponseData(response), DOrderConfirmation.class);
        } catch (IOException e) {
            throw new DeGiroException("IOException while checking order", e);
        }

        return orderConfirmation;
    }

    @Override
    public DPlacedOrder confirmOrder(DNewOrder order, String confirmationId) throws DeGiroException {

        if (order == null) {
            throw new DeGiroException("Order was null (no order to check)");
        }

        if (Strings.isNullOrEmpty(confirmationId)) {
            throw new DeGiroException("ConfirmationId was empty");
        }

        DPlacedOrder placedOrder = null;

        ensureLogged();
        try {
            DResponse response = comm.getUrlData(session.getConfig().getTradingUrl(), "v5/order/" + confirmationId + ";jsessionid=" + session.getJSessionId() + "?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), orderToMap(order));
            placedOrder = gson.fromJson(getResponseData(response), DPlacedOrder.class);
        } catch (IOException e) {
            throw new DeGiroException("IOException while checking order", e);
        }

        return placedOrder;

    }

    @Override
    public DPlacedOrder deleteOrder(String orderId) throws DeGiroException {

        if (Strings.isNullOrEmpty(orderId)) {
            throw new DeGiroException("orderId was empty");
        }

        DPlacedOrder placedOrder = null;

        ensureLogged();
        try {

            DResponse response = comm.getUrlData(session.getConfig().getTradingUrl(), "v5/order/" + orderId + ";jsessionid=" + session.getJSessionId() + "?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), null, null, true);
            placedOrder = gson.fromJson(getResponseData(response), DPlacedOrder.class);
        } catch (IOException e) {
            throw new DeGiroException("IOException while checking order", e);
        }

        return placedOrder;

    }

    private Map orderToMap(DNewOrder order) {
        Map degiroOrder = new HashMap();
        degiroOrder.put("buysell", order.getAction().getValue());
        degiroOrder.put("orderType", order.getOrderType().getValue());
        degiroOrder.put("productId", order.getProductId());
        degiroOrder.put("size", order.getSize());
        degiroOrder.put("timeType", order.getTimeType().getValue());
        if (order.getLimitPrice() != null) {
            degiroOrder.put("price", order.getLimitPrice().toPlainString());
        }
        if (order.getStopPrice() != null) {
            degiroOrder.put("stopPrice", order.getStopPrice().toPlainString());
        }
        return degiroOrder;

    }

    private String getResponseData(DResponse response) throws DeGiroException {

        DLog.WIRE.info(response.getMethod() + " " + response.getUrl() + " >> HTTP " + response.getStatus());
        String data = null;

        if (response.getStatus() == 401) {
            DLog.MANAGER.warn("Session expired, clearing session tokens");
            session.clearSession();
            throw new DUnauthorizedException();
        }

        if (response.getStatus() == 200 || response.getStatus() == 201) {
            data = response.getText();
        } else {
            throw new DeGiroException("Unexpected HTTP Status " + response.getStatus() + ": " + response.getMethod() + " " + response.getUrl());
        }

        return data;

    }

    public DSession getSession() {
        return session;
    }

    private class DPriceTimerTask extends TimerTask {

        @Override
        public void run() {
            try {
                DeGiroImpl.this.checkPriceChanges();
            } catch (Exception e) {
                DLog.MANAGER.error("Exception while updating prices", e);
            }
        }

    }

}
