package org.guyleaf.dailyRankSparkCollector;

import java.util.List;

public abstract class Rank {
    private String categoryId;
    private List<String> videoRanks;

    public String getCategoryId() {
        return this.categoryId;
    }

    public List<String> getVideoRanks() {
        return this.videoRanks;
    }

    public void setVideoRanks(List<String> videoRanks) {
        this.videoRanks = videoRanks;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
