package cat.indiketa.degiro.model;

import lombok.Data;
import lombok.Value;

import java.util.List;

@Data
public class DUpdates {
    DLastUpdate<List<DUpdate<DOrder, String>>> orders;
    DLastUpdate<List<DUpdate<DPortfolioProduct, String>>> portfolio;
    DLastUpdate<DUpdate<DPortfolioSummary, String>> portfolioSummary;
    //UNKNOWN SCHEMA
    DLastUpdate historicalOrders;
    //UNKNOWN SCHEMA
    DLastUpdate transactions;
    //UNKNOWN SCHEMA
    DLastUpdate<List<DUpdate<DAlert, String>>> alerts;

    @Value(staticConstructor = "of")
    public static class DLastUpdate<T> {
        private long lastUpdate;
        private T updates;
    }
}

