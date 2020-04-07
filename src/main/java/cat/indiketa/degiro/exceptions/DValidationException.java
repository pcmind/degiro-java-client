package cat.indiketa.degiro.exceptions;

public class DValidationException extends DeGiroException {
    public DValidationException() {
    }

    public DValidationException(String message) {
        super(message);
    }

    public DValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DValidationException(Throwable cause) {
        super(cause);
    }
}
