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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import tv.exception.ParseException;
import tv.model.Config;

/**
 *
 * @author Sam Malone
 */
public class ConfigParserTest {
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    private File configFile;
    
    @Before
    public void setUp() throws IOException {
        configFile = folder.newFile("tv.conf");
    }
    
    public static String[] arg(String... args) {
        return args;
    }
    
    private void writeConfig(String config) throws IOException {
        FileWriter w = new FileWriter(configFile);
        w.write(config);
        w.flush();
    }

    /**
     * Test of parse method, of class ConfigParser.
     * @throws java.lang.Exception
     */
    @Test
    public void testParseValid() throws Exception {
        writeConfig(getTestAllConfigString());
        Config result = ConfigParser.parse(configFile);
        assertEquals(result.getLibraryName(), LibraryManager.isWindows7() ? "TV" : null);
        assertEquals(result.getMediainfoBinary(), "/path/to/mediainfo");
        assertEquals(result.getPlayer(), "stdout");
        assertEquals(result.getPlayerExecutable(), "/path/to/executable");
        assertEquals(result.getTVDBFile(), "/path/to/tvdb.csv");
        assertEquals(result.getTraktApiKey(), "ab12");
        assertEquals(result.isTraktEnabled(), true);
        assertEquals(result.getTraktPasswordSha1(), "pass");
        assertEquals(result.isTraktUseCheckins(), true);
        assertEquals(result.getTraktUsername(), "user");
        assertArrayEquals(result.getPlayerArguments(), arg("--arg", "val"));
        assertEquals(result.getExtraFolders(), Arrays.asList(arg("/path/to/extra")));
        assertEquals(result.getSourceFolders(), Arrays.asList(arg("/path/to/source1", "/path/to/source2")));
    }
    
    /**
     * Test of parse method, of class ConfigParser.
     * @throws java.lang.Exception
     */
    @Test
    public void testParseInvalid() throws Exception {
        String[] configs = new String[] {
            "noequals",
        };
        for(String config : configs) {
            try {
                writeConfig(config);
                ConfigParser.parse(configFile);
                fail();
            } catch (ParseException ex) {}
        }
    }    
    
    private static String getTestAllConfigString() {
        StringBuilder sb = new StringBuilder();
        sb.append("# comment line\n");
        sb.append("  # indented comment line\n");
        sb.append('\n');
        sb.append("TVDB_FILE=/path/to/tvdb.csv\n");
        sb.append("SOURCE=/path/to/source1\n");
        sb.append("SOURCE=/path/to/source2\n");
        sb.append("\tMEDIAINFO_BINARY=/path/to/mediainfo\n");
        sb.append(" LIBRARY_NAME=TV\n");
        sb.append("PLAYER=stdout\n");
        sb.append("PLAYER_EXECUTABLE=/path/to/executable\n");
        sb.append("PLAYER_ARGUMENTS=--arg\n");
        sb.append("PLAYER_ARGUMENTS=val\n");
        sb.append("FILES_FROM=/path/to/extra\n");
        sb.append("ENABLE_TRAKT=true\n");
        sb.append("TRAKT_USERNAME=user\n");
        sb.append("TRAKT_PASSWORD_SHA1=pass\n");
        sb.append("TRAKT_API_KEY=ab12\n");
        sb.append("TRAKT_USE_CHECKINS=true\n");
        return sb.toString();
    }
    
}
