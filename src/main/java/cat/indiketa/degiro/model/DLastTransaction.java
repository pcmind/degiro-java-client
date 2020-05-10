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
public class DLastTransaction implements DCopyable<DLastTransaction> {

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

    @Override
    public DLastTransaction copy() {
        return withId(id);
    }
}
