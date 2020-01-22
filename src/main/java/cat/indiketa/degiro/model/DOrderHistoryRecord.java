package cat.indiketa.degiro.model;

import java.math.BigDecimal;
import java.util.Calendar;

import lombok.Data;

@Data
public class DOrderHistoryRecord {
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
