package cat.indiketa.degiro.model.updates;

import cat.indiketa.degiro.model.DAlert;
import cat.indiketa.degiro.model.DCashFund;
import cat.indiketa.degiro.model.DHistoricalOrder;
import cat.indiketa.degiro.model.DLastTransaction;
import cat.indiketa.degiro.model.DOrder;
import cat.indiketa.degiro.model.DPortfolioProduct;
import cat.indiketa.degiro.model.DPortfolioSummary;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


/**
 * Represent update received from the network.
 */
@Data
@AllArgsConstructor
public class DUpdates {
    List<DUpdateToken> tokens;
    /**
     * On first update request with token == 0 contain all open orders.
     */
    DLastUpdate<List<DUpdate<DOrder>>> orders;
    /**
     * On first update request with token == 0 contain all portfolio entries.
     */
    DLastUpdate<List<DUpdate<DPortfolioProduct>>> portfolio;
    /**
     * On first update request with token == 0 is empty
     */
    DLastUpdate<List<DUpdate<DHistoricalOrder>>> historicalOrders;
    /**
     * On first update request with token == 0 contain all portfolio summary entries.
     */
    DLastUpdate<DUpdate<DPortfolioSummary>> portfolioSummary;
    /**
     * On first update request with token == 0 contain all active alerts.
     */
    DLastUpdate<List<DUpdate<DAlert>>> alerts;
    /**
     * On first update request with token == 0 contain existing cash funds.
     */
    DLastUpdate<List<DUpdate<DCashFund>>> cashFunds;
    /**
     * On first update request with token == 0 is empty
     */
    DLastUpdate<List<DUpdate<DLastTransaction>>> transactions;

}

