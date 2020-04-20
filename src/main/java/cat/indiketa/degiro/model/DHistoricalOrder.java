package cat.indiketa.degiro.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DHistoricalOrder {
    String id;
    /*
    date field is not a date, but a time in the day
     */
    LocalDateTime date;
    long productId;
    String product;
    String currency;
    DOrderAction buysell;
    BigDecimal size;
    BigDecimal quantity;
    BigDecimal price;
    DOrderType orderType;
    boolean retainedOrder;
    boolean sentToExchange;
}
