package org.guyleaf.youtube.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Rank implements Serializable {
    private final Date collectedDate;
    private final List<String> videoRanks;

    public Rank(Date date) {
        this.collectedDate = date;
        this.videoRanks = new ArrayList<>();
    }

    public Date collectedDate() {
        return this.collectedDate;
    }

    public List<String> videoRanks() {
        return this.videoRanks;
    }

    public void add(String id) {
        this.videoRanks.add(id);
    }
}
