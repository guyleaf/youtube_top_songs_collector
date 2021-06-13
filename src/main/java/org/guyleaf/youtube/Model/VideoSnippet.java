package org.guyleaf.youtube.Model;

import java.io.Serializable;

public class VideoSnippet implements Serializable {
    private final Thumbnail thumbnail;
    private final String channelId;
    private final String channelTitle;
    private final String categoryId;
    private final int viewCount;
    private final String duration;

    public VideoSnippet(Thumbnail thumbnail, String channelId, String channelTitle, String categoryId, int viewCount, String duration) {
        this.thumbnail = thumbnail;
        this.channelId = channelId;
        this.channelTitle = channelTitle;
        this.categoryId = categoryId;
        this.viewCount = viewCount;
        this.duration = duration;
    }

    public Thumbnail thumbnail() {
        return this.thumbnail;
    }

    public String channelId() {
        return this.channelId;
    }

    public String channelTitle() {
        return this.channelTitle;
    }

    public String categoryId() {
        return this.categoryId;
    }

    public int viewCount() {
        return this.viewCount;
    }

    public String duration() {
        return this.duration;
    }
}
