/*
 * Author: guyleaf
 * Date: 2021-06-30 04:23 PM GMT+8
 * Url: https://github.com/guyleaf
 */

package org.guyleaf.youtube;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bson.Document;

import org.guyleaf.youtube.Database.DBConnector;
import org.guyleaf.youtube.Database.MongoDB;
import org.guyleaf.youtube.Model.Category;
import org.guyleaf.youtube.Service.YouTubeVideoURLBuilder;
import org.guyleaf.youtube.Task.CollectingTask;
import org.guyleaf.youtube.Task.CrawlingTask;


public class App 
{
    private static final Dotenv file = Dotenv.load();
    private static final DBConnector db = new MongoDB(file.get("HOST"), file.get("PORT"), file.get("USERNAME"), file.get("PASSWORD"), file.get("DATABASE"));
    private static ScheduledExecutorService scheduledThreadPool;


    private static void setUp() throws FileNotFoundException {
        if (! new File(".env").exists()) {
            throw new FileNotFoundException(".env file not found.");
        }

        System.out.println("#####################################\tTask Started\t#####################################");
        BasicConfigurator.configure();
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.WARN);

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

    public static void main( String[] args ) throws FileNotFoundException {
        setUp();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("#####################################\tTask Shutdown\t#####################################");
            scheduledThreadPool.shutdown();
            db.close();
            System.out.println("service shutdown successfully.");
            System.out.println("Exited!");
        }));
    }
}
