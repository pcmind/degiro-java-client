package cat.indiketa.degiro;

import cat.indiketa.degiro.exceptions.DInvalidCredentialsException;
import cat.indiketa.degiro.exceptions.DUnauthorizedException;
import cat.indiketa.degiro.exceptions.DeGiroException;
import cat.indiketa.degiro.http.DResponse;
import cat.indiketa.degiro.http.IDCommunication;
import cat.indiketa.degiro.log.DLog;
import cat.indiketa.degiro.model.DCashFunds;
import cat.indiketa.degiro.model.DClient;
import cat.indiketa.degiro.model.DConfig;
import cat.indiketa.degiro.model.DLastTransactions;
import cat.indiketa.degiro.model.DLogin;
import cat.indiketa.degiro.model.DNewOrder;
import cat.indiketa.degiro.model.DOrder;
import cat.indiketa.degiro.model.DOrderConfirmation;
import cat.indiketa.degiro.model.DOrders;
import cat.indiketa.degiro.model.DPlacedOrder;
import cat.indiketa.degiro.model.DPortfolioProducts;
import cat.indiketa.degiro.model.DPortfolioSummary;
import cat.indiketa.degiro.model.DPrice;
import cat.indiketa.degiro.model.DPriceHistory;
import cat.indiketa.degiro.model.DPriceListener;
import cat.indiketa.degiro.model.DProductDescriptions;
import cat.indiketa.degiro.model.DProductSearch;
import cat.indiketa.degiro.model.DProductType;
import cat.indiketa.degiro.model.DTransactions;
import cat.indiketa.degiro.model.raw.DRawCashFunds;
import cat.indiketa.degiro.model.raw.DRawOrders;
import cat.indiketa.degiro.model.raw.DRawPortfolio;
import cat.indiketa.degiro.model.raw.DRawPortfolioSummary;
import cat.indiketa.degiro.model.raw.DRawTransactions;
import cat.indiketa.degiro.model.raw.DRawVwdPrice;
import cat.indiketa.degiro.session.DSession;
import cat.indiketa.degiro.utils.DCredentials;
import cat.indiketa.degiro.utils.DUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.gson.reflect.TypeToken;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author indiketa
 */
public class DeGiroImpl implements DeGiro {

    private final DeGiroHost degiro;
    private final DCredentials credentials;
    private final TrackConnection comm;
    private final DSession session;
    private final DJsonDecoder gson;
    private final Map<String, Long> subscribedVwdIssues;
    private final Type rawPriceData = new TypeToken<List<DRawVwdPrice>>() {
    }.getType();

    private DPriceListener priceListener;
    private long portfolioSummaryLastUpdate = 0;
    private long portfolioLastUpdate = 0;
    private String currency = "EUR";
    private long pollingInterval = TimeUnit.SECONDS.toMillis(5);
    private Timer pricePoller = null;

    public DeGiroImpl(DeGiroHost degiro, DCredentials credentials, DSession session, IDCommunication comm) {
        this.degiro = degiro;
        this.session = session;
        this.credentials = credentials;
        this.comm = new TrackConnection(comm);
        this.gson = new DJsonDecoder();
        this.subscribedVwdIssues = new HashMap<>(500);
    }

    @Override
    public DPortfolioProducts getPortfolio() throws DeGiroException {

        DPortfolioProducts portfolio = null;
        ensureLogged();

        try {
            DResponse response = getData("portfolio=" + portfolioLastUpdate, null);
            String data = getResponseData(response);
            DRawPortfolio rawPortfolio = gson.fromJson(data, DRawPortfolio.class);
            portfolioLastUpdate = rawPortfolio.getPortfolio().getLastUpdated();
            portfolio = DUtils.convert(rawPortfolio, currency);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving portfolio", e);
        }
        return portfolio;
    }

    @Override
    public DPortfolioSummary getPortfolioSummary() throws DeGiroException {

        DPortfolioSummary portfolioSummary = null;
        ensureLogged();

        try {
            DResponse response = getData("totalPortfolio=" + portfolioSummaryLastUpdate, null);
            String data = getResponseData(response);
            DRawPortfolioSummary rawPortfolioSummary = gson.fromJson(data, DRawPortfolioSummary.class);
            portfolioSummaryLastUpdate = rawPortfolioSummary.getTotalPortfolio().getLastUpdated();
            portfolioSummary = DUtils.convertPortfolioSummary(rawPortfolioSummary.getTotalPortfolio());
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving portfolio", e);
        }
        return portfolioSummary;
    }

    @Override
    public DCashFunds getCashFunds() throws DeGiroException {

        DCashFunds cashFunds = null;
        ensureLogged();

        try {
            DResponse response = getData("cashFunds=0", null);
            DRawCashFunds rawCashFunds = gson.fromJson(getResponseData(response), DRawCashFunds.class);
            cashFunds = DUtils.convert(rawCashFunds);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving cash funds", e);
        }
        return cashFunds;
    }

