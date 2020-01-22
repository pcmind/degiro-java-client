package cat.indiketa.degiro.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author indiketa
 */
@Data
public class DCashFunds {

    private List<DCashFund> cashFunds;

    @Data
    public static class DCashFund {

        private int id;
        private String currencyCode;
        private BigDecimal value;
    }

}
