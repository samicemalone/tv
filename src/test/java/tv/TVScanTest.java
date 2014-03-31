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

package tv;

import java.io.File;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Sam Malone
 */
public class TVScanTest extends FileSystemEnvironment {
    
    private TVScan tvScanner;
    
    @Before
    public void setUp() {
        tvScanner = new TVScan(MockFileSystem.getSourceFolders());
    }
    
    @After
    public void tearDown() {
        tvScanner = null;
    }

    /**
     * Test of asInt method, of class TVScan.
     */
    @Test
    public void testGetSeasonNo() {
        String ep = "s02e01";
        int expResult = 2;
        int result = TVScan.getSeasonNo(ep);
        assertEquals(expResult, result);
    }

    /**
     * Test of getEpisodeNo method, of class TVScan.
     */
    @Test
    public void testGetEpisodeNo() {
        String ep = "s02e01";
        String expResult = "01";
        String result = TVScan.getEpisodeNo(ep);
        assertEquals(expResult, result);
    }

    /**
     * Test of getDirectory method, of class TVScan.
     */
    @Test
    public void testGetSeasonDirectory() {
        String show = "Scrubs";
        int season = 3;
        File expResult = MockFileSystem.getSeasonDir(show, season);
        File result = tvScanner.getSeasonDirectory(show, season);
        assertEquals(expResult, result);
    }

    /**
     * Test of showExists method, of class TVScan.
     */
    @Test
    public void testShowExists() {
        boolean expResult = true;
        boolean result = tvScanner.showExists("Scrubs");
        assertEquals(expResult, result);
    }

    /**
     * Test of showExists method, of class TVScan.
     */
    @Test
    public void testShowNotExists() {
        boolean expResult = false;
        boolean result = tvScanner.showExists("DoesntExist");
        assertEquals(expResult, result);
    }
    
}
