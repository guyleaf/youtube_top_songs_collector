package org.guyleaf.youtube.Service;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class YouTubeCrawler {
    private final String httpRequest;

    public YouTubeCrawler(String httpRequest) {
        this.httpRequest = httpRequest;
    }

    public String nextResponse() throws IOException {
        String response;

        HttpGet request = new HttpGet(this.httpRequest);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = httpResponse.getEntity();
                    response = EntityUtils.toString(entity);
                }
                else {
                    throw new IOException(EntityUtils.toString(httpResponse.getEntity()));
                }
            }
        }

        return response;
    }
}
