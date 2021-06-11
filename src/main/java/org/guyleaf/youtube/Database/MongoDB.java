/*
 * Author: guyleaf
 * Date: 2021-06-30 04:39 PM GMT+8
 * Url: https://github.com/guyleaf
 */

package org.guyleaf.youtube.Database;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
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

    public void connect() {
        this.client = MongoClients.create(this.url);
        this.db = this.client.getDatabase(this.database);
        this.ping();
    }

    public void test() {
        System.out.println("Collections in database: ");
        this.db.listCollectionNames().forEach(item -> System.out.println("\t"+item));
    }

    public void close() {
        this.client.close();
    }
}
