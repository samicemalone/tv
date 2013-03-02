/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.filter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Ice
 */
public class RangeFilter implements FilenameFilter {
    
    private int startEp;
    private int endEp;
    
    public RangeFilter(String startEp, String endEp) {
        this.startEp = Integer.valueOf(startEp);
        this.endEp = Integer.valueOf(endEp);
    }

    @Override
    public boolean accept(File dir, String name) {
        if(!ExtensionFilter.isValid(name)) {
            return false;
        }
        Pattern p = Pattern.compile("[sS][0-9][0-9][eE]([0-9][0-9])|x([0-9][0-9])");
        Matcher m = p.matcher(name);
        if(m.find() && m.groupCount() == 2) {
            int curEp;
            if(m.group(1) == null && m.group(2) != null) {
                curEp = Integer.valueOf(m.group(2));
            } else {
                curEp = Integer.valueOf(m.group(1));
            }
            if(curEp >= startEp && curEp <= endEp) {
                return true;
            }
        }
        return false;
    }
    
}
