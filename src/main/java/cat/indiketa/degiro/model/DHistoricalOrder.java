package cat.indiketa.degiro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
public class DHistoricalOrder implements DCopyable<DHistoricalOrder> {
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

    @Override
    public DHistoricalOrder copy() {
        return withId(id); //force copy
    }
}
