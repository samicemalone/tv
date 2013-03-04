/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import tv.io.LibraryManager;

/**
 *
 * @author Ice
 */
public class MediaInfo {
    
    private static File executable;
    private List<String> filePaths;
    
    public MediaInfo() {
        filePaths = new ArrayList<String>();
    }
    
    public static File getExecutableFile() {
        if(executable == null) {
            if(LibraryManager.isWindows()) {
                executable = new File("C:/Program Files/MediaInfo/MediaInfo.exe");
            } else {
                executable = new File("/usr/bin/mediainfo");
            }
        }
        return executable;
    }
    
    public static void setExecutableFile(File binary) {
        executable = binary;
    }
    
    public void addFile(String file) {
        filePaths.add(file);
    }

    /**
     * Get the length of the media files in filePaths.
     * mediainfo binary is executed from this method.
     * @return Length in seconds. 0 if filePaths is empty.
     */
    public long getLength() {
        long lengthMS = 0;
        if(filePaths.isEmpty()) {
            return 0;
        }
        List<String> commands = new ArrayList<String>();
        commands.add(executable.getAbsolutePath());
        commands.add("--Output=Video;%Duration%\\r\\n");
        commands.addAll(filePaths);
        try {
            Process process = new ProcessBuilder(commands).start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if(!line.isEmpty()) {
                    lengthMS += parseLong(line, 0);
                }
            }
        } catch (IOException e) {
            
        }
        return (lengthMS / 1000);
    }
    
    /**
     * Utility method for assigning a default long value if the
     * String val cannot be parsed into a long.
     * @param val String value to be parsed
     * @param defaultVal Value to return if val cannot be parsed
     * @return defaultVal if val cannot be parsed as long, otherwise the parsed
     * value of val will be returned
     */
    private long parseLong(String val, long defaultVal) {
        long longVal = defaultVal;
        try {
            longVal = Long.valueOf(val);
        } catch (Exception e) {
            
        }
        return longVal;
    }
    
}
