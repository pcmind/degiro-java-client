package cat.indiketa.degiro.model;

import lombok.Data;

/**
 *
 * @author indiketa
 */
@Data
public class DPlacedOrder {

    private int status;
    private String statusText;
    private String orderId;

}
