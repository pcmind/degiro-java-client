package cat.indiketa.degiro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author indiketa
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
@With
public class DPortfolioSummary implements DCopyable<DPortfolioSummary> {

    /**
     * Usually contains the same amount than the one present in freeSpaceNew[baseCurrency]
     */
    private BigDecimal cashFund;
    private String cashFundCompensationCurrency;
    private BigDecimal cashFundCompensation;
    private BigDecimal cashFundCompensationWithdrawn;
    private BigDecimal cashFundCompensationPending;
    private BigDecimal todayNonProductFees;
    private BigDecimal totalNonProductFees;
    private BigDecimal totalCash;
    private BigDecimal flatexCash;
    private BigDecimal degiroCash;
    private Map<String, BigDecimal> freeSpaceNew;

    @Override
    public DPortfolioSummary copy() {
        Map<String, BigDecimal> freeSpaceNewCopy = null;
        if (freeSpaceNew != null) {
            freeSpaceNewCopy = new HashMap<>(freeSpaceNew);
        }
        return withFreeSpaceNew(freeSpaceNewCopy);
    }
}
