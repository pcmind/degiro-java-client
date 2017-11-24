package cat.indiketa.degiro.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author indiketa
 */
public class DPortfolio {

    public List<DProductRow> active;
    public List<DProductRow> inactive;

    public List<DProductRow> getActive() {
        return active;
    }

    public void setActive(List<DProductRow> active) {
        this.active = active;
    }

    public List<DProductRow> getInactive() {
        return inactive;
    }

    public void setInactive(List<DProductRow> inactive) {
        this.inactive = inactive;
    }

    public static class DProductRow {

        private long id;
        private String product;
        private long size;
        private BigDecimal price;
        private long change;
        private BigDecimal value;
        private Date lastUpdate;
        private String currency;
        private String exchangeBriefCode;
        private long contractSize;
        private boolean closedToday;
        private String productCategory;
        private boolean tradable;
        private BigDecimal closePrice;

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

    }

}
