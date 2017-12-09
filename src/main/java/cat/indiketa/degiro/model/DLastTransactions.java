package cat.indiketa.degiro.model;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author indiketa
 */
public class DLastTransactions {

    private List<DTransaction> transactions;

    public List<DTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<DTransaction> transactions) {
        this.transactions = transactions;
    }

    public static class DTransaction {

        private long id;
        private Calendar date;
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

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public Calendar getDate() {
            return date;
        }

        public void setDate(Calendar date) {
            this.date = date;
        }

        public long getProductId() {
            return productId;
        }

        public void setProductId(long productId) {
            this.productId = productId;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public int getContractType() {
            return contractType;
        }

        public void setContractType(int contractType) {
            this.contractType = contractType;
        }

        public int getContractSize() {
            return contractSize;
        }

        public void setContractSize(int contractSize) {
            this.contractSize = contractSize;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public DOrderAction getBuysell() {
            return buysell;
        }

        public void setBuysell(DOrderAction buysell) {
            this.buysell = buysell;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public long getQuantity() {
            return quantity;
        }

        public void setQuantity(long quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public BigDecimal getStopPrice() {
            return stopPrice;
        }

        public void setStopPrice(BigDecimal stopPrice) {
            this.stopPrice = stopPrice;
        }

        public BigDecimal getTotalOrderValue() {
            return totalOrderValue;
        }

        public void setTotalOrderValue(BigDecimal totalOrderValue) {
            this.totalOrderValue = totalOrderValue;
        }

        public DOrderType getOrderType() {
            return orderType;
        }

        public void setOrderType(DOrderType orderType) {
            this.orderType = orderType;
        }

        public DOrderTime getOrderTime() {
            return orderTime;
        }

        public void setOrderTime(DOrderTime orderTime) {
            this.orderTime = orderTime;
        }

        public boolean isIsModifiable() {
            return isModifiable;
        }

        public void setIsModifiable(boolean isModifiable) {
            this.isModifiable = isModifiable;
        }

        public boolean isIsDeletable() {
            return isDeletable;
        }

        public void setIsDeletable(boolean isDeletable) {
            this.isDeletable = isDeletable;
        }

    }

}
