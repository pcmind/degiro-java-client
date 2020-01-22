package cat.indiketa.degiro.model.raw;

import cat.indiketa.degiro.model.raw.DRawPortfolio.Value;
import lombok.Data;

import java.util.List;

/**
 *
 * @author indiketa
 */
@Data
public class DRawCashFunds {
    public CashFunds cashFunds;

    @Data
    public static class CashFunds {

        public Long lastUpdated;
        public String name;
        public List<Value> value = null;
    }

}
