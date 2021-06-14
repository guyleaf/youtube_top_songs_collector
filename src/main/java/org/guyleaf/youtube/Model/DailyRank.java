package org.guyleaf.youtube.Model;

import org.bson.Document;

public class DailyRank extends Rank {
    private final String collectedDate;

    public DailyRank(String categoryId, String date) {
        super(categoryId);
        this.collectedDate = date;
    }

    public String collectedDate() {
        return this.collectedDate;
    }

    @Override
    public Document toDocument() {
        return super.toDocument().append("collectedDate", this.collectedDate);
    }
}
