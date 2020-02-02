/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.samicemalone.tv.model;

import com.uwetrottmann.trakt5.entities.AccessToken;

/**
 *
 * @author Sam Malone
 */
public class TraktAuthToken {

    /**
     * Length the token is valid for in milliseconds (3 months / 90 days)
     */
    private static final long DEFAULT_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 90;
    
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
        return createdAt + DEFAULT_TOKEN_VALIDITY < System.currentTimeMillis();
    }

    public boolean isRefreshRequired(int thresholdPercent) {
        long elapsed = System.currentTimeMillis() - createdAt;
        long progress = elapsed / DEFAULT_TOKEN_VALIDITY;
        long threshold = DEFAULT_TOKEN_VALIDITY * (thresholdPercent / 100);
        return !hasExpired() && progress > threshold;
    }
    
}
