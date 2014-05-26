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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Sam Malone
 */
public abstract class CSV_IO {
    
    protected File CSV_FILE;
    
    public CSV_IO(File csv) {
        CSV_FILE = csv;
    }
   
    /**
     * Reads the CSV file line by line and executes the handleLine method per line. 
     * @param CSVFieldCount Total Fields in CSV file
     * @throws FileNotFoundException if CSV file not found
     */
    public final void readFile(int CSVFieldCount) throws FileNotFoundException {
        if(!CSV_FILE.exists()) {
            throw new FileNotFoundException();
        }
        try {
            BufferedReader r = new BufferedReader(new FileReader(CSV_FILE));
            try {
                String line;
                Pattern p = getCSVPattern(CSVFieldCount);
                while((line = r.readLine()) != null) {
                    handleLine(p.matcher(line));
                }
            } finally {
                r.close();
            }
        } catch(IOException ex) {
            
        }
    }
    
    /**
     * This method is called when a line has been read
     * @param m Matcher for the CSV fields
     */
    abstract protected void handleLine(Matcher m);
    
    /**
     * Gets the Pattern for matching against the CSV line
     * @param fields Number of CSV fields
     * @return Pattern
     */
    protected final Pattern getCSVPattern(int fields) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < fields - 1; i++) {
            sb.append("\"(.*?)\",");
        }
        sb.append("\"(.*?)\"");
        return Pattern.compile(sb.toString());
    }    
    
    /**
     * Writes the given string to CSV_FILE
     * @param toWrite String to write to CSV_FILE
     */
    protected final void writeFile(String toWrite) {
        BufferedWriter w;
        try {
            w = new BufferedWriter(new FileWriter(CSV_FILE));
            w.write(toWrite);
            try {
                w.flush();
                w.close();
            } catch(IOException e) {
                System.err.println(e);
            }
        } catch(IOException ex) {
            System.err.println(ex);
        }
    }
    
    /**
     * Appends double quotes round the given string toWrap
     * @param sb StringBuilder to append to
     * @param toWrap String to be wrapped in quotes
     */
    protected static void wrapQuotes(StringBuilder sb, String toWrap) {
        sb.append('\"');
        sb.append(toWrap);
        sb.append('\"');
    }
    
    /**
     * Appends a CSV formatted line with the given CSV fields
     * @param sb
     * @param fields 
     */
    protected static void appendCSVLine(StringBuilder sb, String... fields) {
        for(int i = 0; i < fields.length - 1; i++) {
            wrapQuotes(sb, fields[i]);
            sb.append(',');
        }
        wrapQuotes(sb, fields[fields.length-1]);
        sb.append('\n');
    }
        
}