    @Override
    public List<DOrder> getOrders() throws DeGiroException {

        DOrders orders = null;
        ensureLogged();

        try {
            DResponse response = getData("orders=0", null);
            DRawOrders rawOrders = gson.fromJson(getResponseData(response), DRawOrders.class);
            orders = DUtils.convert(rawOrders);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving orders", e);
        }
        return orders.getOrders();
    }

    @Override
    public DLastTransactions getLastTransactions() throws DeGiroException {

        DLastTransactions transactions = null;
        ensureLogged();

        try {
            DResponse response = getData("transactions=0", null);
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

            DResponse response = comm.getUrlData(degiro.getBaseUrl(), "/login/secure/login", login);

            if (response.getStatus() != 200) {
                if (response.getStatus() == 400) {
                    throw new DInvalidCredentialsException();
                } else {
                    throw new DeGiroException("Bad login HTTP status " + response.getStatus());
                }
            }

            response = comm.getUrlData(degiro.getBaseUrl(), "/login/secure/config", null);
            session.setConfig(gson.fromJson(getResponseData(response), DConfig.class));

            response = comm.getUrlData(session.getConfig().getPaUrl(), "client?sessionId=" + session.getJSessionId(), null);
            session.setClient(gson.fromJson(getResponseData(response), DClient.class));

        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving user information", e);
        }

    }

    private void ensureVwdSession() throws DeGiroException {
        ensureLogged();
        if (session.getVwdSession() == null || session.getLastVwdSessionUsed() == 0 || (System.currentTimeMillis() - session.getLastVwdSessionUsed()) > TimeUnit.SECONDS.toMillis(15)) {
            DLog.DEGIRO.info("Renewing VWD session");
            getVwdSession();
            if (!subscribedVwdIssues.isEmpty()) {
                subscribeToPrice(subscribedVwdIssues.keySet());
            }

        }
    }

