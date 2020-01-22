package cat.indiketa.degiro;

import cat.indiketa.degiro.exceptions.DeGiroException;
import cat.indiketa.degiro.model.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author indiketa
 */
public interface DeGiro {

    DCashFunds getCashFunds() throws DeGiroException;

    DLastTransactions getLastTransactions() throws DeGiroException;

    DClient getClientData() throws DeGiroException;

    DAccountInfo getAccountInfo() throws DeGiroException;

    default List<DOrder> getOrders() throws DeGiroException {
        return updateOrders(0).getAdded();
    }

    DOrders updateOrders(long lastUpdate) throws DeGiroException;

    DPortfolioProducts updatePortfolio(long lastUpdate) throws DeGiroException;

    DPortfolioSummaryUpdate updatePortfolioSummary(long lastUpdate) throws DeGiroException;

    /**
     * One show update multiple tables instead of multiple calls to remote api
     *
     * @param lastOrderUpdate last receive orders lastUpdated
     * @param lastPortfolioUpdate last receive portfolio lastUpdated
     * @param lastPortfolioSummaryUpdate last receive portfolioSummary lastUpdated
     * @return
     */
    DUpdates updateAll(long lastOrderUpdate, long lastPortfolioUpdate, long lastPortfolioSummaryUpdate) throws DeGiroException ;

    List<DOrderHistoryRecord> getOrdersHistory(Calendar from, Calendar to) throws DeGiroException;

    DTransactions getTransactions(Calendar from, Calendar to) throws DeGiroException;

    void setPriceListener(DPriceListener priceListener);

    void setPricePollingInterval(int duration, TimeUnit unit) throws DeGiroException;

    void unsubscribeToPrice(String vwdIssueId) ;
    
    void subscribeToPrice(String vwdIssueId) throws DeGiroException;

    void subscribeToPrice(Collection<String> vwdIssueId) throws DeGiroException;

    void clearPriceSubscriptions();

    DConfigDictionary getProductsConfig() throws DeGiroException;

    DProductSearch searchProducts(String text, DProductType type, int limit, int offset) throws DeGiroException;

    DProductDescriptions getProducts(List<Long> productIds) throws DeGiroException;

    DOrderConfirmation checkOrder(DNewOrder order) throws DeGiroException;

    DPlacedOrder confirmOrder(DNewOrder order, String confirmationId) throws DeGiroException;

    DPlacedOrder deleteOrder(String orderId) throws DeGiroException;

    DPlacedOrder updateOrder(DOrder order, BigDecimal limit, BigDecimal stop) throws DeGiroException;

    DPriceHistory getPriceHistory(Long issueId) throws DeGiroException;

    boolean isConnected();

    void close();
}
