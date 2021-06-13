package org.guyleaf.youtube.Model;

public class Thumbnail {
    private String url;
    private int width;
    private int height;

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
}
