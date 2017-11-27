/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.indiketa.degiro.model;

import java.util.List;

/**
 *
 * @author indiketa
 */
public class DTransactions {

    private List<DTransaction> data = null;
    private long status;
    private String statusText;

    public List<DTransaction> getData() {
        return data;
    }

    public void setData(List<DTransaction> data) {
        this.data = data;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public static class DTransaction {

        private long id;
        private long productId;
        private String date;
        private String buysell;
        private double price;
        private long quantity;
        private double total;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getProductId() {
            return productId;
        }

        public void setProductId(long productId) {
            this.productId = productId;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getBuysell() {
            return buysell;
        }

        public void setBuysell(String buysell) {
            this.buysell = buysell;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public long getQuantity() {
            return quantity;
        }

        public void setQuantity(long quantity) {
            this.quantity = quantity;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

    }

}
