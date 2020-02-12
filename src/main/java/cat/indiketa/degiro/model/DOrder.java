/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.indiketa.degiro.model;

import cat.indiketa.degiro.model.raw.DRawOrders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author indiketa
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DOrder {

    private String id;
    private Calendar date;
    private long productId;
    private String product;
    private int contractType;
    private int contractSize;
    private String currency;
    private DOrderAction buySell;
    private long size;
    private long quantity;
    private BigDecimal price;
    private BigDecimal stopPrice;
    private BigDecimal totalOrderValue;
    private DOrderType orderType;
    private DOrderTime orderTime;
    private boolean isModifiable;
    private boolean isDeletable;

    public DOrder copy() {
        return new DOrder(id, date, productId, product, contractType, contractSize, currency, buySell, size, quantity, price, stopPrice, totalOrderValue, orderType, orderTime, isModifiable, isDeletable);
    }

    public boolean isActive() {
        return size > 0;
    }

    /**
     * Setter used by reflection by {@link cat.indiketa.degiro.utils.DUtils#convertOrder(DRawOrders.Value)}
     */
    public void setIsModifiable(boolean modifiable) {
        isModifiable = modifiable;
    }

    /**
     * Setter used by reflection by {@link cat.indiketa.degiro.utils.DUtils#convertOrder(DRawOrders.Value)}
     */
    public void setIsDeletable(boolean deletable) {
        isDeletable = deletable;
    }
}
