package org.guyleaf.youtube.Model;

import java.io.Serializable;
import java.util.Date;

public class HourlyRank extends Rank implements Serializable {
    private final int collectedHour;
    private final String collectedDate;

    public HourlyRank(int categoryId, int hour, String date) {
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
}
