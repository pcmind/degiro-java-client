package cat.indiketa.degiro.model.raw;

import cat.indiketa.degiro.model.raw.DRawPortfolio.Value;
import java.util.List;

/**
 *
 * @author indiketa
 */
public class DRawOrders {

    public RawOrders orders;

    public RawOrders getOrders() {
        return orders;
    }

    public void setOrders(RawOrders orders) {
        this.orders = orders;
    }

    public class RawOrders {

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
