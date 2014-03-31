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

package tv.mode;

import tv.ExitCode;
import tv.TVScan;
import tv.exception.ExitException;

/**
 *
 * @author Sam Malone
 */
public class EpisodeModeFactory {
    
    public static EpisodeMode getEpisodeMode(int mode, TVScan scanner) throws ExitException {
        switch(mode) {
            case EpisodeModes.POINTER:
            case EpisodeModes.SEASONFROMPOINTER:
                return new PointerMode(mode, scanner).readCurrentPointer();
            case EpisodeModes.SEASON:
            case EpisodeModes.SEASONFROMEP:
            case EpisodeModes.EPRANGE:
            case EpisodeModes.ALL:
            case EpisodeModes.SEASONRANGE:
            case EpisodeModes.ALLFROMSEASON:
            case EpisodeModes.LATEST_SEASON:
                return new EpisodeRangeMode(mode, scanner);
            case EpisodeModes.EPSINGLE:
            case EpisodeModes.PILOT:
            case EpisodeModes.LATEST:
                return new WriteOnlyPointerMode(mode, scanner);
        }
        throw new ExitException("The episode mode given is not valid", ExitCode.EPISODES_NOT_FOUND);
    } 
    
}
