package com.example.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.machinezoo.noexception.Exceptions;
import java.util.ArrayList;
import static com.example.utils.StringUtil.validateNonNullAndNonEmpty;

public final class MongoDBUtil {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoCollection;
    private InsertOneResult result;

    private static final MongoDBUtil instance = new MongoDBUtil();

    private MongoDBUtil() {
    }

    public static MongoDBUtil getInstance() {
        return instance;
    }

    public MongoDBUtil connectClient(String connectionString) {
        validateNonNullAndNonEmpty(connectionString, "Connection String");

        Exceptions.wrap(e -> new RuntimeException("Failed to connect to MongoDB client", e)).run(() -> {
            mongoClient = MongoClients.create(connectionString);
        });
        return this;
    }

    public MongoDBUtil useDatabase(String dbName) {
        validateNonNullAndNonEmpty(dbName, "Database Name");

        Exceptions.wrap(e -> new RuntimeException("Failed to access database: " + dbName, e)).run(() -> {
            boolean isDBExists = mongoClient.listDatabaseNames().into(new ArrayList<>()).contains(dbName);
            if (!isDBExists) {
                throw new RuntimeException("Database with name '" + dbName + "' does not exist.");
            }
            mongoDatabase = mongoClient.getDatabase(dbName);
        });
        return this;
    }

    public MongoDBUtil useCollection(String collectionName) {
        validateNonNullAndNonEmpty(collectionName, "Collection Name");
        
        Exceptions.wrap(e -> new RuntimeException("Failed to access collection: " + collectionName, e)).run(() -> {
            boolean isCollectionExists = mongoDatabase.listCollectionNames().into(new ArrayList<>())
                    .contains(collectionName);
            if (!isCollectionExists) {
                throw new RuntimeException("Collection with name '" + collectionName + "' does not exist.");
            }
            mongoCollection = mongoDatabase.getCollection(collectionName);
        });
        return this;
    }

    public MongoDBUtil insertDocument(Document document) {
        if (document == null || document.isEmpty()) {
            throw new IllegalArgumentException("Document cannot be null or empty.");
        }
        Exceptions.wrap(e -> new RuntimeException("Failed to insert document", e)).run(() -> {
            result = mongoCollection.insertOne(document);
        });
        return this;
    }

    public ObjectId getInsertedId() {
        return Exceptions.wrap(e -> new RuntimeException("Failed to retrieve inserted document ID", e)).get(() -> {
            return result.getInsertedId().asObjectId().getValue();
        });
    }

    public void closeClient() {
        Exceptions.wrap(e -> new RuntimeException("Failed to close MongoClient", e)).run(() -> {
            if (mongoClient != null) {
                mongoClient.close();
            }
        });
    }
}
