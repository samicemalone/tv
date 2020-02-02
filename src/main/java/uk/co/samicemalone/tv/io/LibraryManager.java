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
package uk.co.samicemalone.tv.io;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sam Malone
 */
public class LibraryManager {
    
    /**
     * Check if the current operating system supports libraries (Windows 7/8)
     * @return true if the OS is Windows 7/8, false otherwise
     */
    public static boolean hasLibrarySupport() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }
    
    /**
     * Check if the current operating system is running Windows
     * @return  true if OS is windows, false otherwise
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }
    
    /**
     * Check if the given library name is valid and exists
     * @param libraryName Windows 7/8 Library Name
     * @return true if libraryName is valid and exists, false otherwise
     */
    public static boolean isValidLibraryName(String libraryName) {
        return getLibraryPath(libraryName) != null;
    }
    
    /**
     * Gets the full path the the Windows library-ms file with the given name
     * @param libraryName Windows 7/8 Library Name
     * @return Full path to the given library name or null if invalid/doesn't exist
     */
    private static String getLibraryPath(String libraryName) {
        String path = null;
        try {
            path = System.getenv("APPDATA");
            path = path +  "\\Microsoft\\Windows\\Libraries\\" + libraryName + ".library-ms";
            if(!new File(path).exists()) {
                return null;
            }
        } catch (Exception e) {
            
        }
        return path;
    }
    
    /**
     * Gets a list of full paths to the folders that make up the library
     * @param libraryName Windows 7/8 Library Name
     * @return List of paths of the folders in the given library, or an empty list
     */
    public static List<String> parseLibraryFolders(String libraryName) {
        List<String> folders = new ArrayList<>();
        String libraryPath = getLibraryPath(libraryName);
        if(libraryPath == null) {
            return folders;
        }
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        try {
            Document doc = domFactory.newDocumentBuilder().parse(libraryPath);
            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xpath.compile("/libraryDescription/searchConnectorDescriptionList/searchConnectorDescription/simpleLocation/url/text()");
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                folders.add(nodes.item(i).getNodeValue()); 
            }
        } catch (Exception e) {
            
        }
        return folders;
    }
    
}
