package cat.indiketa.degiro;

import cat.indiketa.degiro.exceptions.DInvalidCredentialsException;
import cat.indiketa.degiro.exceptions.DUnauthorizedException;
import cat.indiketa.degiro.exceptions.DeGiroException;
import cat.indiketa.degiro.http.DResponse;
import cat.indiketa.degiro.http.IDCommunication;
import cat.indiketa.degiro.log.DLog;
import cat.indiketa.degiro.model.*;
import cat.indiketa.degiro.model.raw.*;
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
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    //must be set just after login
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

    /**
     * Get all updates at once to avoid roudtrips.
     * Note: We can get each one of the argument separetly but from the API.
     *
     * @param lastOrderUpdate            last receive orders lastUpdated
     * @param lastPortfolioUpdate        last receive portfolio lastUpdated
     * @param lastPortfolioSummaryUpdate last receive portfolioSummary lastUpdated
     * @param lastHistoricalOrders       last historical orders
     * @param lastTransactions           last transactions
     * @param lastAlerts                 last alerts
     * @return all available changes from between last update an now
     * @throws DeGiroException
     */
    @Override
    public DUpdates updateAll(long lastOrderUpdate, long lastPortfolioUpdate, long lastPortfolioSummaryUpdate, long lastHistoricalOrders, long lastTransactions, long lastAlerts) throws DeGiroException {
        DUpdates update = new DUpdates();
        try {

            DResponse response = getData(String.format("portfolio=%d&totalPortfolio=%d&orders=%d&historicalOrders=%d&transactions=%d&alerts=%d", lastPortfolioUpdate, lastPortfolioSummaryUpdate, lastOrderUpdate, lastHistoricalOrders, lastTransactions, lastAlerts), null);
            String data = getResponseData(response);
            //orders
            DRawOrders rawOrders = gson.fromJson(data, DRawOrders.class);
            update.setOrders(DUtils.convert(rawOrders));

            //portfolio summary
            DRawPortfolioSummary rawPortfolioSummary = gson.fromJson(data, DRawPortfolioSummary.class);
            update.setPortfolioSummary(DUtils.convertPortfolioSummary(rawPortfolioSummary.getTotalPortfolio()));

            //portfolio
            DRawPortfolio rawPortfolio = gson.fromJson(data, DRawPortfolio.class);
            update.setPortfolio(DUtils.convert(rawPortfolio));
            //TODO update historicalOrders, transactions, alerts
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving portfolio", e);
        }
        return update;
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
    public List<DOrderHistoryRecord> getOrdersHistory(Calendar from, Calendar to) throws DeGiroException {

        DOrderHistory dOrderHistory = null;
        ensureLogged();

        try {
            String fromStr = from.get(Calendar.DATE) + "%2F" + (from.get(Calendar.MONTH) + 1) + "%2F" + from.get(Calendar.YEAR);
            String toStr = to.get(Calendar.DATE) + "%2F" + (to.get(Calendar.MONTH) + 1) + "%2F" + to.get(Calendar.YEAR);

            DResponse response = comm.getUrlData(session.getConfig().getReportingUrl(), "v4/order-history?fromDate=" + fromStr + "&toDate=" + toStr + "&intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), null);
            dOrderHistory = gson.fromJson(getResponseData(response), DOrderHistory.class);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving order history", e);
        }
        return dOrderHistory.getData();

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
    public DClient getClientData() throws DeGiroException {
        ensureLogged();
        return session.getClient();
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
            final DClientData client = gson.fromJson(getResponseData(response), DClientData.class);
            final DClient data = client.getData();
            if (data == null || data.getIntAccount() == 0) {
                throw new DeGiroException("IOException while retrieving user account information");
            }
            session.setClient(data);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving user information", e);
        }
    }

    public DAccountInfo getAccountInfo() throws DeGiroException {
        ensureLogged();
        try {
            DResponse response = comm.getUrlData(session.getConfig().getTradingUrl(), "v5/account/info/" + session.getClient().getIntAccount() + ";jsessionid=" + session.getJSessionId(), null);
            return gson.fromJsonData(getResponseData(response), DAccountInfo.class);
        } catch (IOException e) {
            throw new DeGiroException("IOException while checking account information", e);
        }
    }

    private void ensureVwdSession() throws DeGiroException {
        ensureLogged();
        if (session.getVwdSession() == null || session.getLastVwdSessionUsed() == 0 || (System.currentTimeMillis() - session.getLastVwdSessionUsed()) > TimeUnit.SECONDS.toMillis(15)) {
            DLog.DEGIRO.info("Renewing VWD session");
            renewVwdSession();
            if (!subscribedVwdIssues.isEmpty()) {
                subscribeToPrice(subscribedVwdIssues.keySet());
            }

        }
    }

    private void renewVwdSession() throws DeGiroException {

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

        Object data = generatePriceRequestPayload(subscribedVwdIssues.entrySet().stream().filter(e -> e.getValue() != null && e.getValue() > pollingInterval).map(Map.Entry::getKey));

        DResponse response = comm.getUrlData(degiro.getQuoteCastUrl(), "/" + session.getVwdSession(), data, headers);
        getResponseData(response);
        session.setLastVwdSessionUsed(System.currentTimeMillis());

    }

    private Object generatePriceRequestPayload(Stream<String> subscribedVwdIssues) {
        StringBuilder requestedIssues = new StringBuilder();
        subscribedVwdIssues.forEach(issueId -> {
            requestedIssues.append("req(X.BidPrice);req(X.AskPrice);req(X.LastPrice);req(X.LastTime);".replace("X", issueId + ""));
        });
        HashMap<String, String> data = new HashMap<>();
        data.put("controlData", requestedIssues.toString());
        return data;

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
    public DConfigDictionary getProductsConfig() throws DeGiroException {
        return get(
                "Products Configuration",
                () -> comm.getUrlData(session.getConfig().getDictionaryUrl(), "?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), null),
                DConfigDictionary.class
        );
    }

    private <T> T get(String desc, Callable<DResponse> call, Class<T> cls) throws DeGiroException {
        return get(desc, call, response -> gson.fromJson(response, cls));
    }

    private <T> T get(String desc, Callable<DResponse> get, ExceptionalTransformation<String, T> transformation) throws DeGiroException {
        ensureLogged();
        try {
            DResponse response = get.call();
            DLog.DEGIRO.trace("Response of {}: " + response);
            return transformation.transform(getResponseData(response));

        } catch (Exception e) {
            throw new DeGiroException("Exception occurred : " + desc, e);
        }
    }

    @Override
    public DProductDescriptions getProducts(List<Long> productIds) throws DeGiroException {
        return get(
                "Product Descriptions",
                () -> {
                    List<Header> headers = new ArrayList<>(1);
                    List<String> productIdStr = productIds.stream().map(String::valueOf).collect(Collectors.toList());
                    return comm.getUrlData(session.getConfig().getProductSearchUrl(), "v5/products/info?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), productIdStr, headers);
                },
                DProductDescriptions.class
        );
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

        //expected response: "{\"data\":{\"confirmationId\":\"15caf4dd-c2f2-4c0a-b5c2-e5f41c04a4be\",\"transactionFees\":[{\"id\":2,\"amount\":0.04,\"currency\":\"USD\"},{\"id\":3,\"amount\":0.50,\"currency\":\"EUR\"}]}}"
        return get("Check Order", () -> {
            return comm.getUrlData(session.getConfig().getTradingUrl(), "v5/checkOrder;jsessionid=" + session.getJSessionId() + "?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), orderToMap(order));
        }, r -> gson.fromJsonData(r, DOrderConfirmation.class));
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
            //ERROR Code 400 expected if price is set to more thant 20% of original price, degiro will reject order
            //"{\"data\":{\"orderId\":\"13ea6a6a-f361-41d8-96e5-f1fa5b3a9e3d\"}}"
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
            degiroOrder.put("buySell", order.getBuySell().getValue());
            degiroOrder.put("orderType", order.getOrderType().getValue());
            degiroOrder.put("productId", order.getProductId());
            degiroOrder.put("size", order.getSize());
            degiroOrder.put("timeType", order.getOrderTime().getValue());
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

    @Override
    public List<Long> getFavorites() throws DeGiroException {
        ensureLogged();
        try {
            DResponse response = comm.getUrlData(session.getConfig().getPaUrl(), "favourites?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), null);
            final List<Long> longs = gson.fromJsonData(getResponseData(response), new TypeToken<List<Long>>() {
            }.getType());
            return longs;
        } catch (IOException e) {
            throw new DeGiroException("IOException while checking account information", e);
        }
    }

    @Override
    public void addFavorite(long productId) throws DeGiroException {
        ensureLogged();
        try {
            final Map<String, Map<String, Object>> objectObjectHashMap = new HashMap<>();
            final HashMap<String, Object> value = new HashMap<>();
            value.put("productId", productId);
            objectObjectHashMap.put("data", value);
            DResponse response = comm.getUrlData(session.getConfig().getPaUrl(), "favourites?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), objectObjectHashMap);
            getResponseData(response); //check for response error
        } catch (IOException e) {
            throw new DeGiroException("IOException while checking account information", e);
        }
    }

    @Override
    public void deleteFavorite(long productId) throws DeGiroException {
        ensureLogged();
        try {
            DResponse response = comm.getUrlData(session.getConfig().getPaUrl(), "favourites?productId=" + productId + "&intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), null, null, "DELETE");
            getResponseData(response); //check for response error
        } catch (IOException e) {
            throw new DeGiroException("IOException while checking account information", e);
        }
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
        degiroOrder.put("buySell", order.getAction().getValue());
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

        DLog.HTTP.info(response.getMethod() + " " + response.getUrl() + " >> HTTP " + response.getStatus() + " Body: " + response.getText());
        String data = null;

        if (response.getStatus() == 401) {
            DLog.DEGIRO.warn("Session expired, clearing session tokens");
            session.clearSession();
            throw new DUnauthorizedException();
        }

        if (response.getStatus() == 400 && !Strings.isNullOrEmpty(response.getText()) && response.getText().startsWith("{")) {
            D400ErrorResponse error;
            try {
                error = gson.fromJson(response.getText(), D400ErrorResponse.class);
            } catch (IOException e) {
                //unable to decode error message
                throw new DeGiroException("Unexpected HTTP Status " + response.getStatus() + ": " + response.getMethod() + " " + response.getUrl() + " " + response.getText());
            }
            if (error != null && !error.getErrors().isEmpty()) {
                throw new DeGiroException(error.getErrorsToString());
            }
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
                DLog.DEGIRO.error("Exception while updating prices", e);
            }
        }

    }

    public boolean isConnected() {
        try {
            ensureLogged();
        } catch (DeGiroException e) {
            return false;
        }
        boolean connected = !Strings.isNullOrEmpty(session.getJSessionId());
        if (connected && comm.lastSuccess < (System.nanoTime() - TimeUnit.SECONDS.toNanos(6))) {
            //test connection if no activity for last x seconds
            try {
                //test if connection is alive
                final DResponse urlData = comm.getUrlData(degiro.getBaseUrl(), "/login/secure/config", null);
                if (urlData.getStatus() == 401) { //session expired
                    session.clearSession();
                    //try to login
                    login();
                    return true;
                }
                connected = urlData.getStatus() == 200;
            } catch (Exception e) {
                DLog.DEGIRO.error("Connection failure", e);
                connected = false;
                session.clearSession();
            }
        }
        return connected;
    }

    @Override
    public void close() {
        if (pricePoller != null) {
            pricePoller.cancel();
            pricePoller = null;
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

    private interface ExceptionalTransformation<T, R> {
        R transform(T data) throws Exception;
    }
}
