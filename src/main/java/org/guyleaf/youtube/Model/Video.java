/*
 * Author: guyleaf
 * Date: 2021-06-30 04:40 PM GMT+8
 * Url: https://github.com/guyleaf
 */

package org.guyleaf.youtube.Model;

import java.io.Serializable;

public class Video implements Serializable {
    private final String videoId;
    private final String title;
    private final String description;
    private final VideoSnippet videoSnippet;

    public Video(String videoId, String title, String description, VideoSnippet videoSnippet) {
        this.videoId = videoId;
        this.title = title;
        this.description = description;
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

    public VideoSnippet snippet() {
        return this.videoSnippet;
    }
}
