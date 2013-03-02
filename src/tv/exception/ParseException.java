/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.exception;

/**
 *
 * @author Ice
 */
public class ParseException extends ExitException {

    public ParseException(String message, int exitCode) {
        super(message, exitCode);
    }
    
}
