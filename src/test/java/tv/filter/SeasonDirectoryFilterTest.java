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

package tv.filter;

import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Sam Malone
 */
public class SeasonDirectoryFilterTest {

    /**
     * Test of accept method, of class SeasonDirectoryFilter.
     */
    @Test
    public void testAccept() {
        String[] seasonDirs = new String[] {
            "Season 0",
            "Season 1",
            "Season 01",
            "Series 1",
            "Series 01",
        };
        SeasonDirectoryFilter instance = new SeasonDirectoryFilter();
        for(String seasonDir : seasonDirs) {
            assertTrue(instance.accept(null, seasonDir));
        }
    }

    /**
     * Test of accept method, of class SeasonDirectoryFilter.Max.
     */
    @Test
    public void testAcceptMax() {
        String[] seasonDirs = new String[] {
            "Season 1",
            "Season 2",
            "Season 3",
        };
        SeasonDirectoryFilter.Max instance = new SeasonDirectoryFilter.Max();
        for(String seasonDir : seasonDirs) {
            assertTrue(instance.accept(null, seasonDir));
        }
        Assert.assertEquals(3, instance.getMax());
    }
    
}
