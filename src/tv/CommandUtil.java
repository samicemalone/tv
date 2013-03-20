/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tv.io.LibraryManager;

/**
 *
 * @author Ice
 */
public class CommandUtil {
    
    /**
     * Get the path of Jar file
     * @return Path to Jar file
     * @throws SecurityException if unable to get protection domain
     */
    public static String getJarPath() throws SecurityException {
        return new File(CommandUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
    }
    
    /**
     * Gets a platform independent binary name. i.e. adds .exe for windows
     * @param path Path/Root of the binary path e.g. "bin/java" = "bin/java.exe"
     * @return path with .exe suffix if windows, path otherwise
     */
    public static String getBinary(String path) {
        if(LibraryManager.isWindows()) {
            return path.concat(".exe");
        }
        return path;
    }
    
    /**
     * Builds a command string that can launch java and execute the currently 
     * running jar file with the arguments given in dequotedArgsList
     * @param dequotedArgsList List of dequoted arguments to pass to jar
     * @return command string or null if error
     */
    public static String[] buildJavaCommandString(List<String> dequotedArgsList) {
        String[] args = new String[dequotedArgsList.size() + 2]; //size - 1 + 3
        args[0] = new File(System.getProperty("java.home"), getBinary("bin/java")).getAbsolutePath();
        args[1] = "-jar";
        try {
            args[2] = getJarPath();
        } catch(SecurityException e) {
            return null;
        }
        for(int i = 1; i < dequotedArgsList.size(); i++) { // skip [0] = tv
            args[i+2] = dequotedArgsList.get(i);
        }
        return args;
    }
    
    /**
     * Removes the quotes from the command string and add adds each
     * argument to a list.
     * @param command Command String e.g. vlc "Modern Family" next
     * @return List of dequotes arguments from command or empty list
     */
    public static List<String> dequoteArgsToList(String command) {
        List<String> matchList = new ArrayList();
        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher regexMatcher = regex.matcher(command);
        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                matchList.add(regexMatcher.group(1)); // Add dequoted "
            } else if (regexMatcher.group(2) != null) {
                matchList.add(regexMatcher.group(2)); // Add dequoted '
            } else {
                matchList.add(regexMatcher.group()); // Add unquoted word
            }
        }
        return matchList;
    }
    
}
