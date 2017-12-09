package cat.indiketa.degiro.model;

/**
 *
 * @author indiketa
 */
public class DOrderConfirmation {

    private String confirmationId;
    private double freeSpaceNew;
    private String message;
    private long status;
    private String statusText;

    public String getConfirmationId() {
        return confirmationId;
    }

    public void setConfirmationId(String confirmationId) {
        this.confirmationId = confirmationId;
    }

    public double getFreeSpaceNew() {
        return freeSpaceNew;
    }

    public void setFreeSpaceNew(double freeSpaceNew) {
        this.freeSpaceNew = freeSpaceNew;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
    
    

}
