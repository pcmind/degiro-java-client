package cat.indiketa.degiro.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Calendar;

@Data
public class DOrderHistoryRecord implements IValidable {
    private Calendar created;
    private String orderId;
    private long productId;
    private BigDecimal size;
    private BigDecimal price;
    private DOrderAction buysell;
    private DOrderType orderTypeId;
    private DOrderTime orderTimeTypeId;
    private BigDecimal stopPrice;
    private int currentTradedSize;
    private int totalTradedSize;
    private Type type;
    private Status status;
    private Calendar last;
    private boolean isActive;

    @Override
    public boolean isInvalid() {
        return productId == 0 || orderId == null || created == null;
    }


    public static enum Type {
        CREATE,
        MODIFY,
        DELETE;
    }

    public static enum Status {
        CONFIRMED,
        REJECTED
    }
}
