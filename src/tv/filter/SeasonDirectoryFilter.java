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
public class SeasonDirectoryFilter implements FilenameFilter {
    
    public static String REGEX = "Season ([0-9]+)";
    
    @Override
    public boolean accept(File dir, String name) {
        return name.matches(REGEX);
    }
}
