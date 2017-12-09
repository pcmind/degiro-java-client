package cat.indiketa.degiro.model;

import java.math.BigDecimal;

/**
 *
 * @author indiketa
 */
public class DNewOrder {

    private DOrderAction action;
    private DOrderType orderType;
    private DOrderTime timeType;
    private long productId;
    private long size;
    private BigDecimal limitPrice;
    private BigDecimal stopPrice;

    public DNewOrder(DOrderAction action, DOrderType orderType, DOrderTime timeType, long productId, long size, BigDecimal limitPrice, BigDecimal stopPrice) {
        this.action = action;
        this.orderType = orderType;
        this.timeType = timeType;
        this.productId = productId;
        this.size = size;
        this.limitPrice = limitPrice;
        this.stopPrice = stopPrice;

        if (action == null) {
            throw new NullPointerException("DOrderAction is null");
        }

        if (orderType == null) {
            throw new NullPointerException("DOrderType is null");
        }

        if (timeType == null) {
            throw new NullPointerException("DTimeType is null");
        }

        if ((orderType == DOrderType.LIMITED || orderType == DOrderType.LIMITED_STOP_LOSS) && limitPrice == null) {
            throw new NullPointerException("Limit price is null and order type is limited");
        }

        if ((orderType == DOrderType.STOP_LOSS || orderType == DOrderType.LIMITED_STOP_LOSS) && limitPrice == null) {
            throw new NullPointerException("Stop price is null and order type is stop");
        }

        if (orderType != DOrderType.STOP_LOSS && orderType != DOrderType.LIMITED_STOP_LOSS && stopPrice != null) {
            throw new RuntimeException("Stop price only makes sense in STOP_LOSS and LIMITED_STOP_LOSS types. Type was " + orderType.name());
        }
        
        if (orderType != DOrderType.LIMITED && orderType != DOrderType.LIMITED_STOP_LOSS && stopPrice != null) {
            throw new RuntimeException("Limit price only makes sense in LIMITED and LIMITED_STOP_LOSS types. Type was " + orderType.name());
        }

        if ((orderType == DOrderType.STOP_LOSS || orderType == DOrderType.LIMITED_STOP_LOSS) && limitPrice == null) {
            throw new NullPointerException("Stop price is null and order type is stop");
        }
    }

    public DOrderAction getAction() {
        return action;
    }

    public void setAction(DOrderAction action) {
        this.action = action;
    }

    public DOrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(DOrderType orderType) {
        this.orderType = orderType;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public DOrderTime getTimeType() {
        return timeType;
    }

    public void setTimeType(DOrderTime timeType) {
        this.timeType = timeType;
    }

    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(BigDecimal limitPrice) {
        this.limitPrice = limitPrice;
    }

    public BigDecimal getStopPrice() {
        return stopPrice;
    }

    public void setStopPrice(BigDecimal stopPrice) {
        this.stopPrice = stopPrice;
    }

}
