package org.guyleaf.youtube.Task;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CollectingTask implements Runnable {
    private final ScheduledExecutorService scheduler;
    private final String categoryId;
    private final String currentDate;
    private final Dotenv file;
    private final AtomicInteger counter;

    public CollectingTask(ScheduledExecutorService scheduler, String categoryId, String currentDate) {
        this.scheduler = scheduler;
        this.categoryId = categoryId;
        this.currentDate = currentDate;
        this.file = Dotenv.load();
        this.counter = new AtomicInteger(0);
    }

    @Override
    public void run() {
        Map<String, String> env = new HashMap<>();
        env.put("SPARK_URL", file.get("SPARK_URL"));
        env.put("DB_INPUT_URI", String.format("mongodb://%s:%s@%s:%s/%s.hourlyRank?authSource=%s", file.get("USERNAME"), file.get("PASSWORD"), file.get("HOST"), file.get("PORT"), file.get("DATABASE"), file.get("DATABASE")));
        env.put("DB_OUTPUT_URI", String.format("mongodb://%s:%s@%s:%s/%s.dailyRank?authSource=%s", file.get("USERNAME"), file.get("PASSWORD"), file.get("HOST"), file.get("PORT"), file.get("DATABASE"), file.get("DATABASE")));
        env.put("JAVA_HOME", file.get("JAVA_HOME"));

        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            SparkAppHandle handle = new SparkLauncher(env)
                    .setSparkHome(file.get("SPARK_HOME"))
                    .setAppResource(file.get("POST_PROCESS_FILE"))
                    .setMainClass(file.get("POST_PROCESS_MAIN_CLASS"))
                    .setMaster(file.get("SPARK_URL"))
                    .setConf(SparkLauncher.DRIVER_MEMORY, "2g")
                    .setConf("spark.scheduler.mode", "FAIR")
                    .setConf("spark.scheduler.allocation.file", file.get("SCHEDULER_FILE"))
                    .addAppArgs(this.categoryId, this.currentDate)
                    .startApplication(new SparkAppHandle.Listener() {
                        //這裡監聽任務狀態，當任務結束時（不管是什麼原因結束）,isFinal（）方法會返回true,否則返回false
                        @Override
                        public void stateChanged(SparkAppHandle sparkAppHandle) {
                            if (sparkAppHandle.getState().isFinal()) {
                                countDownLatch.countDown();
                            }
                            System.out.println("state:" + sparkAppHandle.getState().toString());
                        }


                        @Override
                        public void infoChanged(SparkAppHandle sparkAppHandle) {
                            System.out.println("Info:" + sparkAppHandle.getState().toString());
                        }
                    });
        } catch (IOException e) {
            System.out.println("Post-process -> CategoryId: " + this.categoryId + ", error message: " + e.getMessage());
            System.out.println("Post-process -> CategoryId: " + this.categoryId + ", attempt retry: " + counter.incrementAndGet());
            this.scheduler.schedule(this, 1, TimeUnit.MINUTES);
            Thread.currentThread().interrupt();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            System.out.println("Post-process -> CategoryId: " + this.categoryId + ", receive interrupt signal!");
            Thread.currentThread().interrupt();
        }
    }
}
