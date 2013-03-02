/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv;

import java.text.DecimalFormat;

/**
 *
 * @author Ice
 */
public class MediaUtil {
    
    /**
     * Converts the given size (in bytes) to a human readable format.
     * The format is  ###0.## [unit]. E.g. 1012.12 MB, 123.45 GB
     * @param size Size in bytes
     * @return Formatted size String in ###0.## [unit] format.
     */
    public static String readableFileSize(long size) {
        if(size <= 0) {
            return "0";
        }
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("###0.##").format(size/Math.pow(1024, digitGroups)) + ' ' + units[digitGroups];
    }
    
    /**
     * Converts the given length (in seconds) to a human readable format.
     * The format is hh:mm:ss.
     * @param length Length in seconds
     * @return Formatted Length String in hh:mm:ss format.
     */
    public static String readableLength(long length) {
        String seconds = Integer.toString((int)(length % 60));  
        String minutes = Integer.toString((int)((length % 3600) / 60));  
        String hours = Integer.toString((int)(length / 3600));
        if (seconds.length() < 2) {  
            seconds = "0" + seconds;  
        }  
        if (minutes.length() < 2) {  
            minutes = "0" + minutes;  
        }  
        if (hours.length() < 2) {  
            hours = "0" + hours;  
        }
        return String.format("%s:%s:%s", hours, minutes, seconds);
    }
    
}
