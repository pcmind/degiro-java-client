package cat.indiketa.degiro;

import cat.indiketa.degiro.exceptions.DInvalidCredentialsException;
import cat.indiketa.degiro.exceptions.DUnauthorizedException;
import cat.indiketa.degiro.exceptions.DValidationException;
import cat.indiketa.degiro.exceptions.DeGiroException;
import cat.indiketa.degiro.exceptions.SessionExpiredException;
import cat.indiketa.degiro.http.DResponse;
import cat.indiketa.degiro.http.IDCommunication;
import cat.indiketa.degiro.log.DLog;
import cat.indiketa.degiro.model.D400ErrorResponse;
import cat.indiketa.degiro.model.DAccountInfo;
import cat.indiketa.degiro.model.DCashFunds;
import cat.indiketa.degiro.model.DClient;
import cat.indiketa.degiro.model.DConfig;
import cat.indiketa.degiro.model.DConfigDictionary;
import cat.indiketa.degiro.model.DFavorites;
import cat.indiketa.degiro.model.DLastTransactions;
import cat.indiketa.degiro.model.DLogin;
import cat.indiketa.degiro.model.DNewOrder;
import cat.indiketa.degiro.model.DOrder;
import cat.indiketa.degiro.model.DOrderConfirmation;
import cat.indiketa.degiro.model.DOrderHistory;
import cat.indiketa.degiro.model.DOrderHistoryRecord;
import cat.indiketa.degiro.model.DPlacedOrder;
import cat.indiketa.degiro.model.DPrice;
import cat.indiketa.degiro.model.DPriceHistory;
import cat.indiketa.degiro.model.DPriceListener;
import cat.indiketa.degiro.model.DProductDescriptions;
import cat.indiketa.degiro.model.DProductSearch;
import cat.indiketa.degiro.model.DProductType;
import cat.indiketa.degiro.model.DTransactions;
import cat.indiketa.degiro.model.DUpdates;
import cat.indiketa.degiro.model.DvwdSessionId;
import cat.indiketa.degiro.model.IValidable;
import cat.indiketa.degiro.model.raw.DRawAlerts;
import cat.indiketa.degiro.model.raw.DRawCashFunds;
import cat.indiketa.degiro.model.raw.DRawHistoricalOrders;
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
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author indiketa
 */
public class DeGiroImpl implements DeGiro {

    private final DeGiroHost degiro;
    private final DCredentials credentials;
    private final TrackConnection comm;
    private final DSession session;
    private final DJsonDecoder gson;
    private final DPricePoller pricePoller = new DPricePoller();

    public DeGiroImpl(DeGiroHost degiro, DCredentials credentials, DSession session, IDCommunication comm) {
        this.degiro = degiro;
        this.session = session;
        this.credentials = credentials;
        this.comm = new TrackConnection(comm);
        this.gson = new DJsonDecoder();
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

            DResponse response = getUpdateData(String.format("portfolio=%d&totalPortfolio=%d&orders=%d&historicalOrders=%d&transactions=%d&alerts=%d", lastPortfolioUpdate, lastPortfolioSummaryUpdate, lastOrderUpdate, lastHistoricalOrders, lastTransactions, lastAlerts), null);
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

            final DRawAlerts dRawAlerts = gson.fromJson(data, DRawAlerts.class);
            update.setAlerts(DUtils.convert(dRawAlerts));

            final DRawTransactions dRawTransactions = gson.fromJson(data, DRawTransactions.class);
            update.setTransactions(DUpdates.DLastUpdate.of(dRawTransactions.transactions.lastUpdated, null));

            final DRawHistoricalOrders historicOrders = gson.fromJson(data, DRawHistoricalOrders.class);
            update.setTransactions(DUpdates.DLastUpdate.of(historicOrders.historicalOrders.lastUpdated, null));
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
            DResponse response = getUpdateData("cashFunds=0", null);
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
            DResponse response = getUpdateData("transactions=0", null);
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

        String fromStr = from.get(Calendar.DATE) + "%2F" + (from.get(Calendar.MONTH) + 1) + "%2F" + from.get(Calendar.YEAR);
        String toStr = to.get(Calendar.DATE) + "%2F" + (to.get(Calendar.MONTH) + 1) + "%2F" + to.get(Calendar.YEAR);

        transactions = httpGet(
                DTransactions.class,
                session.getConfig().getReportingUrl(), "v4/transactions?orderId=&product=&fromDate=" + fromStr + "&toDate=" + toStr + "&groupTransactionsByOrder=false&intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(),
                gson::fromJson
        );
        return transactions;

    }

