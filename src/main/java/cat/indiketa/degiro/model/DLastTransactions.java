package cat.indiketa.degiro.model;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import lombok.Data;

/**
 *
 * @author indiketa
 */
@Data
public class DLastTransactions {

    private List<DTransaction> transactions;

    @Data
    public static class DTransaction {

        private long id;
        private Calendar date;
        private long productId;
        private String product;
        private int contractType;
        private int contractSize;
        private String currency;
        private DOrderAction buysell;
        private long size;
        private long quantity;
        private BigDecimal price;
        private BigDecimal stopPrice;
        private BigDecimal totalOrderValue;
        private DOrderType orderType;
        private DOrderTime orderTime;
        private boolean isModifiable;
        private boolean isDeletable;

    }

}
