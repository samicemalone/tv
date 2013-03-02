/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.filter;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author Ice
 */
public class ExtensionFilter implements FilenameFilter {
    
    public static final String[] FORMATS = {"mkv", "avi", "mp4", "mpg"};
    
    public static boolean isValid(String name) {
        for(int i = 0; i < FORMATS.length; i++) {
            if(name.toLowerCase().endsWith(FORMATS[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean accept(File dir, String name) {
        return isValid(name);
    }
}
