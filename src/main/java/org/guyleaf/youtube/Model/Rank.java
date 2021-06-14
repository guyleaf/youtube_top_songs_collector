package org.guyleaf.youtube.Model;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public abstract class Rank {
    private final String categoryId;
    private final List<String> videoRanks;

    public Rank(String categoryId) {
        this.categoryId = categoryId;
        this.videoRanks = new ArrayList<>();
    }

    public String categoryId() {
        return this.categoryId;
    }

    public List<String> videoRanks() {
        return this.videoRanks;
    }

    public void add(String id) {
        this.videoRanks.add(id);
    }

    public Document toDocument() {
        return new Document("categoryId", this.categoryId).append("videoRanks", this.videoRanks);
    }
}
