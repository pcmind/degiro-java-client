package cat.indiketa.degiro.model;

import lombok.Data;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class DCashMovement implements IValidable {
    OffsetDateTime date;
    OffsetDateTime valueDate;
    //zero is a valid id; it is the cash fund price id
    long id;
    @Nullable
    Long productId;
    @Nullable
    String orderId;
    String description;
    String currency;
    @Nullable
    BigDecimal change;
    @Nullable
    Balence balance;

    /**
     * Knonw types
     *
     * "type": "CASH_TRANSACTION"
     * "type": "CASH_FUND_NAV_CHANGE"
     * "type": "CASH_FUND_TRANSACTION"
     * "type": "TRANSACTION"
     */
    private String type;

    @Override
    public boolean isInvalid() {
        return  date == null || valueDate == null;
    }


    @Data
    public static class Balence {
        BigDecimal total;
        List<CashFund> cashFund;
        BigDecimal unsettledCash;
    }

    @Data
    public static class CashFund {
        BigDecimal participation;
        BigDecimal price;
        long id;
    }

}
