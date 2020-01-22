package cat.indiketa.degiro.model;

import lombok.Data;

import java.util.Map;

@Data
public class DAccountInfo {
    private long clientId;
    private String baseCurrency;
    private Map<String, DCurrencyPair> currencyPairs;
    private String marginType;
    private Map<String, DCashFund[]> cashFunds;

    @Data
    public static class DCurrencyPair {
        private long id;
        private String price;
    }

    @Data
    public static class DCashFund {
        public long id;
        public String name;
        public long[] productIds;
    }
}
