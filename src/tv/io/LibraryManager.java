/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author Ice
 */
public class LibraryManager {
    
    /**
     * Check if the current operating system is Windows 7
     * @return true if the OS is Windows 7, false otherwise
     */
    public static boolean isWindows7() {
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        return "Windows 7".equals(osName) && "6.1".equals(osVersion);
    }
    
    /**
     * Check if the current operating system is running Windows
     * @return  true if OS is windows, false otherwise
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }
    
    /**
     * Check if the given library name is valid and exists
     * @param libraryName Windows 7 Libarary Name
     * @return true if libraryName is valid and exists, false otherwise
     */
    public static boolean isValidLibraryName(String libraryName) {
        return getLibraryPath(libraryName) != null;
    }
    
    /**
     * Gets the full path the the Windows library-ms file with the given name
     * @param libraryName Windows 7 Library Name
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
     * @param libraryName Windows 7 Library Name
     * @return List of paths of the folders in the given library, or an empty list
     */
    public static List<String> parseLibraryFolders(String libraryName) {
        List<String> folders = new ArrayList<String>();
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
