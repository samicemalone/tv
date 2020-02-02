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

import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.tv.ExitCode;
import uk.co.samicemalone.tv.MediaInfo;
import uk.co.samicemalone.tv.TV;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.exception.FileNotFoundException;
import uk.co.samicemalone.tv.tvdb.model.Show;
import uk.co.samicemalone.tv.util.MediaUtil;

import java.io.File;
import java.util.List;

/**
 *
 * @author Sam Malone
 */
public class LengthAction implements Action, FileAction {

    @Override
    public boolean isAction(int action) {
        return action == Action.LENGTH;
    }

    private MediaInfo getMediaInfo() throws FileNotFoundException {
        if(!TV.ENV.getMediaInfoBinary().exists()) {
            throw new FileNotFoundException("The MediaInfo binary could not be found", ExitCode.FILE_NOT_FOUND);
        }
        return new MediaInfo(TV.ENV.getMediaInfoBinary());
    }

    @Override
    public void execute(Show show, List<EpisodeMatch> list) throws ExitException {
        MediaInfo mediaInfo = getMediaInfo();
        list.forEach(m -> mediaInfo.addFile(m.getEpisodeFile().getAbsolutePath()));
        System.out.println(MediaUtil.readableLength(mediaInfo.getLength()));
    }

    @Override
    public void execute(File file) throws ExitException {
        MediaInfo mediaInfo = getMediaInfo();
        mediaInfo.addFile(file.getAbsolutePath());
        System.out.println(MediaUtil.readableLength(mediaInfo.getLength()));
    }
}
