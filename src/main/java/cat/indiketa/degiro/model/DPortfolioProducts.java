package cat.indiketa.degiro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author indiketa
 */
@Data
public class DPortfolioProducts {
    private long lastUpdate;

    private List<DPortfolioProduct> added = new ArrayList<>();
    private List<DPortfolioProduct> updates = new ArrayList<>();
    private Set<String> removed = new LinkedHashSet<>();

    public boolean hasChanges() {
        return !added.isEmpty() || !updates.isEmpty() || !removed.isEmpty();
    }


    //see https://github.com/Prog-Party/MMM-DeGiro/blob/4eef24373b6657eb4edb94ae7aec766b99a43a9c/MMM-DeGiro.js
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DPortfolioProduct {
        protected String currency;
        protected String id;
        protected String positionType; //will be PRODUCT/CASH
        //price of single
        protected Long size;
        protected BigDecimal price;
        //price times size
        protected BigDecimal value;
        protected String accruedInterest;

        public DPortfolioProduct(String currency) {
            this.currency = currency;
        }

        /**
         * {
         * "isAdded": true,
         * "name": "plBase",
         * "value": {
         * "EUR": -32.75007802
         * }
         * }
         */
        protected BigDecimal plBase;
        /**
         * {
         * "isAdded": true,
         * "name": "todayPlBase",
         * "value": {
         * "EUR": -32.59918492
         * }
         * },
         */
        protected BigDecimal todayPlBase;
        protected BigDecimal portfolioValueCorrection;
        protected BigDecimal breakEvenPrice;
        protected Long averageFxRate;
        protected BigDecimal realizedProductPl;
        protected BigDecimal realizedFxPl;
        protected BigDecimal todayRealizedProductPl;
        protected BigDecimal todayRealizedFxPl;

        public DPortfolioProduct copy() {
            return new DPortfolioProduct(currency, id, positionType, size, price, value, accruedInterest, plBase, todayPlBase, portfolioValueCorrection, breakEvenPrice, averageFxRate, realizedProductPl, realizedFxPl, todayRealizedProductPl, todayRealizedFxPl);
        }
    }

}
