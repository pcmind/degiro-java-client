package cat.indiketa.degiro.model;

/**
 *
 * @author indiketa
 */
public class DPlacedOrder {

    private int status;
    private String statusText;
    private String orderId;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

}
