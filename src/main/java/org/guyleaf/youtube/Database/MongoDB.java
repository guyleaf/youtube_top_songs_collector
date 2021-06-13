/*
 * Author: guyleaf
 * Date: 2021-06-30 04:39 PM GMT+8
 * Url: https://github.com/guyleaf
 */

package org.guyleaf.youtube.Database;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOneModel;
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.bson.conversions.Bson;

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
    public <T> void query(String collection, Consumer<T> consumer, Class<T> modelObject) {
        this.checkConnection();
        MongoCollection<T> tmp = this.db.getCollection(collection, modelObject);
        tmp.find().forEach(consumer);
    }

    @Override
    public <T> void insertMany(String collection, List<T> data, Class<T> modelObject) {
        this.checkConnection();
        MongoCollection<T> tmp = this.db.getCollection(collection, modelObject);
        tmp.insertMany(data);
    }

    @Override
    public <T> void insertOne(String collection, T data, Class<T> modelObject) {
        this.checkConnection();
        MongoCollection<T> tmp = this.db.getCollection(collection, modelObject);
        tmp.insertOne(data);
    }

    @Override
    public <T> void upsertMany(String collection, Bson filter, List<T> data, Class<T> modelObject) {
        this.checkConnection();
        MongoCollection<Document> tmp = this.db.getCollection(collection);

    }

    @Override
    public <T> void upsertOne(String collection, T data, Class<T> modelObject) {

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
