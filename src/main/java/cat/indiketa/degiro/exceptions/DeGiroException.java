package cat.indiketa.degiro.exceptions;

/**
 *
 * @author indiketa
 */
public class DeGiroException extends Exception {

    public DeGiroException() {
    }

    public DeGiroException(String message) {
        super(message);
    }

    public DeGiroException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeGiroException(Throwable cause) {
        super(cause);
    }
    
    
    
}
