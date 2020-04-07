package cat.indiketa.degiro.model;

import lombok.Data;

import java.util.Map;

/**
 *
 * @author indiketa
 */
@Data
public class DProductDescriptions implements IValidable {

    private Map<Long, DProductDescription> data;

    @Override
    public boolean isInvalid() {
        return data == null;
    }
}
