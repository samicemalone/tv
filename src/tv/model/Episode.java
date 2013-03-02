/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.model;

/**
 *
 * @author Ice
 */
public class Episode {

    private String show;
    private String seasonNo;
    private String episodeNo;
    private String user;

    /**
     * Creates an instance of Episode
     * @param show TV Show
     * @param seasonNo Zero padded season number
     * @param episodeNo Zero padded episode number
     */
    public Episode(String show, String seasonNo, String episodeNo) {
        this.show = show;
        this.seasonNo = seasonNo;
        this.episodeNo = episodeNo;
    }
    
    /**
     * Gets the user to which this Episode refers
     * @return User if exists, empty string otherwise
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the user to which this Episode refers
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Gets the episode number
     * @return Zero padded episode number
     */
    public String getEpisodeNo() {
        return episodeNo;
    }

    /**
     * Sets the episode number
     * @param episodeNo Zero padded episode number
     */
    public void setEpisodeNo(String episodeNo) {
        this.episodeNo = episodeNo;
    }

    /**
     * Gets the season number
     * @return Zero padded season number
     */
    public String getSeasonNo() {
        return seasonNo;
    }

    /**
     * Sets the season number
     * @param seasonNo Zero padded season number
     */
    public void setSeasonNo(String seasonNo) {
        this.seasonNo = seasonNo;
    }

    /**
     * Sets the TV Show
     * @return 
     */
    public String getShow() {
        return show;
    }

    /**
     * Gets the TV Show
     * @param show 
     */
    public void setShow(String show) {
        this.show = show;
    }
}
