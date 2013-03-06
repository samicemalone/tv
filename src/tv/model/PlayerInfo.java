/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.model;

/**
 *
 * @author Ice
 */
public class PlayerInfo {
    
    private String player;
    private String playerExecutable;
    private String[] playerArguments;

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getPlayerExecutable() {
        return playerExecutable;
    }

    public void setPlayerExecutable(String playerExecutable) {
        this.playerExecutable = playerExecutable;
    }

    public String[] getPlayerArguments() {
        return playerArguments;
    }

    public void setPlayerArguments(String[] playerArguments) {
        this.playerArguments = playerArguments;
    }
    
}
