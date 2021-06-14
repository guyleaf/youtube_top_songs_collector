package org.guyleaf.youtube.Model;

import org.bson.Document;

public final class Category {
    private String id;
    private String title;

    public Category() { }

    public Category(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String id() {
        return this.id;
    }

    public String title() {
        return this.title;
    }

    public Document toDocument() {
        return new Document("id", this.id).append("title", this.title);
    }
}
