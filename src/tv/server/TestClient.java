/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.SocketFactory;

/**
 *
 * @author Ice
 */
public class TestClient {
    
    public static void main(String args[]) {
        //execute("vlc Friends s04e21 -i");        
        System.out.println();
        ArrayList<String> list = parse(execute("list_stored_eps"));
        for(int i = 0; i < list.size(); i++){
            System.out.println(list.get(i));
        }
    }
    
    private static String execute(String command) {
        StringBuilder sb = new StringBuilder();
        try {
            Socket s = SocketFactory.getDefault().createSocket(Inet4Address.getByAddress(new byte[] {127,0,0,1}), 5768);
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            String line;
            out.println(command);
            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
            while ((line = r.readLine()) != null) {
                sb.append(line);
            }
            out.close();
            r.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return sb.toString();
    }
    
    private static final Pattern CSV_PARSER = Pattern.compile("\"([^\"]+?)\",?|([^,]+),?|,");
    
    public static ArrayList<String> parse(String csv) {
        ArrayList<String> list = new ArrayList<String>();
        Matcher m = CSV_PARSER.matcher(csv);
        while (m.find()) {
            String match = m.group();
            if (match == null) {
                break;
            }
            if (match.endsWith(",")) {  // trim trailing ,
                match = match.substring(0, match.length() - 1);
            }
            if (match.startsWith("\"")) { // assume also ends with
                match = match.substring(1, match.length() - 1);
            }
            if (match.length() == 0) {
                match = "";
            }
            list.add(match);
        }
        return list;
    }
    
}
