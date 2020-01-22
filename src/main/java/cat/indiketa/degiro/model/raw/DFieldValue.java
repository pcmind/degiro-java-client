package cat.indiketa.degiro.model.raw;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DFieldValue {
    private String name;
    private Object value;
    private Boolean isAdded;
}
