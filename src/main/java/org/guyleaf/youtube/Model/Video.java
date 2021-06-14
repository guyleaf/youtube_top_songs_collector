/*
 * Author: guyleaf
 * Date: 2021-06-30 04:40 PM GMT+8
 * Url: https://github.com/guyleaf
 */

package org.guyleaf.youtube.Model;

import org.bson.Document;

public class Video {
    private final String id;
    private final String title;
    private final String description;
    private final VideoSnippet videoSnippet;

    public Video(String id, String title, String description, VideoSnippet videoSnippet) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.videoSnippet = videoSnippet;
    }

    public String id() {
        return this.id;
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

    public Document toDocument() {
        return new Document("id", this.id)
                .append("title", this.title)
                .append("description", this.description)
                .append("snippet", this.videoSnippet.toDocument());
    }
}
