/*
 * Copyright (c) 2013, Sam Malone. All rights reserved.
 * 
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 * 
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  - Neither the name of Sam Malone nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package tv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tv.io.LibraryManager;

/**
 *
 * @author Sam Malone
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
     * Returns the canonical pathname string of this abstract pathname of f.
     * If there is an error due to filesystem queries, the absolute path will
     * be returned instead
     * @param f File
     * @return canonical path of f, or absolute path if error
     */
    public static String getCanonicalPath(File f) {
        try {
            return f.getCanonicalPath();
        } catch(IOException e) {
            
        }
        return f.getAbsolutePath();
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
        List<String> matchList = new ArrayList<String>();
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
