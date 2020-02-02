/*
 * Copyright (c) 2015, Ice. All rights reserved.
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
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Properties;
import uk.co.samicemalone.tv.model.TraktAuthToken;

/**
 *
 * @author Ice
 */
public class TraktAuthTokenReader {
    
    /**
     * Read the Trakt auth token
     * @param authTokenFile Auth token File
     * @return 
     */
    public static TraktAuthToken read(File authTokenFile) {
        Properties p = new Properties();
        try (BufferedReader br = Files.newBufferedReader(authTokenFile.toPath(), Charset.defaultCharset())) {
            p.load(br);
            String[] keys = new String[] {"access_token", "refresh_token", "expires_in", "created_at"};
            for (String key : keys) {
                if(!p.containsKey(key) || p.getProperty(key) == null || p.getProperty(key).isEmpty()) {
                    return null;
                }
            }
            return new TraktAuthToken(
                p.getProperty("access_token"),
                p.getProperty("refresh_token"),
                Long.parseLong(p.getProperty("created_at")),
                Long.parseLong(p.getProperty("expires_in"))
            );
        } catch (IOException | NumberFormatException ex) {

        }
        return null;
    }
    
    public static void write(TraktAuthToken token, File authTokenFile) throws IOException {
        Properties p = new Properties();
        p.setProperty("access_token", token.getAccessToken());
        p.setProperty("refresh_token", token.getRefreshToken());
        p.setProperty("created_at", String.valueOf(token.getCreatedAt()));
        p.setProperty("expires_in", String.valueOf(token.getExpiresIn()));
        try (BufferedWriter bw = Files.newBufferedWriter(authTokenFile.toPath(), Charset.defaultCharset())) {
            p.store(bw, null);
        }
    }
    
    
}
