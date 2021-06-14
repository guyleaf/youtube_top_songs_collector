package org.guyleaf.youtube.Task;

import org.bson.Document;
import org.guyleaf.youtube.Database.DBConnector;
import org.guyleaf.youtube.Model.HourlyRank;
import org.guyleaf.youtube.Model.Thumbnail;
import org.guyleaf.youtube.Model.Video;
import org.guyleaf.youtube.Model.VideoSnippet;
import org.guyleaf.youtube.Service.URLBuilder;
import org.guyleaf.youtube.Service.YouTubeCrawler;

import java.text.SimpleDateFormat;

import java.util.List;
import java.util.Calendar;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONObject;

public class CrawlingTask implements Runnable {
    private final ScheduledExecutorService scheduler;
    private final URLBuilder youTubeVideoURLBuilder;
    private final String categoryId;
    private final DBConnector db;
    private final int MAX_RETRY = 5;

    private final AtomicInteger counter;
    private int currentHour;
    private Date currentDate;

    public CrawlingTask(ScheduledExecutorService scheduler, DBConnector db, URLBuilder youTubeVideoURLBuilder, String categoryId) {
        this.youTubeVideoURLBuilder = youTubeVideoURLBuilder;
        this.categoryId = categoryId;
        this.db = db;
        this.scheduler = scheduler;
        this.counter = new AtomicInteger(0);
        this.currentHour = 1; // 1:00-24:00 as a cycle
    }

    private List<String> processRank(JSONArray object) {
        List<String> videoIds = new ArrayList<>();

        for (int i = 0; i < object.length(); i++) {
            JSONObject item = object.getJSONObject(i);
            videoIds.add(item.getString("id"));
        }

        return videoIds;
    }

    private List<Video> processVideo(JSONArray object) {
        List<Video> videos = new ArrayList<>();

        for (int i = 0; i < object.length(); i++) {
            JSONObject item = object.getJSONObject(i);
            VideoSnippet snippet = this.processSnippet(item);
            videos.add(new Video(item.getString("id"),
                        item.getJSONObject("snippet").getString("title"),
                        item.getJSONObject("snippet").getString("description"),
                        snippet));
        }

        return videos;
    }

    private VideoSnippet processSnippet(JSONObject object) {
        JSONObject snippet = object.getJSONObject("snippet");
        JSONObject image = snippet.getJSONObject("thumbnails").getJSONObject("high");
        Thumbnail thumbnail = new Thumbnail(image.getString("url"), image.getInt("width"), image.getInt("height"));
        String channelId = snippet.getString("channelId");
        String channelTitle = snippet.getString("channelTitle");
        String categoryId = snippet.getString("categoryId");
        String viewCount = object.getJSONObject("statistics").optString("viewCount");
        String duration = object.getJSONObject("contentDetails").getString("duration");
        return new VideoSnippet(thumbnail, channelId, channelTitle, categoryId, viewCount, duration);
    }

    private void save(HourlyRank rank, List<Video> videos) {
        Document filter = new Document("categoryId", rank.categoryId())
                .append("collectedHour", rank.collectedHour())
                .append("collectedDate", rank.collectedDate());
        this.db.upsertOne("hourlyRank", filter, rank.toDocument());

        List<Document> filters = new ArrayList<>();
        List<Document> data = new ArrayList<>();

        for (Video video: videos) {
            data.add(video.toDocument());
            filters.add(new Document("id", video.id()));
        }
        this.db.bulkUpsert("video", filters, data);
    }

    @Override
    public void run() {
        try {
            String token = "";
            HourlyRank rank;
            List<Video> videos = new ArrayList<>();

            if (this.currentHour == 1) {
                this.currentDate = Calendar.getInstance(TimeZone.getTimeZone("Asia/Taipei")).getTime();
            }

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            rank = new HourlyRank(this.categoryId, this.currentHour, formatter.format(this.currentDate));

            System.out.printf("CategoryId: %s, hour: %d, date: %s\n", this.categoryId, rank.collectedHour(), rank.collectedDate());

            // 50 queries per request
            while (true) {
                String httpRequest = this.youTubeVideoURLBuilder.setPageToken(token).toString();
                YouTubeCrawler crawler = new YouTubeCrawler(httpRequest);
                String response = crawler.execute();
                JSONObject object = new JSONObject(response);

                this.processRank(object.getJSONArray("items")).forEach(rank::add);
                videos.addAll(Objects.requireNonNull(this.processVideo(object.getJSONArray("items"))));

                // End of json
                if (object.isNull("nextPageToken")) {
                    break;
                }
                token = object.getString("nextPageToken");

                Thread.sleep(1000);
            }

            this.save(rank, videos);
        }
        catch (InterruptedException e) {
            System.out.println("CategoryId: " + this.categoryId + ", receive interrupt signal!");
            Thread.currentThread().interrupt();
        }
        catch (Exception e) {
            System.out.println("CategoryId: " + this.categoryId + ", error message: " + e.getMessage());
            System.out.println("CategoryId: " + this.categoryId + ", attempt request: " + counter.incrementAndGet() + " try.");
            this.scheduler.schedule(this, 1, TimeUnit.MINUTES);
            Thread.currentThread().interrupt();
        }

        // if finish a cycle, then do collecting task
        if (this.currentHour == 24) {
            this.scheduler.schedule(new CollectingTask(this.scheduler, this.categoryId, this.currentDate), 1, TimeUnit.SECONDS);
        }

        this.currentHour = (this.currentHour + 1) % 25;
        this.currentHour = this.currentHour == 0 ? 1 : this.currentHour;
        // reset failed counter
        this.counter.set(0);
    }
}
