package cat.indiketa.degiro.model.raw;

import cat.indiketa.degiro.model.raw.DRawPortfolio.Value;
import java.util.List;

/**
 *
 * @author indiketa
 */
public class DRawTransactions {

    public RawTransactions transactions;

    public RawTransactions getTransactions() {
        return transactions;
    }

    public void setTransactions(RawTransactions transactions) {
        this.transactions = transactions;
    }

    public class RawTransactions {

        public Long lastUpdated;
        public String name;
        public List<Value> value = null;

        public Long getLastUpdated() {
            return lastUpdated;
        }

        public void setLastUpdated(Long lastUpdated) {
            this.lastUpdated = lastUpdated;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Value> getValue() {
            return value;
        }

        public void setValue(List<Value> value) {
            this.value = value;
        }

    }

}
