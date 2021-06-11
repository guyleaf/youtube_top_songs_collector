package org.guyleaf.youtube.Service;

import java.util.List;

import com.neovisionaries.i18n.CountryCode;

public class YouTubeCategoryURLBuilder implements URLBuilder {
    private final String baseURL;
    private final String key;
    private String parts;
    private String regionCode;
    private String hl;

    public static YouTubeCategoryURLBuilder build(String baseURL, String key) {
        return new YouTubeCategoryURLBuilder(baseURL, key);
    }

    private YouTubeCategoryURLBuilder(String baseURL, String key) {
        this.baseURL = baseURL;
        this.key = key;
        this.parts = "";
        this.regionCode = "";
        this.hl = "en_US";
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
    public URLBuilder setRegionCode(String regionCode) {
        if (CountryCode.getByAlpha2Code(regionCode) == null) {
            throw new NullPointerException("Region code format should conform ISO 3166-1 alpha-2.");
        }

        this.regionCode = regionCode;
        return this;
    }

    @Override
    public URLBuilder setHl(String hl) {
        this.hl = hl;
        return this;
    }

    @Override
    public String toString() {
        if (this.parts.isEmpty()) {
            throw new NullPointerException("Chart or Part parameter should not be empty.");
        }

        String url = this.baseURL + "?" +
                "part=" + this.parts + "&";

        if (!this.regionCode.isEmpty()) {
            url += "regionCode=" + this.regionCode + "&";
        }

        url += "hl=" + this.hl + "&" + "key=" + this.key;

        return url;
    }
}
