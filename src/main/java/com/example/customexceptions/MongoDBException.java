package com.example.customexceptions;

public class MongoDBException extends Exception {
    public MongoDBException(String message) {
        super(message);
    }

    public MongoDBException(String message, Throwable cause) {
        super(message, cause);
    }
}
