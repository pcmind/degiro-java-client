package cat.indiketa.degiro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
public class DPortfolioProduct {
    protected String id;
    protected String positionType; //will be PRODUCT/CASH
    //price of single
    protected long size;
    protected BigDecimal price;
    //price times size
    protected BigDecimal value;
    protected String accruedInterest;

    /**
     * {
     * "isAdded": true,
     * "name": "plBase",
     * "value": {
     * "EUR": -32.75007802
     * }
     * }
     */
    protected Map<String, BigDecimal> plBase;
    /**
     * {
     * "isAdded": true,
     * "name": "todayPlBase",
     * "value": {
     * "EUR": -32.59918492
     * }
     * },
     */
    protected Map<String, BigDecimal> todayPlBase;
    protected BigDecimal portfolioValueCorrection;
    protected BigDecimal breakEvenPrice;
    protected long averageFxRate;
    protected BigDecimal realizedProductPl;
    protected BigDecimal realizedFxPl;
    protected BigDecimal todayRealizedProductPl;
    protected BigDecimal todayRealizedFxPl;

    public DPortfolioProduct copy() {
        return withId(id); //force copy
    }
}
