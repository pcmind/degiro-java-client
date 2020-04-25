package cat.indiketa.degiro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Represent a portfolio product currently owned.
 *
 */
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
    /**
     * Average price paid for this product for each unit in product currency.
     * <br>
     * Fees are not taken into account.
     * <br>
     * Total price in base currency (without fees) can be calculated as
     * <pre>(breakEvenPrice * size) * averageFxRate</pre>
     */
    protected BigDecimal breakEvenPrice;
    /**
     * Average FX rate used to buy current portfolio entry. Will be 1 if same currency as base currency.
     */
    protected BigDecimal averageFxRate;
    protected BigDecimal realizedProductPl;
    protected BigDecimal realizedFxPl;
    protected BigDecimal todayRealizedProductPl;
    protected BigDecimal todayRealizedFxPl;

    public DPortfolioProduct copy() {
        return withId(id); //force copy
    }
}
