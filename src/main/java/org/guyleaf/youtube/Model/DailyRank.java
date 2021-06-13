package org.guyleaf.youtube.Model;

import java.io.Serializable;
import java.util.Date;

public class DailyRank extends Rank implements Serializable {
    private final Date collectedDate;

    public DailyRank(int categoryId, Date date) {
        super(categoryId);
        this.collectedDate = date;
    }

    public Date collectedDate() {
        return this.collectedDate;
    }
}
