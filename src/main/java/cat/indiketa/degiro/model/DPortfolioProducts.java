package cat.indiketa.degiro.model;

import java.math.BigDecimal;
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
        protected long change;
        protected BigDecimal value;
        protected Date lastUpdate;
        protected String currency;
        protected String exchangeBriefCode;
        protected long contractSize;
        protected boolean closedToday;
        protected String productCategory;
        protected boolean tradable;
        protected BigDecimal closePrice;

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
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public long getChange() {
            return change;
        }

        public void setChange(long change) {
            this.change = change;
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
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

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 29 * hash + (int) (this.id ^ (this.id >>> 32));
            hash = 29 * hash + (int) (this.size ^ (this.size >>> 32));
            hash = 29 * hash + Objects.hashCode(this.price);
            hash = 29 * hash + Objects.hashCode(this.value);
            hash = 29 * hash + Objects.hashCode(this.lastUpdate);
            hash = 29 * hash + Objects.hashCode(this.productCategory);
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
            if (this.tradable != other.tradable) {
                return false;
            }
            if (!Objects.equals(this.price, other.price)) {
                return false;
            }
            if (!Objects.equals(this.value, other.value)) {
                return false;
            }
            if (!Objects.equals(this.lastUpdate, other.lastUpdate)) {
                return false;
            }
            return true;
        }
        
        

    }

}
