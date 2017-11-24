package cat.indiketa.degiro;

/**
 *
 * @author indiketa
 */
public class DegiroException extends Exception {

    public DegiroException() {
    }

    public DegiroException(String message) {
        super(message);
    }

    public DegiroException(String message, Throwable cause) {
        super(message, cause);
    }

    public DegiroException(Throwable cause) {
        super(cause);
    }
    
    
    
}
