/*
 * Author: guyleaf
 * Date: 2021-06-30 04:23 PM GMT+8
 * Url: https://github.com/guyleaf
 */

package org.guyleaf.youtube;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;

import org.guyleaf.youtube.Database.DBConnector;
import org.guyleaf.youtube.Database.MongoDB;
import org.guyleaf.youtube.Model.Category;
import org.guyleaf.youtube.Service.YouTubeVideoURLBuilder;
import org.guyleaf.youtube.Task.CrawlingTask;

import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;


public class App 
{
    private static final Dotenv file = Dotenv.load();
    private static final DBConnector db = new MongoDB(file.get("HOST"), file.get("PORT"), file.get("USERNAME"), file.get("PASSWORD"), file.get("DATABASE"));
    private static ScheduledExecutorService scheduledThreadPool;


    private static void setUp() {
        System.out.println("#####################################\tTask Started\t#####################################");
        db.connect();

        List<Category> categoryList = new ArrayList<>();
        Consumer<Document> consumer = document -> categoryList.add(new Category(document.getString("id"), document.getString("title")));
        db.query("category", consumer);

        scheduledThreadPool = Executors.newScheduledThreadPool(10);
        for (Category category: categoryList) {
            YouTubeVideoURLBuilder builder = YouTubeVideoURLBuilder.build(file.get("VIDEOBASEURL"), file.get("API_KEY"))
                    .setParts(new ArrayList<>(Arrays.asList("id", "snippet", "contentDetails", "statistics")))
                    .setChart("mostPopular")
                    .setRegionCode("TW")
                    .setCategoryId(category.id())
                    .setMaxResults(50);
            scheduledThreadPool.scheduleAtFixedRate(new CrawlingTask(scheduledThreadPool, db, builder, category.id()), 1, 3600, TimeUnit.SECONDS);
        }
        System.out.println("#####################################\tTask Init Complete\t#####################################");
    }

    public static void main( String[] args ) throws IOException, InterruptedException {
//        setUp();
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            System.out.println("#####################################\tTask Shutdown\t#####################################");
//            scheduledThreadPool.shutdown();
//            db.close();
//            System.out.println("service shutdown successfully.");
//            System.out.println("Exited!");
//        }));
        Map<String, String> env = new HashMap<>();
        env.put("SPARK_URL", "spark://spark-master:7077");
        env.put("DB_INPUT_URI", String.format("mongodb://%s:%s@%s:%s/%s.hourlyRank?authSource=%s", file.get("USERNAME"), file.get("PASSWORD"), file.get("HOST"), file.get("PORT"), file.get("DATABASE"), file.get("DATABASE")));
        env.put("DB_OUTPUT_URI", String.format("mongodb://%s:%s@%s:%s/%s.dailyRank?authSource=%s", file.get("USERNAME"), file.get("PASSWORD"), file.get("HOST"), file.get("PORT"), file.get("DATABASE"), file.get("DATABASE")));
        env.put("JAVA_HOME", "/usr/lib/jvm/default-jvm");

        CountDownLatch countDownLatch = new CountDownLatch(1);
        SparkAppHandle handle = new SparkLauncher(env)
                .setSparkHome("/spark")
                .setAppResource("/tmp/DailyRankSparkCollector.jar")
                .setMainClass("org.guyleaf.dailyRankSparkCollector.App")
                .setMaster("spark://spark-master:7077")
                .setConf(SparkLauncher.DRIVER_MEMORY, "2g")
                .setConf(SparkLauncher.EXECUTOR_MEMORY, "1g")
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
        countDownLatch.await();
    }
}
