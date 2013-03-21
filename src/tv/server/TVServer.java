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
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import tv.CommandUtil;
import tv.io.TVDBManager;
import tv.model.Episode;

/**
 *
 * @author Ice
 */
public class TVServer {
    
    private final static int SERVER_PORT = 5768;
    private boolean isRunning = true;
    private ArrayList<Episode> episodeList;
    private TVDBManager io;
    private ServerSocket serverSocket;
    
    public TVServer() {
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
        
        /**
         * Spawns a new process to execute the tv commands in cmdArray.
         * The new process has its input stream redirected into the given
         * PrintWriter (stdout).
         * @param cmdArray Command Array containing path to java, jar, tv args
         * @param stdout PrintWriter to output the redirected input from the
         * new process
         */
        private void spawn(String[] cmdArray, PrintWriter stdout) {
            try {
                BufferedReader br = null;
                ProcessBuilder pb = new ProcessBuilder(cmdArray);
                pb.redirectErrorStream(true);
                Process p = pb.start();
                try {
                    br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    boolean hasOutput = false;
                    while((line = br.readLine()) != null) {
                        hasOutput = true;
                        stdout.println(line);
                    }
                    if(p.waitFor() == 0 && !hasOutput) {
                        stdout.println("OK");
                    }
                } catch (InterruptedException ex) {

                } finally {
                    br.close();
                }
            } catch (IOException e) {
                
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
                if(command.startsWith("tv ")) {
                    String[] cmdArray = CommandUtil.buildJavaCommandString(CommandUtil.dequoteArgsToList(command));
                    if(cmdArray == null) {
                        out.println("Unable to determine location of Jar file");
                    } else {
                        spawn(cmdArray, out);
                    }
                }
                if(command.startsWith("get_show_name ")) {
                    File f = new File(command.substring("get_show_name ".length()));
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
