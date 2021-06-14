package org.guyleaf.youtube.Task;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;

public class CollectingTask implements Runnable {
    private ScheduledExecutorService scheduler;
    private String categoryId;
    private Date currentDate;

    public CollectingTask(ScheduledExecutorService scheduler, String categoryId, Date currentDate) {
        this.scheduler = scheduler;
        this.categoryId = categoryId;
        this.currentDate = currentDate;
    }

    @Override
    public void run() {

    }
}
