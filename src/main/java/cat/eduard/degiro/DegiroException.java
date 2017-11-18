/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.eduard.degiro;

/**
 *
 * @author casa
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
