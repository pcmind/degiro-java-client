package cat.indiketa.degiro.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author indiketa
 */
@Data
public class DCashMovements implements IValidable {

    private List<DCashMovement> cashMovements = new ArrayList<>();

    @Override
    public boolean isInvalid() {
        if (cashMovements == null) {
            return true;
        }
        for (DCashMovement datum : cashMovements) {
            if (datum.isInvalid()) {
                return true;
            }
        }
        return false;
    }

}
