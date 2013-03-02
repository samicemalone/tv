/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.exception;

/**
 *
 * @author Ice
 */
public class MissingArgumentException extends ExitException {

    public MissingArgumentException(String string, int exitCode) {
        super(string, exitCode);
    }
    
}
