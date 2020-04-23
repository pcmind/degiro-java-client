package cat.indiketa.degiro.model;

import lombok.Data;

/**
 *
 * @author indiketa
 */
@Data
public class DPlacedOrder implements IValidable {

    private int status;
    private String statusText;
    private String orderId;

    @Override
    public boolean isInvalid() {
        //on creation orderId is filled but not on confirmation or change
        return false;
    }
}
