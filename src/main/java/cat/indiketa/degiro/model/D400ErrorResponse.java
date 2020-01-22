package cat.indiketa.degiro.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

//{"errors":[{"text":"O preço introduzido 1000.0000 é muito alto quando comparado com o último preço 158.9600. O limite superior é 190.7520."}]}
@Data
public class D400ErrorResponse {
    private List<DErrorMessage> errors = new ArrayList<>();

    public String getErrorsToString() {
        if (errors != null && errors.size() == 1) {
            return errors.get(0).text;
        }
        return errors.toString();
    }

    @Data
    public static class DErrorMessage {
        private String text;
    }
}
