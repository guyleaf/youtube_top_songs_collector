package org.guyleaf.youtube.Service;

import java.util.List;

public class YouTubeVideoURLBuilder implements URLBuilder{
    private final String baseURL;
    private final String key;
    private String parts;
    private String chart;
    private String pageToken;
    private String regionCode;
    private int categoryId;
    private int maxResults;

    public static YouTubeVideoURLBuilder build(String baseURL, String key) {
        return new YouTubeVideoURLBuilder(baseURL, key);
    }

    private YouTubeVideoURLBuilder(String baseURL, String key) {
        this.baseURL = baseURL;
        this.key = key;
        this.parts = "";
        this.chart = "";
        this.pageToken = "";
        this.regionCode = "";
        this.categoryId = 0;
        this.maxResults = 5;
    }

    @Override
    public URLBuilder setParts(List<String> parts) {
        StringBuilder builder = new StringBuilder();

        for (String part: parts) {
            builder.append(part).append(",");
        }

        builder.deleteCharAt(builder.length() - 1);

        this.parts = builder.toString();
        return this;
    }

    @Override
    public URLBuilder setChart(String chart) {
        this.chart = chart;
        return this;
    }

    @Override
    public URLBuilder setPageToken(String pageToken) {
        this.pageToken = pageToken;
        return this;
    }

    @Override
    public URLBuilder setRegionCode(String regionCode) {
        this.regionCode = regionCode;
        return this;
    }

    @Override
    public URLBuilder setCategoryId(int categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    @Override
    public URLBuilder setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    @Override
    public String toString() {
        if (this.chart.isEmpty() || this.parts.isEmpty()) {
            throw new NullPointerException("Chart or Part parameter should not be empty.");
        }

        String url = this.baseURL + "?" +
                "part=" + this.parts + "&" +
                "chart=" + this.chart + "&" +
                "maxResults=" + this.maxResults + "&";

        if (!this.pageToken.isEmpty()) {
            url += "pageToken=" + this.pageToken + "&";
        }

        if (!this.regionCode.isEmpty()) {
            url += "regionCode=" + this.regionCode + "&";
        }

        url += "categoryId=" + this.categoryId + "&" + "key=" + this.key;

        return url;
    }
}
