package cat.indiketa.degiro.model;

import lombok.Data;

import java.util.Map;

@Data
public class DAccountInfo implements IValidable {
    private long clientId;
    private String baseCurrency;
    private Map<String, DCurrencyPair> currencyPairs;
    private String marginType;
    private Map<String, DCashFund[]> cashFunds;

    @Override
    public boolean isInvalid() {
        return clientId == 0 || baseCurrency == null || currencyPairs == null || marginType == null || cashFunds == null;
    }

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
