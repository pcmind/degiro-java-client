package cat.indiketa.degiro.model;

import java.math.BigDecimal;
import java.util.Calendar;

public interface DOrderInterface {

    String getId();

    DOrderAction getBuySell();

    Boolean getIsDeletable();

    void setBuySell(DOrderAction buySell);

    void setOrderTime(DOrderTime orderTime);

    void setDate(Calendar date);

    void setSize(Long size);

    void setProductId(Long productId);

    Calendar getDate();

    void setStopPrice(BigDecimal stopPrice);

    Long getProductId();

    String getProduct();

    void setContractSize(Integer contractSize);

    void setCurrency(String currency);

    Integer getContractType();

    Integer getContractSize();

    String getCurrency();

    DOrderType getOrderType();

    void setOrderType(DOrderType orderType);

    Boolean getIsModifiable();

    void setId(String id);

    BigDecimal getPrice();

    void setPrice(BigDecimal price);

    void setContractType(Integer contractType);

    Long getSize();

    BigDecimal getTotalOrderValue();

    void setTotalOrderValue(BigDecimal totalOrderValue);

    DOrderTime getOrderTime();

    Long getQuantity();

    void setProduct(String product);

    void setQuantity(Long quantity);

    BigDecimal getStopPrice();

    DOrder copy();

    boolean isActive();

    void setIsModifiable(boolean modifiable);

    void setIsDeletable(boolean deletable);
}
