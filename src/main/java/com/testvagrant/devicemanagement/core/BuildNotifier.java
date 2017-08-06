package com.testvagrant.devicemanagement.core;


import org.bson.Document;

import java.util.Date;

import static com.testvagrant.devicemanagement.core.Constants.*;

public class BuildNotifier {

    public BuildNotifier() {

    }

    public void notifyBuildStart() {
        Date buildStartTime = new Date();
        Document build_time = new Document(KEY_BUILDS_BUILD_START_TIME, buildStartTime)
                .append(KEY_BUILDS_BUILD_END_TIME, new Date())
                .append(KEY_BUILDS_SCENARIOS_COUNT, 0)
                .append(KEY_BUILDS_SCENARIO_SUCCESS_RATE, 0.0);
        MongoBase.getInstance().getDatabase(DATABASE_NAME).getCollection(COLLECTION_BUILDS).insertOne(build_time);
    }
}
