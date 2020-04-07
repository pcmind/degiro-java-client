package cat.indiketa.degiro.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author indiketa
 */
@ToString
@EqualsAndHashCode
@Getter
public class DNewOrder {

    private DOrderAction action;
    private DOrderType orderType;
    private DOrderTime timeType;
    private String productId;
    private long size;
    private BigDecimal limitPrice;
    private BigDecimal stopPrice;

    public DNewOrder(DOrderAction action, DOrderType orderType, DOrderTime timeType, String productId, long size, BigDecimal limitPrice, BigDecimal stopPrice) {
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

        final boolean requireLimitPrice = orderType == DOrderType.LIMITED || orderType == DOrderType.LIMITED_STOP_LOSS;
        final boolean requiredStopPrice = orderType == DOrderType.LIMITED_STOP_LOSS || orderType == DOrderType.STOP_LOSS || orderType == DOrderType.TRAILING_STOP;

        if (requireLimitPrice && limitPrice == null) {
            throw new NullPointerException("Limit price is null and order type is " + orderType.name());
        }

        if (requiredStopPrice && stopPrice == null) {
            throw new NullPointerException("Stop price is null and order type is " + orderType.name());
        }

        if (!requireLimitPrice && limitPrice != null) {
            throw new RuntimeException("Stop price only makes sense in STOP_LOSS and LIMITED_STOP_LOSS types. Type was " + orderType.name());
        }

        if (!requiredStopPrice && stopPrice != null) {
            throw new RuntimeException("Limit price only makes sense in LIMITED and LIMITED_STOP_LOSS types. Type was " + orderType.name());
        }
    }
}
