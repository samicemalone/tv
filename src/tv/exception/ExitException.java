/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.exception;

/**
 *
 * @author Ice
 */
public class ExitException extends Exception {
    
    public int exitCode;
    
    public ExitException(String message, int exitCode) {
        super(message);
        this.exitCode = exitCode;
    }
    
    public int getExitCode() {
        return exitCode;
    }
    
}
