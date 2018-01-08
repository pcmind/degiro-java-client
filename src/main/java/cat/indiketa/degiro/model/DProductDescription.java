package cat.indiketa.degiro.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author indiketa
 */
public class DProductDescription {

    private String feedQuality;
    private long orderBookDepth;
    private String vwdIdentifierType;
    private String vwdId;
    private boolean qualitySwitchable;
    private boolean qualitySwitchFree;
    private long vwdModuleId;
    private long id;
    private String name;
    private String isin;
    private double contractSize;
    private String exchangeId;
    private String symbol;
    private DProductType productTypeId;
    private boolean tradable;
    private List<DOrderTime> orderTimeTypes = null;
    private boolean gtcAllowed;
    private List<DOrderType> buyOrderTypes = null;
    private List<DOrderType> sellOrderTypes = null;
    private boolean marketAllowed;
    private boolean limitHitOrderAllowed;
    private boolean stoplossAllowed;
    private boolean stopLimitOrderAllowed;
    private boolean joinOrderAllowed;
    private boolean trailingStopOrderAllowed;
    private boolean combinedOrderAllowed;
    private boolean sellAmountAllowed;
    private boolean isFund;
    private double closePrice;
    private Date closePriceDate;
    private String category;
    private String currency;

    public String getFeedQuality() {
        return feedQuality;
    }

    public void setFeedQuality(String feedQuality) {
        this.feedQuality = feedQuality;
    }

    public long getOrderBookDepth() {
        return orderBookDepth;
    }

    public void setOrderBookDepth(long orderBookDepth) {
        this.orderBookDepth = orderBookDepth;
    }

    public String getVwdIdentifierType() {
        return vwdIdentifierType;
    }

    public void setVwdIdentifierType(String vwdIdentifierType) {
        this.vwdIdentifierType = vwdIdentifierType;
    }

    public String getVwdId() {
        return vwdId;
    }

    public void setVwdId(String vwdId) {
        this.vwdId = vwdId;
    }

    public boolean isQualitySwitchable() {
        return qualitySwitchable;
    }

    public void setQualitySwitchable(boolean qualitySwitchable) {
        this.qualitySwitchable = qualitySwitchable;
    }

    public boolean isQualitySwitchFree() {
        return qualitySwitchFree;
    }

    public void setQualitySwitchFree(boolean qualitySwitchFree) {
        this.qualitySwitchFree = qualitySwitchFree;
    }

    public long getVwdModuleId() {
        return vwdModuleId;
    }

    public void setVwdModuleId(long vwdModuleId) {
        this.vwdModuleId = vwdModuleId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public double getContractSize() {
        return contractSize;
    }

    public void setContractSize(double contractSize) {
        this.contractSize = contractSize;
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public DProductType getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(DProductType productTypeId) {
        this.productTypeId = productTypeId;
    }

    public boolean isTradable() {
        return tradable;
    }

    public void setTradable(boolean tradable) {
        this.tradable = tradable;
    }

    public List<DOrderTime> getOrderTimeTypes() {
        return orderTimeTypes;
    }

    public void setOrderTimeTypes(List<DOrderTime> orderTimeTypes) {
        this.orderTimeTypes = orderTimeTypes;
    }

    public boolean isGtcAllowed() {
        return gtcAllowed;
    }

    public void setGtcAllowed(boolean gtcAllowed) {
        this.gtcAllowed = gtcAllowed;
    }

    public List<DOrderType> getBuyOrderTypes() {
        return buyOrderTypes;
    }

    public void setBuyOrderTypes(List<DOrderType> buyOrderTypes) {
        this.buyOrderTypes = buyOrderTypes;
    }

    public List<DOrderType> getSellOrderTypes() {
        return sellOrderTypes;
    }

    public void setSellOrderTypes(List<DOrderType> sellOrderTypes) {
        this.sellOrderTypes = sellOrderTypes;
    }

    public boolean isMarketAllowed() {
        return marketAllowed;
    }

    public void setMarketAllowed(boolean marketAllowed) {
        this.marketAllowed = marketAllowed;
    }

    public boolean isLimitHitOrderAllowed() {
        return limitHitOrderAllowed;
    }

    public void setLimitHitOrderAllowed(boolean limitHitOrderAllowed) {
        this.limitHitOrderAllowed = limitHitOrderAllowed;
    }

    public boolean isStoplossAllowed() {
        return stoplossAllowed;
    }

    public void setStoplossAllowed(boolean stoplossAllowed) {
        this.stoplossAllowed = stoplossAllowed;
    }

    public boolean isStopLimitOrderAllowed() {
        return stopLimitOrderAllowed;
    }

    public void setStopLimitOrderAllowed(boolean stopLimitOrderAllowed) {
        this.stopLimitOrderAllowed = stopLimitOrderAllowed;
    }

    public boolean isJoinOrderAllowed() {
        return joinOrderAllowed;
    }

    public void setJoinOrderAllowed(boolean joinOrderAllowed) {
        this.joinOrderAllowed = joinOrderAllowed;
    }

    public boolean isTrailingStopOrderAllowed() {
        return trailingStopOrderAllowed;
    }

    public void setTrailingStopOrderAllowed(boolean trailingStopOrderAllowed) {
        this.trailingStopOrderAllowed = trailingStopOrderAllowed;
    }

    public boolean isCombinedOrderAllowed() {
        return combinedOrderAllowed;
    }

    public void setCombinedOrderAllowed(boolean combinedOrderAllowed) {
        this.combinedOrderAllowed = combinedOrderAllowed;
    }

    public boolean isSellAmountAllowed() {
        return sellAmountAllowed;
    }

    public void setSellAmountAllowed(boolean sellAmountAllowed) {
        this.sellAmountAllowed = sellAmountAllowed;
    }

    public boolean isIsFund() {
        return isFund;
    }

    public void setIsFund(boolean isFund) {
        this.isFund = isFund;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    public Date getClosePriceDate() {
        return closePriceDate;
    }

    public void setClosePriceDate(Date closePriceDate) {
        this.closePriceDate = closePriceDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.isin);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DProductDescription other = (DProductDescription) obj;
        if (!Objects.equals(this.isin, other.isin)) {
            return false;
        }
        return true;
    }
    
    

}
