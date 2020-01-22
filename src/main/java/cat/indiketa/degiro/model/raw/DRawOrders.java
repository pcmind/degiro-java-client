package cat.indiketa.degiro.model.raw;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * @author indiketa
 */
@Data
public class DRawOrders {

    public RawOrders orders;

    @Data
    @NoArgsConstructor
    public static class RawOrders {

        public Long lastUpdated;
        public String name;
        public List<Value> value = null;
    }
    @Data
    public static class Value {


        private String id;
        private List<DFieldValue> value = null;
        private Boolean isAdded;
        //true when row was deleted
        private Boolean isRemoved;

        public boolean getIsRemoved() {
            return isRemoved != null && isRemoved;
        }
        public boolean getIsAdded() {
            return isAdded != null && isAdded;
        }
    }

}
