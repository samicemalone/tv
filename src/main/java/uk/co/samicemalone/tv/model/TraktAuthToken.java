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

package uk.co.samicemalone.tv.model;

import com.uwetrottmann.trakt5.entities.AccessToken;

/**
 *
 * @author Sam Malone
 */
public class TraktAuthToken {

    private String accessToken;
    private String refreshToken;
    private long createdAt;
    private long expiresIn;

    public TraktAuthToken(String accessToken, String refreshToken, long createdAt, long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.createdAt = createdAt;
        this.expiresIn = expiresIn;
    }

    public TraktAuthToken(AccessToken t) {
        accessToken = t.access_token;
        refreshToken = t.refresh_token;
        expiresIn = t.expires_in;
        createdAt = System.currentTimeMillis();
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String rerfreshToken) {
        this.refreshToken = rerfreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public boolean hasExpired() {
        return createdAt + (1000L * expiresIn) < System.currentTimeMillis();
    }

    public boolean isRefreshRequired(int thresholdPercent) {
        if (hasExpired()) {
            return true;
        }

        long totalDurationMs = expiresIn * 1000L;
        // Calculate the point in time (ms) when we should trigger a refresh
        long refreshThresholdMs = (totalDurationMs * thresholdPercent) / 100;
        long refreshAtTimestamp = createdAt + refreshThresholdMs;

        return System.currentTimeMillis() > refreshAtTimestamp;
    }
    
}
