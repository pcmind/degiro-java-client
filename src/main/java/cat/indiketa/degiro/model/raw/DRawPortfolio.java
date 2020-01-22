package cat.indiketa.degiro.model.raw;

import lombok.Data;

import java.util.List;

/**
 *
 * @author indiketa
 */
@Data
public class DRawPortfolio {

    private Portfolio portfolio;

    @Data
    public static class Portfolio {

        private Long lastUpdated;
        private String name;
        private List<Value> value = null;
        private Boolean isAdded;
        //true when row was deleted
        private Boolean isRemoved;

    }
    @Data
    public static class Value {


        /**
         * Usualy string = "positionrow"
         */
        private String name;

        /**
         * Samve as productId but in string format instead of number.
         * hence only one entry in portfolio per product is present in remote DeGiro.
         *
         */
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

        public boolean getIsModified() {
            return isAdded == null && isRemoved == null;
        }
    }

}
