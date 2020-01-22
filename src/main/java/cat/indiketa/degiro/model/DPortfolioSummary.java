package cat.indiketa.degiro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author indiketa
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
public class DPortfolioSummary {

    private BigDecimal cash;
    private String cashFundCompensationCurrency;
    private BigDecimal cashFundCompensation;
    private BigDecimal cashFundCompensationWithdrawn;
    private BigDecimal cashFundCompensationPending;
    private BigDecimal todayNonProductFees;
    private BigDecimal totalNonProductFees;
    private Map<String, BigDecimal> freeSpaceNew;

    public DPortfolioSummary copy() {
        Map<String, BigDecimal> freeSpaceNewCopy = null;
        if (freeSpaceNew != null) {
            freeSpaceNewCopy = new HashMap<>();
            freeSpaceNewCopy.putAll(freeSpaceNew);
        }
        return new DPortfolioSummary(cash, cashFundCompensationCurrency, cashFundCompensation, cashFundCompensationWithdrawn, cashFundCompensationPending, todayNonProductFees, totalNonProductFees, freeSpaceNewCopy);
    }
}
