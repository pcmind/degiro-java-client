package cat.indiketa.degiro.model;

import lombok.Data;

import javax.annotation.Nullable;
import java.math.BigDecimal;

@Data
public class DTransaction implements IValidable {

    private long id;
    private long productId;
    private String date;
    private DOrderAction buysell;
    private BigDecimal price;
    private long quantity;
    private BigDecimal total;
    private long orderTypeId;
    private String counterParty;
    private boolean transfered;
    @Nullable
    private BigDecimal fxRate;
    private BigDecimal totalInBaseCurrency;
    private BigDecimal feeInBaseCurrency;
    private BigDecimal totalPlusFeeInBaseCurrency;

    @Override
    public boolean isInvalid() {
        return id == 0 || productId == 0 || date == null;
    }
}
