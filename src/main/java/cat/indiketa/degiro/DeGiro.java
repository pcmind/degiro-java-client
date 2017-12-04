package cat.indiketa.degiro;

import cat.indiketa.degiro.exceptions.DeGiroException;
import cat.indiketa.degiro.model.DCashFunds;
import cat.indiketa.degiro.model.DLastTransactions;
import cat.indiketa.degiro.model.DOrders;
import cat.indiketa.degiro.model.DPortfolio;
import cat.indiketa.degiro.model.DPriceListener;
import cat.indiketa.degiro.model.DProducts;
import cat.indiketa.degiro.model.DTransactions;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author indiketa
 */
public interface DeGiro {

    void clearPriceWatchs();

    DCashFunds getCashFunds() throws DeGiroException;

    DLastTransactions getLastTransactions() throws DeGiroException;

    DOrders getOrders() throws DeGiroException;

    DPortfolio getPortfolio() throws DeGiroException;

    DProducts getProducts(List<String> productIds) throws DeGiroException;

    DTransactions getTransactions(Calendar from, Calendar to) throws DeGiroException;

    void setPriceListener(DPriceListener priceListener);

    void setPricePollingInterval(int duration, TimeUnit unit) throws DeGiroException;

    void subscribeToPrice(Collection<Long> vwdIssueId) throws DeGiroException;

}
