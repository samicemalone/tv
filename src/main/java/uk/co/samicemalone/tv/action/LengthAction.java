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
import uk.co.samicemalone.tv.ExitCode;
import uk.co.samicemalone.tv.MediaInfo;
import uk.co.samicemalone.tv.TV;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.exception.FileNotFoundException;
import uk.co.samicemalone.tv.model.Episode;
import uk.co.samicemalone.tv.util.MediaUtil;

/**
 *
 * @author Sam Malone
 */
public class LengthAction implements Action {

    @Override
    public void execute(File[] list) throws ExitException {
        if(!TV.ENV.getMediaInfoBinary().exists()) {
            throw new FileNotFoundException("The MediaInfo binary could not be found", ExitCode.FILE_NOT_FOUND);
        }
        MediaInfo mediaInfo = new MediaInfo(TV.ENV.getMediaInfoBinary());
        length(mediaInfo, list);
        System.out.println(MediaUtil.readableLength(mediaInfo.getLength()));
    }

    @Override
    public void execute(File list, Episode pointerEpisode) throws ExitException {
        execute(new File[] { list });
    }
    
    /**
     * Get the total length of the media in the files/directories given in list.
     * @param mediaInfo MediaInfo instance
     * @param list List of files/directories
     * @return Length in seconds. 0 if list is empty.
     */
    private void length(MediaInfo mediaInfo, File[] list) {
        for(File item : list) {
            if(item.isDirectory()) {
                length(mediaInfo, item.listFiles(FILTER));
            } else {
                mediaInfo.addFile(item.getAbsolutePath());
            }
        }
    }
    
}
