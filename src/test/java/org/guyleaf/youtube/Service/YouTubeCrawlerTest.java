package org.guyleaf.youtube.Service;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assume.assumeNoException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class YouTubeCrawlerTest {
    private YouTubeCategoryURLBuilder categoryURLBuilder;
    private YouTubeVideoURLBuilder youTubeVideoURLBuilder;

    @Before
    public void setUp() {
        Dotenv file = Dotenv.load();
        this.categoryURLBuilder = YouTubeCategoryURLBuilder.build(file.get("CATEGORYBASEURL"), file.get("API_KEY"));
        this.youTubeVideoURLBuilder = YouTubeVideoURLBuilder.build(file.get("VIDEOBASEURL"), file.get("API_KEY"));
    }

    @Test
    public void test_category_request() {
        String httpRequest = this.categoryURLBuilder
                .setParts(new ArrayList<>(Collections.singletonList("snippet")))
                .setRegionCode("TW")
                .setHl("zh_TW")
                .toString();

        YouTubeCrawler crawler = new YouTubeCrawler(httpRequest);

        try {
            String response = crawler.execute();
            assertFalse(response.isEmpty());
            System.out.println(response);
        }
        catch (Exception e) {
            assumeNoException(e);
        }
    }

    @Test
    public void test_video_request() {
        String httpRequest = this.youTubeVideoURLBuilder
                .setParts(new ArrayList<>(Arrays.asList("id", "snippet", "contentDetails", "statistics")))
                .setChart("mostPopular")
                .setRegionCode("TW")
                .setCategoryId("0")
                .toString();

        YouTubeCrawler crawler = new YouTubeCrawler(httpRequest);

        try {
            String response = crawler.execute();
            assertFalse(response.isEmpty());
            System.out.println(response);
        }
        catch (Exception e) {
            assumeNoException(e);
        }
    }
}
