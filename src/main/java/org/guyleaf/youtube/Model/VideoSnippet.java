package org.guyleaf.youtube.Model;

import org.bson.Document;

public class VideoSnippet {
    private final Thumbnail thumbnail;
    private final String channelId;
    private final String channelTitle;
    private final String categoryId;
    private final String viewCount;
    private final String duration;

    public VideoSnippet(Thumbnail thumbnail, String channelId, String channelTitle, String categoryId, String viewCount, String duration) {
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

    public String viewCount() {
        return this.viewCount;
    }

    public String duration() {
        return this.duration;
    }

    public Document toDocument() {
        return new Document("channelId", this.channelId)
                .append("channelTitle", this.channelTitle)
                .append("categoryId", this.categoryId)
                .append("thumbnail", this.thumbnail.toDocument())
                .append("viewCount", this.viewCount)
                .append("duration", this.duration);
    }
}
