package cat.indiketa.degiro.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DLastTransaction {

    private long id;
    private LocalDateTime date;
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

    /**
     * @see cat.indiketa.degiro.json.DUpdatesDeserializer
     */
    public void setIsModifiable(boolean modifiable) {
        isModifiable = modifiable;
    }

    /**
     * @see cat.indiketa.degiro.json.DUpdatesDeserializer
     */
    public void setIsDeletable(boolean deletable) {
        isDeletable = deletable;
    }
}
