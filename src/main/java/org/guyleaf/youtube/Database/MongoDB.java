/*
 * Author: guyleaf
 * Date: 2021-06-30 04:39 PM GMT+8
 * Url: https://github.com/guyleaf
 */

package org.guyleaf.youtube.Database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;

import org.apache.log4j.*;
import org.bson.Document;

public class MongoDB implements DBConnector {
    private MongoClient client;
    private final String url;
    private final String database;
    private MongoDatabase db;

    public MongoDB(String host, String port, String user, String password, String database) {
        this.client = null;
        this.database = database;
        this.url = String.format("mongodb://%s:%s@%s:%s/?authSource=%s", user, password, host, port, database);

        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.addAppender(new ConsoleAppender(
                new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
        mongoLogger.setLevel(Level.INFO); // e.g. or Log.WARNING, etc.
    }

    private void ping() {
        try {
            this.db.runCommand(new Document("ping", 1));
        }
        catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        }
    }

    private void checkConnection() {
        if (this.db == null) {
            throw new RuntimeException("Database connection not found, please connect before doing operation");
        }
        else {
            this.ping();
        }
    }

    @Override
    public void query(String collection, Consumer<Document> consumer) {
        this.checkConnection();
        MongoCollection<Document> tmp = this.db.getCollection(collection);
        tmp.find().forEach(consumer);
    }

    @Override
    public void query(String collection, Document filter, Consumer<Document> consumer) {
        this.checkConnection();
        MongoCollection<Document> tmp = this.db.getCollection(collection);
        tmp.find(filter).forEach(consumer);
    }

    @Override
    public void insertMany(String collection, List<Document> data) {
        this.checkConnection();
        MongoCollection<Document> tmp = this.db.getCollection(collection);
        tmp.insertMany(data);
    }

    @Override
    public void insertOne(String collection, Document data) {
        this.checkConnection();
        MongoCollection<Document> tmp = this.db.getCollection(collection);
        tmp.insertOne(data);
    }

    /**
     * Upsert multiple document in different rule
     * @param collection collection name
     * @param filter A list of rules
     * @param data A list of data
     */
    @Override
    public void bulkUpsert(String collection, List<Document> filter, List<Document> data) {
        this.checkConnection();

        List<UpdateOneModel<Document>> docs = new ArrayList<>();
        Iterator<Document> filter_iter = filter.iterator();
        Iterator<Document> data_iter = data.iterator();

        while (filter_iter.hasNext() && data_iter.hasNext()) {
            docs.add(new UpdateOneModel<>(filter_iter.next(), new Document("$setOnInsert",  data_iter.next()), new UpdateOptions().upsert(true)));
        }

        MongoCollection<Document> tmp = this.db.getCollection(collection);
        tmp.bulkWrite(docs);
    }

    @Override
    public void upsertOne(String collection, Document filter, Document data) {
        this.checkConnection();
        data = new Document("$setOnInsert", data);
        MongoCollection<Document> tmp = this.db.getCollection(collection);
        tmp.updateOne(filter, data, new UpdateOptions().upsert(true));
    }

    @Override
    public void connect() {
        this.client = MongoClients.create(this.url);
        this.db = this.client.getDatabase(this.database);
        this.ping();
    }

    @Override
    public void test() {
        System.out.println("Collections in database: ");
        this.db.listCollectionNames().forEach(item -> System.out.println("\t"+item));
    }

    @Override
    public void close() {
        this.client.close();
    }
}
