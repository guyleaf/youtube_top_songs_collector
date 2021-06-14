package org.guyleaf.youtube.Model;

import org.bson.Document;

public class HourlyRank extends Rank {
    private final int collectedHour;
    private final String collectedDate;

    public HourlyRank(String categoryId, int hour, String date) {
        super(categoryId);
        this.collectedHour = hour;
        this.collectedDate = date;
    }

    public int collectedHour() {
        return this.collectedHour;
    }

    public String collectedDate() {
        return this.collectedDate;
    }

    @Override
    public Document toDocument() {
        return super.toDocument().append("collectedDate", this.collectedDate).append("collectedHour", this.collectedHour);
    }
}
