/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import tv.io.CSV_IO;
import tv.io.TVDBManager;
import tv.model.Episode;

/**
 *
 * @author Ice
 */
public class VLCServer {
    
    private final static int SERVER_PORT = 5768;
    private boolean isRunning = true;
    private ArrayList<Episode> episodeList;
    private TVDBManager io;
    private ServerSocket serverSocket;
    
    public VLCServer() {
        io = new TVDBManager();
    }
    
    public void start(){
        try {
            serverSocket = ServerSocketFactory.getDefault().createServerSocket(SERVER_PORT);
            while(isRunning) {
                try {
                    episodeList = io.readAllStorage();
                } catch (FileNotFoundException ex) {
                    episodeList = new ArrayList<Episode>();
                }
                Socket client = serverSocket.accept();
                new Thread(new WorkerThread(client)).start();
            }
        } catch (IOException ex) {
            
        }
    }

    public void shutdown() {
        try {
            Socket s = SocketFactory.getDefault().createSocket(Inet4Address.getByName("127.0.0.1"), 5768);
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            out.println("shutdown");
            out.close();
            s.close();
        } catch (IOException ex) {
            
        }
    }
    
    private class WorkerThread implements Runnable {
        
        private Socket client;
        
        public WorkerThread(Socket client) {
            this.client = client;
        }
        
        public void csvStringToArray(ArrayList<String> matchList, String command) {
            Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
            Matcher regexMatcher = regex.matcher(command);
            while (regexMatcher.find()) {
                if (regexMatcher.group(1) != null) {
                    // Add double-quoted string without the quotes
                    matchList.add(regexMatcher.group(1));
                } else if (regexMatcher.group(2) != null) {
                    // Add single-quoted string without the quotes
                    matchList.add(regexMatcher.group(2));
                } else {
                    // Add unquoted word
                    matchList.add(regexMatcher.group());
                }
            }
        }

        @Override
        public void run() {
            try {
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                BufferedReader r = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String command = r.readLine();
                if(command.equals("shutdown")) {
                    isRunning = false;
                    serverSocket.close();
                }
                if(command.startsWith("vlc")) {
                    ArrayList<String> matchList = new ArrayList<String>();
                    csvStringToArray(matchList, command);
                    String[] args = new String[matchList.size() + 2]; //size - 1 + 3
                    args[0] = "C:\\Program Files (x86)\\Java\\jdk1.6.0_16\\bin\\java.exe";
                    args[1] = "-jar";
                    args[2] = "J:\\Downloads\\Dev\\Java\\vlc\\dist\\vlc.jar";
                    for(int i = 1; i < matchList.size(); i++) {
                        args[i+2] = matchList.get(i);
                    }
                    BufferedReader br = null;
                    ProcessBuilder pb = new ProcessBuilder(args);
                    pb.redirectErrorStream(true);
                    Process p = pb.start();
                    try {
                        br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line;
                        boolean hasOutput = false;
                        while((line = br.readLine()) != null) {
                            hasOutput = true;
                            out.println(line);
                        }
                        if(p.waitFor() == 0 && !hasOutput) {
                            out.println("OK");
                        }
                    } catch (InterruptedException ex) {

                    } finally {
                        br.close();
                    }
                }
                if(command.startsWith("get_show_name")) {
                    File f = new File(command.substring("get_show_name".length()+1));
                    if(f.getParent() != null) {
                        f = new File(f.getParent()).getParentFile();
                    }
                    if(f == null) {
                        out.println();
                    } else {
                        out.println(f.getName());
                    }
                }
                if(command.equals("list_shows")) {
                    out.println(io.getCSVShows());
                }
                if(command.equals("list_stored_eps")) {
                    out.println(io.getCSVEpisodes(episodeList));
                }
                r.close();
                out.close();
                client.close();
            } catch (IOException ex) {
                
            }
        }
        
    }
    
}
