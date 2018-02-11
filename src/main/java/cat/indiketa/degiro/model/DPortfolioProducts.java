package cat.indiketa.degiro.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author indiketa
 */
public class DPortfolioProducts {

    private List<DPortfolioProduct> active;
    private List<DPortfolioProduct> inactive;

    public List<DPortfolioProduct> getActive() {
        return active;
    }

    public void setActive(List<DPortfolioProduct> active) {
        this.active = active;
    }

    public List<DPortfolioProduct> getInactive() {
        return inactive;
    }

    public void setInactive(List<DPortfolioProduct> inactive) {
        this.inactive = inactive;
    }

    public static class DPortfolioProduct {

        protected long id;
        protected String product;
        protected long size;
        protected BigDecimal price;
        protected BigDecimal realPrice;
        protected BigDecimal change;
        protected BigDecimal value;
        protected BigDecimal realValue;
        protected Date lastUpdate;
        protected String currency;
        protected String exchangeBriefCode;
        protected long contractSize;
        protected boolean closedToday;
        protected String productCategory;
        protected boolean tradable;
        protected BigDecimal closePrice;
        protected BigDecimal plBase;
        protected BigDecimal todayPlBase;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
            calcRealPrice();
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void calcRealPrice() {
            if (price != null && change != null) {
                int scale = price.scale();
                realPrice = price.subtract(price.multiply(change)).setScale(scale, RoundingMode.HALF_UP);
                realValue = realPrice.multiply(new BigDecimal(size));
            }
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public BigDecimal getChange() {
            return change;
        }

        public void setChange(BigDecimal change) {
            this.change = change;
            calcRealPrice();
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
            calcRealPrice();
        }

        public Date getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(Date lastUpdate) {
            this.lastUpdate = lastUpdate;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getExchangeBriefCode() {
            return exchangeBriefCode;
        }

        public void setExchangeBriefCode(String exchangeBriefCode) {
            this.exchangeBriefCode = exchangeBriefCode;
        }

        public long getContractSize() {
            return contractSize;
        }

        public void setContractSize(long contractSize) {
            this.contractSize = contractSize;
        }

        public boolean isClosedToday() {
            return closedToday;
        }

        public void setClosedToday(boolean closedToday) {
            this.closedToday = closedToday;
        }

        public String getProductCategory() {
            return productCategory;
        }

        public void setProductCategory(String productCategory) {
            this.productCategory = productCategory;
        }

        public boolean isTradable() {
            return tradable;
        }

        public void setTradable(boolean tradable) {
            this.tradable = tradable;
        }

        public BigDecimal getClosePrice() {
            return closePrice;
        }

        public void setClosePrice(BigDecimal closePrice) {
            this.closePrice = closePrice;
        }

        public BigDecimal getPlBase() {
            return plBase;
        }

        public void setPlBase(BigDecimal plBase) {
            this.plBase = plBase;
        }

        public BigDecimal getTodayPlBase() {
            return todayPlBase;
        }

        public void setTodayPlBase(BigDecimal todayPlBase) {
            this.todayPlBase = todayPlBase;
        }

        public BigDecimal getRealPrice() {
            return realPrice;
        }

        public void setRealPrice(BigDecimal realPrice) {
            this.realPrice = realPrice;
        }

        public BigDecimal getRealValue() {
            return realValue;
        }

        public void setRealValue(BigDecimal realValue) {
            this.realValue = realValue;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 17 * hash + (int) (this.id ^ (this.id >>> 32));
            hash = 17 * hash + Objects.hashCode(this.product);
            hash = 17 * hash + (int) (this.size ^ (this.size >>> 32));
            hash = 17 * hash + Objects.hashCode(this.price);
            hash = 17 * hash + Objects.hashCode(this.change);
            hash = 17 * hash + Objects.hashCode(this.value);
            hash = 17 * hash + Objects.hashCode(this.currency);
            hash = 17 * hash + Objects.hashCode(this.exchangeBriefCode);
            hash = 17 * hash + (int) (this.contractSize ^ (this.contractSize >>> 32));
            hash = 17 * hash + (this.closedToday ? 1 : 0);
            hash = 17 * hash + Objects.hashCode(this.productCategory);
            hash = 17 * hash + (this.tradable ? 1 : 0);
            hash = 17 * hash + Objects.hashCode(this.closePrice);
            hash = 17 * hash + Objects.hashCode(this.plBase);
            hash = 17 * hash + Objects.hashCode(this.todayPlBase);
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
            final DPortfolioProduct other = (DPortfolioProduct) obj;
            if (this.id != other.id) {
                return false;
            }
            if (this.size != other.size) {
                return false;
            }
            if (this.change != other.change) {
                return false;
            }
            if (this.contractSize != other.contractSize) {
                return false;
            }
            if (this.closedToday != other.closedToday) {
                return false;
            }
            if (this.tradable != other.tradable) {
                return false;
            }
            if (!Objects.equals(this.product, other.product)) {
                return false;
            }
            if (!Objects.equals(this.currency, other.currency)) {
                return false;
            }
            if (!Objects.equals(this.exchangeBriefCode, other.exchangeBriefCode)) {
                return false;
            }
            if (!Objects.equals(this.productCategory, other.productCategory)) {
                return false;
            }
            if (!Objects.equals(this.price, other.price)) {
                return false;
            }
            if (!Objects.equals(this.value, other.value)) {
                return false;
            }
            if (!Objects.equals(this.closePrice, other.closePrice)) {
                return false;
            }
            if (!Objects.equals(this.plBase, other.plBase)) {
                return false;
            }
            if (!Objects.equals(this.todayPlBase, other.todayPlBase)) {
                return false;
            }
            return true;
        }

    }

}