    private void ensureLogged() throws DeGiroException {
        if (Strings.isNullOrEmpty(session.getJSessionId()) || session.getClient() == null || session.getConfig() == null) {
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

            DConfig config =  httpGet(DConfig.class, degiro.getBaseUrl(), "/login/secure/config", gson::fromJsonData);
            session.setConfig(config);

            DClient client = httpGet(DClient.class, config.getPaUrl(), "client?sessionId=" + session.getJSessionId(), gson::fromJsonData);
            session.setClient(client);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving user information", e);
        }
    }

    public DAccountInfo getAccountInfo() throws DeGiroException {
        ensureLogged();
        return httpGet(DAccountInfo.class, session.getConfig().getTradingUrl(), "v5/account/info/" + session.getClient().getIntAccount() + ";jsessionid=" + session.getJSessionId(), gson::fromJsonData);
    }


    @Override
    public void setPricePollingInterval(int duration, TimeUnit unit) throws DeGiroException {
        pricePoller.setPollingInterval(duration, unit);
    }

    @Override
    public void setPriceListener(DPriceListener priceListener) {
        this.pricePoller.setPriceListener(priceListener);
    }

    @Override
    public void unsubscribeToPrice(String vwdIssueId) {
        pricePoller.unsubscribe(Collections.singletonList(vwdIssueId));
    }

    @Override
    public void subscribeToPrice(String vwdIssueId) throws DeGiroException {
        pricePoller.subscribe(Collections.singletonList(vwdIssueId));
    }

    @Override
    public void subscribeToPrice(Collection<String> vwdIssueId) throws DeGiroException {
        pricePoller.subscribe(vwdIssueId);
    }

    @Override
    public void clearPriceSubscriptions() {
        pricePoller.clear();
    }

