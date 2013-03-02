/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.exception;

/**
 *
 * @author Ice
 */
public class FileNotFoundException extends ExitException {
    
    public FileNotFoundException(String message, int exitCode) {
        super(message, exitCode);
    }
    
}
