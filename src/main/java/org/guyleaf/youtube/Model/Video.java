/*
 * Author: guyleaf
 * Date: 2021-06-30 04:40 PM GMT+8
 * Url: https://github.com/guyleaf
 */

package org.guyleaf.youtube.Model;

import java.io.Serializable;
import java.util.Date;

public class Video implements Serializable {
    private final String videoId;
    private final String title;
    private final String description;
    private final Integer scrapedHour;
    private final Date scrapedDate;
    private final VideoSnippet videoSnippet;

    public Video(String videoId, String title, String description, Integer scrapedHour, Date scrapedDate, VideoSnippet videoSnippet) {
        this.videoId = videoId;
        this.title = title;
        this.description = description;
        this.scrapedHour = scrapedHour;
        this.scrapedDate = scrapedDate;
        this.videoSnippet = videoSnippet;
    }

    public String videoId() {
        return this.videoId;
    }

    public String title() {
        return this.title;
    }

    public String description() {
        return this.description;
    }

    public Integer scrapedHour() {
        return this.scrapedHour;
    }

    public Date scrapedDate() {
        return this.scrapedDate;
    }

    public VideoSnippet snippet() {
        return this.videoSnippet;
    }
}
