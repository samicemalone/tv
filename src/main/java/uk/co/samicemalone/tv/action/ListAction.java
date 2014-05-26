/*
 * Copyright (c) 2013, Sam Malone
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package uk.co.samicemalone.tv.action;

import java.io.File;
import uk.co.samicemalone.tv.model.Episode;
import uk.co.samicemalone.tv.util.CommandUtil;

/**
 *
 * @author Sam Malone
 */
public class ListAction implements Action {
    
    private boolean listPath;

    /**
     * Creates a new instance of ListAction
     * @param listPath if true, the full file paths will be listed otherwise only
     * file names will be listed
     */
    public ListAction(boolean listPath) {
        this.listPath = listPath;
    }
    
    /**
     * Creates a new instance of ListAction with the default action to show file
     * names
     */
    public ListAction() {
        this(false);
    }

    @Override
    public void execute(File[] list) {
        list(list);
    }

    @Override
    public void execute(File list, Episode pointerEpisode) {
        execute(new File[] { list });
    }
    
    /**
     * Prints the list of media files from the given list of files/directories
     * to stdout.
     * @param list List of files/directories
     */
    private void list(File[] list) {
        for(File file : list) {
            if(file.isDirectory()) {
                list(file.listFiles(FILTER));
            } else {
                System.out.println(listPath ? CommandUtil.getCanonicalPath(file) : file.getName());
            }
        }
    }

    @Override
    public int hashCode() {
        return 79 * 5 + (this.listPath ? 1 : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ListAction other = (ListAction) obj;
        return this.listPath == other.listPath;
    }
    
}
