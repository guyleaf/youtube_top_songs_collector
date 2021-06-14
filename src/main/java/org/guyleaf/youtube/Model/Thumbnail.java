package org.guyleaf.youtube.Model;

import org.bson.Document;

public class Thumbnail {
    private final String url;
    private final int width;
    private final int height;

    public Thumbnail(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }

    public String url() {
        return this.url;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public Document toDocument() {
        return new Document("url", this.url).append("width", this.width).append("height", this.height);
    }
}
