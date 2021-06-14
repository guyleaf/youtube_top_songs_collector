package org.guyleaf.dailyRankSparkCollector;

import java.io.Serializable;

public final class HourlyRank extends Rank implements Serializable {
    private int collectedHour;
    private String collectedDate;

    public int getCollectedHour() {
        return this.collectedHour;
    }

    public String getCollectedDate() {
        return this.collectedDate;
    }

    public void setCollectedHour(int collectedHour) {
        this.collectedHour = collectedHour;
    }

    public void setCollectedDate(String collectedDate) {
        this.collectedDate = collectedDate;
    }
}
