package cat.indiketa.degiro;

import cat.indiketa.degiro.exceptions.DeGiroException;
import cat.indiketa.degiro.model.DCashFunds;
import cat.indiketa.degiro.model.DLastTransactions;
import cat.indiketa.degiro.model.DNewOrder;
import cat.indiketa.degiro.model.DOrder;
import cat.indiketa.degiro.model.DOrderConfirmation;
import cat.indiketa.degiro.model.DPlacedOrder;
import cat.indiketa.degiro.model.DPortfolioProducts;
import cat.indiketa.degiro.model.DPortfolioSummary;
import cat.indiketa.degiro.model.DPriceListener;
import cat.indiketa.degiro.model.DProductSearch;
import cat.indiketa.degiro.model.DProductType;
import cat.indiketa.degiro.model.DProductDescriptions;
import cat.indiketa.degiro.model.DTransactions;
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

    List<DOrder> getOrders() throws DeGiroException;

    DPortfolioProducts getPortfolio() throws DeGiroException;
    
    DPortfolioSummary getPortfolioSummary() throws DeGiroException;

    DTransactions getTransactions(Calendar from, Calendar to) throws DeGiroException;

    void setPriceListener(DPriceListener priceListener);

    void setPricePollingInterval(int duration, TimeUnit unit) throws DeGiroException;

    void subscribeToPrice(Collection<String> vwdIssueId) throws DeGiroException;

    void clearPriceSubscriptions();

    DProductSearch searchProducts(String text, DProductType type, int limit, int offset) throws DeGiroException;

    DProductDescriptions getProducts(List<Long> productIds) throws DeGiroException;

    DOrderConfirmation checkOrder(DNewOrder order) throws DeGiroException;

    DPlacedOrder confirmOrder(DNewOrder order, String confirmationId) throws DeGiroException;

    DPlacedOrder deleteOrder(String orderId) throws DeGiroException;

    DPlacedOrder updateOrder(DOrder order, BigDecimal limit, BigDecimal stop) throws DeGiroException;

}
