package cat.indiketa.degiro.model.raw;

import cat.indiketa.degiro.model.raw.DRawPortfolio.Value;
import lombok.Data;

import java.util.List;

/**
 *
 * @author indiketa
 */
@Data
public class DRawTransactions {

    public RawTransactions transactions;

    @Data
    public class RawTransactions {

        public Long lastUpdated;
        public String name;
        public List<Value> value = null;

    }

}
