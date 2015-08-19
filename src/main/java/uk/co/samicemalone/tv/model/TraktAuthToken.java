/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.samicemalone.tv.model;

import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;

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

    public TraktAuthToken(OAuthAccessTokenResponse r) {
        accessToken = r.getAccessToken();
        refreshToken = r.getRefreshToken();
        expiresIn = r.getExpiresIn();
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
    
    public boolean isValid(long tokenValidity) {
        return createdAt + tokenValidity > System.currentTimeMillis();

    }
    
    
}
