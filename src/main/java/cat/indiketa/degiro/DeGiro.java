package cat.indiketa.degiro;

import cat.indiketa.degiro.exceptions.DeGiroException;
import cat.indiketa.degiro.model.DAccountInfo;
import cat.indiketa.degiro.model.DCashMovement;
import cat.indiketa.degiro.model.DClient;
import cat.indiketa.degiro.model.DConfigDictionary;
import cat.indiketa.degiro.model.DNewOrder;
import cat.indiketa.degiro.model.DOrder;
import cat.indiketa.degiro.model.DOrderConfirmation;
import cat.indiketa.degiro.model.DOrderHistoryRecord;
import cat.indiketa.degiro.model.DPlacedOrder;
import cat.indiketa.degiro.model.DPrice;
import cat.indiketa.degiro.model.DPriceHistory;
import cat.indiketa.degiro.model.DProductDescriptions;
import cat.indiketa.degiro.model.DProductSearch;
import cat.indiketa.degiro.model.DProductType;
import cat.indiketa.degiro.model.DTransaction;
import cat.indiketa.degiro.model.updates.DUpdateSection;
import cat.indiketa.degiro.model.updates.DUpdateToken;
import cat.indiketa.degiro.model.updates.DUpdates;

import java.io.Closeable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author indiketa
 */
public interface DeGiro {

    List<DCashMovement> getAccountOverview(LocalDate from, LocalDate to) throws DeGiroException;

    DClient getClientData() throws DeGiroException;

    DAccountInfo getAccountInfo() throws DeGiroException;

    /**
     * One request to update multiple tables instead of multiple calls to remote api
     *
     * @param tokens identifiers of section to update and start last token received
     * @return change available that happened after last provided tokens
     * @see DUpdateToken#allSections() create initial tokens for first request for all sections
     * @see DUpdateToken#createInitial(DUpdateSection...)  create initial tokens for first request for selected sections
     */
    DUpdates updateAll(Collection<DUpdateToken> tokens) throws DeGiroException;

    List<DOrderHistoryRecord> getOrdersHistory(LocalDate from, LocalDate to) throws DeGiroException;

    List<DTransaction> getTransactions(LocalDate from, LocalDate to) throws DeGiroException;

    /**
     * Price poll instance to manages subscription and get updates on products prices.
     *
     * @return client poller instance
     */
    PricePoller getPricePoller();

    DConfigDictionary getProductsConfig() throws DeGiroException;

    DProductSearch searchProducts(String text, DProductType type, int limit, int offset) throws DeGiroException;

    DProductDescriptions getProducts(List<String> productIds) throws DeGiroException;

    DOrderConfirmation checkOrder(DNewOrder order) throws DeGiroException;

    DPlacedOrder confirmOrder(DNewOrder order, String confirmationId) throws DeGiroException;

    DPlacedOrder deleteOrder(String orderId) throws DeGiroException;

    DPlacedOrder updateOrder(DOrder order, BigDecimal limit, BigDecimal stop) throws DeGiroException;

    DPriceHistory getPriceHistory(Long issueId) throws DeGiroException;

    List<Long> getFavorites() throws DeGiroException;

    void addFavorite(long productId) throws DeGiroException;

    void deleteFavorite(long productId) throws DeGiroException;

    boolean isConnected();

    void close();

    interface PricePoller extends Closeable {

        void subscribe(Collection<String> vwdIssueId) throws DeGiroException;

        default void subscribe(String vwdIssueId) throws DeGiroException {
            subscribe(Collections.singletonList(vwdIssueId));
        }

        void unsubscribe(Collection<String> vwdIssueId) throws DeGiroException;

        default void unsubscribe(String vwdIssueId) throws DeGiroException {
            unsubscribe(Collections.singletonList(vwdIssueId));
        }

        /**
         * Clear subscriptions.
         */
        void unsubscribeAll();


        Collection<DPrice> poll();
    }
}
