package org.guyleaf.youtube.Service;

import java.util.List;
import java.util.NoSuchElementException;

public interface URLBuilder {
    default URLBuilder setParts(List<String> parts) throws NoSuchElementException {
        throw new NoSuchElementException("Undefined setParts method.");
    }
    default URLBuilder setChart(String chart) throws NoSuchElementException {
        throw new NoSuchElementException("Undefined setChart method.");
    }
    default URLBuilder setRegionCode(String regionCode) throws NoSuchElementException {
        throw new NoSuchElementException("Undefined setRegionCode method.");
    }
    default URLBuilder setHl(String hl) throws NoSuchElementException {
        throw new NoSuchElementException("Undefined setHl method.");
    }
    default URLBuilder setPageToken(String pageToken) throws NoSuchElementException {
        throw new NoSuchElementException("Undefined setPageToken method.");
    }
    default URLBuilder setCategoryId(String categoryId) throws NoSuchElementException {
        throw new NoSuchElementException("Undefined setCategoryId method.");
    }
    default URLBuilder setMaxResults(int maxResults) throws NoSuchElementException {
        throw new NoSuchElementException("Undefined setMaxResults method.");
    }
    String toString();
}