    @Override
    public DConfigDictionary getProductsConfig() throws DeGiroException {
        return httpGet(DConfigDictionary.class, session.getConfig().getDictionaryUrl(), "?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), gson::fromJson);
    }

    private <T extends IValidable> T httpGet(Class<T> cls, String base, String uri, ExceptionalTransformation<T> fromJsonData) throws DeGiroException {
        return requestAndValidate(cls, base, uri, null, null, fromJsonData, null);
    }

    private <T extends IValidable> T httpPost(Class<T> cls, String base, String uri, Object data, List<Header> headers, ExceptionalTransformation<T> fromJsonData) throws DeGiroException {
        return requestAndValidate(cls, base, uri, data, headers, fromJsonData, null);
    }

    private <T extends IValidable> T requestAndValidate(Class<T> cls, String base, String uri, Object data, List<Header> headers, ExceptionalTransformation<T> fromJsonData, String method) throws DeGiroException {
        final DResponse response;
        try {
            response = comm.getUrlData(base, uri, data, headers, method);
        } catch (IOException e) {
            throw new DeGiroException("Unable to execute request to " + base + uri + " with data: " + data);
        }
        final String responseData = getResponseData(response);
        try {
            final T fromData = fromJsonData.transform(responseData, cls);
            if (fromData == null || fromData.isInvalid()) {
                throw new DValidationException("Unexpected API Response: " + response);
            }
            return fromData;
        } catch (Exception e) {
            Throwables.throwIfInstanceOf(e, DeGiroException.class);
            throw new DeGiroException("Unable to decode response '" + response + "'");
        }
    }

    @Override
    public DProductDescriptions getProducts(List<Long> productIds) throws DeGiroException {
        if(productIds.isEmpty()) {
            return null;
        }
        List<Header> headers = new ArrayList<>(1);
        List<String> productIdStr = productIds.stream().map(String::valueOf).collect(Collectors.toList());
        return httpPost(
                DProductDescriptions.class,
               session.getConfig().getProductSearchUrl(),
                "v5/products/info?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(),
                productIdStr,
                headers,
                gson::fromJson
        );
    }

    @Override
    public DProductSearch searchProducts(String text, DProductType type, int limit, int offset) throws DeGiroException {

        if (Strings.isNullOrEmpty(text)) {
            throw new DeGiroException("Nothing to search");
        }

        DProductSearch productSearch = null;

        ensureLogged();

        String qs = "&searchText=" + text;

        if (type != null && type.getTypeCode() != 0) {
            qs += "&productTypeId=" + type.getTypeCode();
        }
        qs += "&limit=" + limit;
        if (offset > 0) {
            qs += "&offset=" + offset;
        }

        productSearch = httpGet(
                DProductSearch.class,
                session.getConfig().getProductSearchUrl(),
                "v5/products/lookup?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId() + qs,
                gson::fromJson
        );


        return productSearch;
    }

    @Override
    public DOrderConfirmation checkOrder(DNewOrder order) throws DeGiroException {

        if (order == null) {
            throw new DeGiroException("Order was null (no order to check)");
        }
        ensureLogged();
        //expected response: "{\"data\":{\"confirmationId\":\"15caf4dd-c2f2-4c0a-b5c2-e5f41c04a4be\",\"transactionFees\":[{\"id\":2,\"amount\":0.04,\"currency\":\"USD\"},{\"id\":3,\"amount\":0.50,\"currency\":\"EUR\"}]}}"
        return httpPost(
                DOrderConfirmation.class,
                session.getConfig().getTradingUrl(), "v5/checkOrder;jsessionid=" + session.getJSessionId() + "?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(),
                orderToMap(order),
                null,
                gson::fromJsonData
        );
    }

    @Override
    public DPlacedOrder confirmOrder(DNewOrder order, String confirmationId) throws DeGiroException {

        if (order == null) {
            throw new DeGiroException("Order was null (no order to check)");
        }

        if (Strings.isNullOrEmpty(confirmationId)) {
            throw new DeGiroException("ConfirmationId was empty");
        }

        ensureLogged();
        //ERROR Code 400 expected if price is set to more thant 20% of original price, degiro will reject order
        //"{\"data\":{\"orderId\":\"13ea6a6a-f361-41d8-96e5-f1fa5b3a9e3d\"}}"
        DPlacedOrder placedOrder = httpPost(
                DPlacedOrder.class,
                session.getConfig().getTradingUrl(),
                "v5/order/" + confirmationId + ";jsessionid=" + session.getJSessionId() + "?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(),
                orderToMap(order),
                null,
                gson::fromJson
        );

        return placedOrder;

    }

    @Override
    public DPlacedOrder deleteOrder(String orderId) throws DeGiroException {

        if (Strings.isNullOrEmpty(orderId)) {
            throw new DeGiroException("orderId was empty");
        }

        DPlacedOrder placedOrder = null;

        ensureLogged();
        placedOrder = requestAndValidate(
                DPlacedOrder.class,
                session.getConfig().getTradingUrl(),
                "v5/order/" + orderId + ";jsessionid=" + session.getJSessionId() + "?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(),
                null,
                null,
                gson::fromJson,
                "DELETE"
        );
        return placedOrder;

    }

    @Override
    public DPlacedOrder updateOrder(DOrder order, BigDecimal limit, BigDecimal stop) throws DeGiroException {

        if (order == null) {
            throw new NullPointerException("Order was null");
        }

        DPlacedOrder placedOrder = null;

        ensureLogged();
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

        placedOrder = requestAndValidate(
                DPlacedOrder.class,
                session.getConfig().getTradingUrl(),
                "v5/order/" + order.getId() + ";jsessionid=" + session.getJSessionId() + "?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(),
                degiroOrder,
                null,
                gson::fromJson,
                "PUT"
        );

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
        return httpGet(
                DFavorites.class,
                session.getConfig().getPaUrl(),
                "favourites?intAccount=" + session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(),
                gson::fromJsonData
        );
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

    private DResponse getUpdateData(String params, Object data) throws IOException {
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
        pricePoller.close();
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

    private interface ExceptionalTransformation<T> {
        T transform(String data, Class<T> cls) throws Exception;
    }

    private class DPricePoller implements Closeable {
        private final DVwdPriceDecoder vwdDecoder = new DVwdPriceDecoder();
        private final Set<String> subscriptions = new ConcurrentSkipListSet<>();
        private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
                1,
                new ThreadFactoryBuilder().setNameFormat("degiro-poller-%d").setDaemon(true).build()
        );
        private final Type rawPriceData = new TypeToken<List<DRawVwdPrice>>() {
        }.getType();
        private String vwdSessionId;
        private DPriceListener priceListener;
        //must be set just after login
        private long pollingInterval = TimeUnit.SECONDS.toMillis(5);
        private Future<?> task;

        public synchronized void setPriceListener(DPriceListener priceListener) {
            this.priceListener = priceListener;
            startPollingIfRequired();
        }

        public synchronized void setPollingInterval(long pollingInterval, TimeUnit unit) {
            this.pollingInterval = unit.toMillis(pollingInterval);
            if (task != null) {
                task.cancel(false);
                task = null;
                startPollingIfRequired();
            }
        }

        public synchronized void subscribe(Collection<String> vwdIssueIds) {
            if (task != null && subscriptions.isEmpty()) {
                //only subscribe new subscriptions, older ones where already subscribed
                final Set<String> difference = Sets.difference(Sets.newHashSet(vwdIssueIds), subscriptions);
                subscribeToPriceUpdates(difference, true);
            }

            this.subscriptions.addAll(vwdIssueIds);
            startPollingIfRequired();
        }

        public synchronized void unsubscribe(Collection<String> vwdIssueId) {
            try {
                //we just need to unsubscribe if subscribed
                if (task != null && subscriptions.isEmpty()) {
                    subscribeToPriceUpdates(vwdIssueId, false);
                }
            } catch (Exception e) {
                DLog.DEGIRO.error("Fail to subscribe " + vwdIssueId + ". Expiring session " + vwdSessionId, e);
                vwdSessionId = null;
            } finally {
                subscriptions.removeAll(vwdIssueId);
            }
        }

        private void startPollingIfRequired() {
            if (task == null && priceListener != null && !subscriptions.isEmpty()) {
                //start polling for updates
                task = scheduler.scheduleAtFixedRate(this::poll, 0, pollingInterval, TimeUnit.MILLISECONDS);
            }
        }

        public synchronized void clear() {
            try {
                task.cancel(true);
                unsubscribe(Lists.newArrayList(subscriptions));
            } catch (Exception e) {
                subscriptions.clear();
                task = null;
            }
        }

        @Override
        public synchronized void close() {
            scheduler.shutdownNow();
            vwdDecoder.resetState();
        }

        public synchronized void poll() {
            try {
                ensureLogged();
                if (vwdSessionId == null) {
                    vwdSessionId = newVwdSession();
                    //subscribe to price update
                    subscribeToPriceUpdates(subscriptions, true);
                }
                final List<DRawVwdPrice> data = checkPriceChanges(vwdSessionId);

                List<DPrice> prices = vwdDecoder.decode(data);

                if (priceListener != null) {
                    for (DPrice price : prices) {
                        priceListener.priceChanged(price);
                    }
                }
            } catch (SessionExpiredException e) {
                DLog.DEGIRO.error("Session " + vwdSessionId + " has expired");
                vwdDecoder.resetState();
                vwdSessionId = null;
                //next update will update session
                //not recursive to avoid stack overflow
            } catch (Exception e) {
                DLog.DEGIRO.error("Exception while updating prices", e);
            }
        }

        /**
         * Subscribe or unsubscribe to price update.
         */
        private void subscribeToPriceUpdates(Collection<String> difference, boolean subscribe) {
            try {
                subscribeOrUnsubscribe(vwdSessionId, difference, subscribe, vwdDecoder.getSupportedFields());
            } catch (Exception e) {
                DLog.DEGIRO.error("Fail to subscribe " + difference + ". Expiring session " + vwdSessionId, e);
                vwdSessionId = null;
            }
        }

        private List<DRawVwdPrice> checkPriceChanges(String vwdSession) throws DeGiroException {
            try {
                List<Header> headers = new ArrayList<>(1);
                headers.add(new BasicHeader("Origin", session.getConfig().getTradingUrl()));

                DResponse response = comm.getUrlData(degiro.getQuoteCastUrl(), "/" + vwdSession, null, headers);
                return gson.fromJson(getResponseData(response), rawPriceData);
            } catch (IOException e) {
                throw new DeGiroException("IOException while subscribing to issues", e);
            }
        }

        private void subscribeOrUnsubscribe(String vwdSessionId, Collection<String> vwdIds, boolean subscribe, Set<String> supportedFields) throws DeGiroException {
            if (!vwdIds.isEmpty()) {
                try{
                   List<Header> headers = new ArrayList<>(1);
                   headers.add(new BasicHeader("Origin", session.getConfig().getTradingUrl()));
                   HashMap<String, String> data = new HashMap<>();
                   data.put("controlData", buildSubsciptionRequestString(vwdIds, supportedFields, subscribe));
                   DResponse response = comm.getUrlData(degiro.getQuoteCastUrl(), "/" + vwdSessionId, data, headers);
                   getResponseData(response);
                   DLog.DEGIRO.info("Subscribed successfully for issues " + Joiner.on(", ").join(vwdIds));
                } catch (IOException e) {
                    throw new DeGiroException("IOException while subscribing to issues " + vwdIds, e);
                }
            }
        }

        private String newVwdSession() throws DeGiroException {
            List<Header> headers = new ArrayList<>(1);
            headers.add(new BasicHeader("Origin", session.getConfig().getTradingUrl()));
            HashMap<String, String> data = new HashMap();
            data.put("referrer", degiro.getBaseUrl());
            DvwdSessionId vwdSession = httpPost(
                    DvwdSessionId.class,
                    degiro.getQuoteCastUrl(), "/request_session?version=1.0.20180305&userToken=" + session.getClient().getId(),
                    data,
                    headers,
                    gson::fromJson
            );
            return vwdSession.getSessionId();
        }

        private String buildSubsciptionRequestString(Collection<String> subscribedVwdIssues, Collection<String> fields, boolean subscribe) {
            final String str = subscribe ? "req(" : "rel(";
            StringBuilder requestedIssues = new StringBuilder();
            for (String issueId : subscribedVwdIssues) {
                for (String vwdData : fields) {
                    requestedIssues.append(str).append(issueId).append(".").append(vwdData).append(");");
                }
            }
            return requestedIssues.toString();
        }
    }
}
