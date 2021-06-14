/*
 * Author: guyleaf
 * Date: 2021-06-30 04:39 PM GMT+8
 * Url: https://github.com/guyleaf
 */

package org.guyleaf.youtube.Database;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.List;
import java.util.function.Consumer;

public interface DBConnector {
    void connect();
    void query(String collection, Consumer<Document> consumer);
    void query(String collection, Document filter, Consumer<Document> consumer);
    void insertMany(String collection, List<Document> data);
    void insertOne(String collection, Document data);
    void upsertOne(String collection, Document filter, Document data);

    /**
     * Upsert multiple document in different rule
     * @param collection collection name
     * @param filter A list of rules
     * @param data A list of data
     */
    void bulkUpsert(String collection, List<Document> filter, List<Document> data);
    void test();
    void close();
}
