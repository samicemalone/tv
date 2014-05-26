/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.samicemalone.tv.model;

/**
 *
 * @author Sam Malone
 */
public class TraktCredentials {
    
    private String username;
    private String passwordSha1;
    private String apiKey;

    public TraktCredentials(String username, String passwordSha1, String apiKey) {
        this.username = username;
        this.passwordSha1 = passwordSha1;
        this.apiKey = apiKey;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String traktApiKey) {
        apiKey = traktApiKey;
    }

    public String getPasswordSha1() {
        return passwordSha1;
    }

    public void setPasswordSha1(String traktPasswordSha1) {
        passwordSha1 = traktPasswordSha1;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String traktUsername) {
        username = traktUsername;
    }
    
}
