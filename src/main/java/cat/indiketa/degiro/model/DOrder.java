/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.indiketa.degiro.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author indiketa
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DOrder {

    private String id;
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
    private DOrderTime orderTimeType;
    private boolean isModifiable;
    private boolean isDeletable;

    //delta sync may receive same information in two distinct field... :/
    public void setOrderTypeId(DOrderType type) {
        this.orderType = type;
    }

    //delta sync may receive same information in two distinct field... :/
    public void setOrderTimeTypeId(DOrderTime type) {
        this.orderTimeType = type;
    }

    public DOrder copy() {
        return new DOrder(id, date, productId, product, contractType, contractSize, currency, buysell, size, quantity, price, stopPrice, totalOrderValue, orderType, orderTimeType, isModifiable, isDeletable);
    }

    public boolean isActive() {
        return size > 0;
    }

    /**
     * Setter used by reflection by {@link cat.indiketa.degiro.json.DUpdatesDeserializer#deserialize(JsonElement, Type, JsonDeserializationContext)}
     */
    public void setIsModifiable(boolean modifiable) {
        isModifiable = modifiable;
    }

    /**
     * Setter used by reflection by {@link cat.indiketa.degiro.json.DUpdatesDeserializer#deserialize(JsonElement, Type, JsonDeserializationContext)}
     */
    public void setIsDeletable(boolean deletable) {
        isDeletable = deletable;
    }
}
