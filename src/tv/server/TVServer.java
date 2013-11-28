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
import java.util.Arrays;
import java.util.List;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import tv.TV;
import tv.filter.ExtensionFilter;
import tv.io.TVDBManager;
import tv.model.Episode;
import tv.util.CommandUtil;

/**
 *
 * @author Sam Malone
 */
public class TVServer {
    
    private final static int SERVER_PORT = 5768;
    private boolean isRunning = true;
    private ArrayList<Episode> episodeList;
    private final TVDBManager io;
    private ServerSocket serverSocket;
    
    public TVServer() {
        io = new TVDBManager(TV.ENV.getTVDB());
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
        
        private final Socket client;
        
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
                    if(br != null) {
                        br.close();
                    }
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
                    List<String> cmdList = CommandUtil.dequoteArgsToList(command);
                    for(String source : TV.ENV.getArguments().getSourceFolders()) {
                        cmdList.add("--source");
                        cmdList.add(source);
                    }
                    String[] cmdArray = CommandUtil.buildJavaCommandString(cmdList);
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
                    out.println(f == null ? "" : f.getName());
                }
                if(command.equals("list_shows")) {
                    out.println(io.getCSVShows());
                }
                if(command.equals("list_stored_eps")) {
                    out.println(io.getCSVEpisodes(episodeList));
                }
                if(command.equals("list_extra_files")) {
                    List<File> files = new ArrayList<File>();
                    for(String dir : TV.ENV.getArguments().getExtraFolders()) {
                        files.addAll(Arrays.asList(new File(dir).listFiles(new ExtensionFilter())));
                    }
                    for(File file : files) {
                        out.println(file.getAbsolutePath());
                    }
                }
                r.close();
                out.close();
                client.close();
            } catch (IOException ex) {

            }
        }
        
    }
    
}
