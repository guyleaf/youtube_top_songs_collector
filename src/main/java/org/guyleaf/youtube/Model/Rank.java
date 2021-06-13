package org.guyleaf.youtube.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Rank {
    private final int categoryId;
    private final List<String> videoRanks;

    public Rank(int categoryId) {
        this.categoryId = categoryId;
        this.videoRanks = new ArrayList<>();
    }

    public int categoryId() {
        return this.categoryId;
    }

    public List<String> videoRanks() {
        return this.videoRanks;
    }

    public void add(String id) {
        this.videoRanks.add(id);
    }
}
