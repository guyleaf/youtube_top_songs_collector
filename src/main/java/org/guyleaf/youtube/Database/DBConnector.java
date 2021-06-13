/*
 * Author: guyleaf
 * Date: 2021-06-30 04:39 PM GMT+8
 * Url: https://github.com/guyleaf
 */

package org.guyleaf.youtube.Database;

import org.bson.conversions.Bson;

import java.util.List;
import java.util.function.Consumer;

public interface DBConnector {
    void connect();
    <T> void query(String collection, Consumer<T> consumer, Class<T> modelObject);
    <T> void insertMany(String collection, List<T> data, Class<T> modelObject);
    <T> void insertOne(String collection, T data, Class<T> modelObject);
    <T> void upsertOne(String collection, T data, Class<T> modelObject);
    <T> void upsertMany(String collection, Bson filter, List<T> data, Class<T> modelObject);
    void test();
    void close();
}