    private void getVwdSession() throws DeGiroException {

        try {
            List<Header> headers = new ArrayList<>(1);
            headers.add(new BasicHeader("Origin", session.getConfig().getTradingUrl()));
            HashMap<String, String> data = new HashMap();
            data.put("referrer", degiro.getBaseUrl());
            DResponse response = comm.getUrlData(degiro.getQuoteCastUrl(), "/request_session?version=1.0.20170315&userToken=" + session.getClient().getId(), data, headers);
            HashMap map = gson.fromJson(getResponseData(response), HashMap.class);
            session.setVwdSession((String) map.get("sessionId"));
            session.setLastVwdSessionUsed(System.currentTimeMillis());
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
    public synchronized void unsubscribeToPrice(String vwdIssueId) {
        subscribedVwdIssues.remove(vwdIssueId);
    }

    @Override
    public synchronized void subscribeToPrice(String vwdIssueId) throws DeGiroException {
        ArrayList<String> list = new ArrayList<>(1);
        list.add(vwdIssueId);
        subscribeToPrice(list);
    }

    @Override
    public synchronized void subscribeToPrice(Collection<String> vwdIssueId) throws DeGiroException {

        if (priceListener == null) {
            throw new DeGiroException("PriceListener not set");
        }

        try {

            for (String issueId : vwdIssueId) {
                if (!subscribedVwdIssues.containsKey(issueId)) {
                    subscribedVwdIssues.put(issueId, null);
                }
            }

            requestPriceUpdate();
            DLog.DEGIRO.info("Subscribed successfully for issues " + Joiner.on(", ").join(vwdIssueId));

        } catch (IOException e) {
            throw new DeGiroException("IOException while subscribing to issues", e);
        }

        if (pricePoller == null) {
            pricePoller = new Timer("PRICE-POLLER", true);
            pricePoller.scheduleAtFixedRate(new DPriceTimerTask(), 0, pollingInterval);
        }

    }

    private void requestPriceUpdate() throws DeGiroException, IOException {
        ensureVwdSession();
        List<Header> headers = new ArrayList<>(1);
        headers.add(new BasicHeader("Origin", session.getConfig().getTradingUrl()));

        HashMap<String, String> data = new HashMap();
        data.put("controlData", generatePriceRequestPayload());

        DResponse response = comm.getUrlData(degiro.getQuoteCastUrl(), "/" + session.getVwdSession(), data, headers);
        getResponseData(response);
        session.setLastVwdSessionUsed(System.currentTimeMillis());

    }

    private String generatePriceRequestPayload() {

        String requestedIssues = "";
        for (String issueId : subscribedVwdIssues.keySet()) {
            Long last = subscribedVwdIssues.get(issueId);
            if (last == null || last > pollingInterval) {
                requestedIssues += "req(X.BidPrice);req(X.AskPrice);req(X.LastPrice);req(X.LastTime);".replace("X", issueId + "");
            }
        }
        return requestedIssues;

    }

    private void checkPriceChanges() throws DeGiroException {
        ensureVwdSession();

        try {
            requestPriceUpdate();
            List<Header> headers = new ArrayList<>(1);
            headers.add(new BasicHeader("Origin", session.getConfig().getTradingUrl()));

            DResponse response = comm.getUrlData(degiro.getQuoteCastUrl(), "/" + session.getVwdSession(), null, headers);
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
    public synchronized void clearPriceSubscriptions() {
        session.setVwdSession(null);
        subscribedVwdIssues.clear();
        pricePoller.cancel();
        pricePoller = null;
    }

    @Override
    public DProductDescriptions getProducts(List<Long> productIds) throws DeGiroException {

        DProductDescriptions products = null;

        ensureLogged();
        try {
            List<Header> headers = new ArrayList<>(1);
            ArrayList<String> productIdStr = new ArrayList<>(productIds.size());
            for (Long productId : productIds) {
                productIdStr.add(productId + "");
            }
            DResponse response = comm.getUrlData(session.getConfig().getProductSearchUrl(), "v5/products/info?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), productIdStr, headers);
            products = gson.fromJson(getResponseData(response), DProductDescriptions.class);

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
            DResponse response = comm.getUrlData(session.getConfig().getTradingUrl(), "v5/order/" + orderId + ";jsessionid=" + session.getJSessionId() + "?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), null, null, "DELETE");
            placedOrder = gson.fromJson(getResponseData(response), DPlacedOrder.class);
        } catch (IOException e) {
            throw new DeGiroException("IOException while checking order", e);
        }

        return placedOrder;

    }

    @Override
    public DPlacedOrder updateOrder(DOrder order, BigDecimal limit, BigDecimal stop) throws DeGiroException {

        if (order == null) {
            throw new NullPointerException("Order was null");
        }

        DPlacedOrder placedOrder = null;

        ensureLogged();
        try {

            Map degiroOrder = new HashMap();
            degiroOrder.put("buysell", order.getBuysell().getValue());
            degiroOrder.put("orderType", order.getOrderType().getValue());
            degiroOrder.put("productId", order.getProductId());
            degiroOrder.put("size", order.getSize());
            degiroOrder.put("timeType", order.getOrderType().getValue());
            if (limit != null) {
                degiroOrder.put("price", limit.toPlainString());
            }
            if (stop != null) {
                degiroOrder.put("stopPrice", stop.toPlainString());
            }

            DResponse response = comm.getUrlData(session.getConfig().getTradingUrl(), "v5/order/" + order.getId() + ";jsessionid=" + session.getJSessionId() + "?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), degiroOrder, null, "PUT");
            placedOrder = gson.fromJson(getResponseData(response), DPlacedOrder.class);
        } catch (IOException e) {
            throw new DeGiroException("IOException while checking order", e);
        }

        return placedOrder;

    }

    @Override
    public DPriceHistory getPriceHistory(Long issueId) throws DeGiroException {

        DPriceHistory priceHistory = null;

        ensureLogged();
        try {

            DResponse response = getGraph("series=price%3Aissueid%3A" + issueId);
            priceHistory = gson.fromJson(getResponseData(response), DPriceHistory.class);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving price data", e);
        }

        return priceHistory;
    }

    private DResponse getGraph(String params) throws IOException {
                /*
requestid:  1
resolution: PT1S
culture:    en-US
period:     P1D
series:     issueid:280172443
format:     json
callback:   vwd.hchart.seriesRequestManager.sync_response
userToken:  91940
tz:         Europe/Madrid
         */
        return comm.getUrlData(degiro.getChartingUrl(), "?requestid=1&resolution=PT1S&culture=en-US&period=P1D&" + params + "&format=json&userToken=" + session.getConfig().getClientId() + "&tz=Europe%2FMadrid", null);
    }

    private DResponse getData(String params, Object data) throws IOException {
        return comm.getUrlData(session.getConfig().getTradingUrl() + "v5/update/" + session.getClient().getIntAccount() + ";jsessionid=" + session.getJSessionId(), "?" + params, data);
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

        DLog.HTTP.info(response.getMethod() + " " + response.getUrl() + " >> HTTP " + response.getStatus());
        String data = null;

        if (response.getStatus() == 401) {
            DLog.DEGIRO.warn("Session expired, clearing session tokens");
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    private class DPriceTimerTask extends TimerTask {

        @Override
        public void run() {
            try {
                DeGiroImpl.this.checkPriceChanges();
            } catch (Exception e) {
                DLog.DEGIRO.error("Exception while updating prices", e);
            }
        }

    }

    private static final class TrackConnection implements IDCommunication {
        private final IDCommunication delegate;
        private long lastSuccess = 0;

        public TrackConnection(IDCommunication delegate) {
            this.delegate = delegate;
        }

        @Override
        public DResponse getUrlData(String base, String uri, Object data, List<Header> headers, String method)
                throws IOException {
            final DResponse urlData = delegate.getUrlData(base, uri, data, headers, method);
            if (urlData != null && urlData.getStatus() == 200) {
                lastSuccess = System.nanoTime();
            }
            return urlData;
        }
    }

}
