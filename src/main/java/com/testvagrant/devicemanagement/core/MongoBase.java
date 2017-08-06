package com.testvagrant.devicemanagement.core;


import com.mongodb.MongoClient;

import static com.testvagrant.devicemanagement.core.MongoPort.mongoPort;

public class MongoBase {

    private static MongoClient mongoClient = null;

    //Singleton
    private MongoBase() {
    }



    public synchronized static MongoClient getInstance() {
        resetMongoClientIfRequired();
        if(mongoClient==null) {
//            MongoClientURI uri = new MongoClientURI(
//                    "mongodb://krishnanandb:Krishna%400405@firstcluster-shard-00-00-unuus.mongodb.net:27017,firstcluster-shard-00-01-unuus.mongodb.net:27017,firstcluster-shard-00-02-unuus.mongodb.net:27017/optimus?ssl=true&replicaSet=FirstCluster-shard-0&authSource=admin"
//            );
//             mongoClient = new MongoClient(uri);
            mongoClient = new MongoClient(optimusHost(), optimusPort());
        }
        return mongoClient;
    }


    private static String optimusHost() {
        return "localhost";
    }

    private static int optimusPort() {
        return mongoPort().getPort();
    }



    public synchronized static void close(){
        mongoClient.close();
    }


    private static void resetMongoClientIfRequired() {
        if(mongoClient!=null) {
            try {
                mongoClient.getConnectPoint();
            } catch (Exception e) {
                mongoClient = new MongoClient(optimusHost(), optimusPort());
            }
        }
    }

}
