package cat.indiketa.degiro.model;

import lombok.Data;

@Data
public class DvwdSessionId implements IValidable {
    private String sessionId;

    @Override
    public boolean isInvalid() {
        return sessionId == null;
    }
}
