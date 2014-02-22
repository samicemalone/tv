/*
 * Copyright (c) 2014, Sam Malone. All rights reserved.
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

package tv.io;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import tv.TV;
import tv.model.Episode;

/**
 *
 * @author Sam Malone
 */
public class TraktDBManager {

    private static final int TRAKT_DB = 0;
    private static final int TRAKT_JOURNAL = 1;
    
    private final File traktDB;
    private final File traktDBJournal;
    
    private List<Episode> journalEpisodes;
    private String showName;
    private int showId = -1;
    
    public TraktDBManager() {
        traktDB = TV.ENV.getDefaultTraktDB();
        traktDBJournal = TV.ENV.getDefaultTraktDBJournal();
        journalEpisodes = new ArrayList<Episode>();
    }
    
    /**
     * Find the trakt tvdb id for the given show name.
     * @param showName Show name
     * @return trakt tvdb id or -1 if not found or the tvdb file doesn't exist
     */
    public int findShowId(String showName) {
        this.showName = showName;
        showId = -1;
        if(showName != null) {
            try {
                readFile(TRAKT_DB, traktDB);
            } catch (FileNotFoundException ex) {
                
            }
        }
        return showId;
    }

    /**
     * Called when a file is being read.
     * @param id id to uniquely identify the file being read
     * @param line csv fields
     * @return true if no more input needs to be read. false to continue
     * reading
     */
    private boolean onReadLine(int id, String[] line) {
        switch(id) {
            case TRAKT_DB:
                if(showName.equals(line[0])) {
                    try {
                        showId = Integer.parseInt(line[1]);
                        return true;
                    } catch (NumberFormatException ex) {}
                }
                break;
            case TRAKT_JOURNAL:
                try {
                    Episode ep = new Episode(line[0], "", line[1], line[2]);
                    ep.setPlayedDate(Integer.parseInt(line[3]));
                    journalEpisodes.add(ep);
                } catch (NumberFormatException ex) {}
                break;
        }
        return false;
    }
    
    /**
     * Read the episodes from the journal that have previously had errors when 
     * marking as seen.
     * @return List of episodes in the journal, or empty list if the file
     * doesn't exist
     */
    public List<Episode> readJournal() {
        journalEpisodes = new ArrayList<Episode>();
        try {
            readFile(TRAKT_JOURNAL, traktDBJournal);
        } catch (FileNotFoundException ex) {
            
        }
        return journalEpisodes;
    }

    /**
     * Append the show information to the trakt tvdb
     * @param showId trakt tvdb show id
     * @param show show name
     * @throws IOException if unable to write to tvdb
     */
    public void appendTVDB(int showId, String show) throws IOException {
        traktDB.createNewFile();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(traktDB, true), "UTF-8"));
        try {
            writer.writeNext(new String[] { show, String.valueOf(showId) });
        } finally {
            writer.close();
        }
    }
    
    /**
     * Read a csv file. See {@link #onReadLine(int, java.lang.String[]) to
     * handle each line of input.
     * @param id id to uniquely identify the read line action
     * @param file file to read
     * @throws FileNotFoundException if the given file doesn't exist
     */
    private void readFile(int id, File file) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(fis, "UTF-8"));
            String[] nextLine;
            boolean isStopReading = false;
            try {
                while ((nextLine = reader.readNext()) != null && !isStopReading) {
                    isStopReading = onReadLine(id, nextLine);
                }
            } finally {
                reader.close();
            }
        } catch(IOException ex) {}
    }
    
    /**
     * Remove the journal file
     */
    public void removeJournal() {
        traktDBJournal.delete();
    }
    
    /**
     * Append a list of episodes to the journal
     * @param eps list of episodes
     * @throws IOException if an error occurs whilst appending
     */
    public void appendJournal(List<Episode> eps) throws IOException {
        writeJournal(eps, true);
    }
    
    /**
     * Write a list of episodes to the journal
     * @param eps list of episodes
     * @param append whether to append to the end of the file or not
     * @throws IOException if an error occurs whilst writing
     */
    public void writeJournal(List<Episode> eps, boolean append) throws IOException {
        traktDBJournal.createNewFile();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(traktDBJournal, append), "UTF-8"));
        try {
            for(Episode ep : eps) {
                writer.writeNext(new String[] {
                    ep.getShow(),
                    ep.getSeasonNo(),
                    ep.getEpisodeNo(),
                    String.valueOf(ep.getPlayedDate())
                });
            }
        } finally {
            writer.close();
        }
    }    
    
}
