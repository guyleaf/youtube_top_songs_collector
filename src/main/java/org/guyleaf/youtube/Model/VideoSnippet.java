package org.guyleaf.youtube.Model;

import java.io.Serializable;

public class VideoSnippet implements Serializable {
    private final String thumbnail;
    private final String channelId;
    private final String channelTitle;
    private final String categoryId;
    private final Integer viewCount;
    private final Integer rank;
    private final String duration;

    public VideoSnippet(String thumbnail, String channelId, String channelTitle, String categoryId, Integer viewCount, Integer rank, String duration) {
        this.thumbnail = thumbnail;
        this.channelId = channelId;
        this.channelTitle = channelTitle;
        this.categoryId = categoryId;
        this.viewCount = viewCount;
        this.rank = rank;
        this.duration = duration;
    }

    public String thumbnail() {
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

    public Integer viewCount() {
        return this.viewCount;
    }

    public Integer rank() {
        return this.rank;
    }

    public String duration() {
        return this.duration;
    }
}
