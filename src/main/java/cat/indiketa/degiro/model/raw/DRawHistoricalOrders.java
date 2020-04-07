package cat.indiketa.degiro.model.raw;

import lombok.Data;

import java.util.List;

@Data
public class DRawHistoricalOrders {
    public HistoricalOrders historicalOrders;

    @Data
    public static class HistoricalOrders {

        public Long lastUpdated;
        public String name;
        public List<DRawPortfolio.Value> value = null;
    }
}
