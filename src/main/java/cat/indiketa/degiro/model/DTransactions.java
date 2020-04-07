package cat.indiketa.degiro.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author indiketa
 */
@Data
public class DTransactions implements IValidable {

    private List<DTransaction> data = new ArrayList<>();
    private long status;
    private String statusText;

    @Override
    public boolean isInvalid() {
        if (data == null) {
            return true;
        }
        for (DTransaction datum : data) {
            if (datum.isInvalid()) {
                return true;
            }
        }
        return false;
    }

}
