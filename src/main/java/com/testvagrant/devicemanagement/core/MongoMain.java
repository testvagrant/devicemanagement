package com.testvagrant.devicemanagement.core;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.testvagrant.devicemanagement.exceptions.MongoInstanceException;

import java.util.ArrayList;

public class MongoMain {

    private static String mongoMainString;

    public static void main(String[] args) throws MongoInstanceException {
        System.out.println("Getting started with mongo main");
        MongoClient mongoClient = MongoBase.getInstance();
        MongoDatabase optimus = mongoClient.getDatabase("optimus");
        createCollection(optimus, "scenarios");
        createCollection(optimus, "builds");
        createCollection(optimus, "devices");
        createCollection(optimus, "intellisense");
        new BuildNotifier().notifyBuildStart();
    }

    public static void closeMongo() {
        MongoBase.close();
    }

    private static void createCollection(MongoDatabase dbname, String collectionName) {
        if (!collectionExists(dbname, collectionName)) {
            System.out.println("Created collection " + collectionName);
            dbname.createCollection(collectionName);
        }
    }

    private static boolean collectionExists(MongoDatabase dbName, String collectionName) {
        return dbName.listCollectionNames()
                .into(new ArrayList<>()).contains(collectionName);
    }

}
