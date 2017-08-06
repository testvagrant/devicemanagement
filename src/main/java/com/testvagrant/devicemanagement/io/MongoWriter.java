/*
 * Copyright (c) 2017.  TestVagrant Technologies
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.testvagrant.devicemanagement.io;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.testvagrant.commons.entities.DeviceDetails;
import com.testvagrant.commons.entities.SmartBOT;
import com.testvagrant.commons.entities.reportParser.ExecutedScenario;
import com.testvagrant.commons.exceptions.DeviceEngagedException;
import com.testvagrant.devicemanagement.utils.DeviceMatcherFunction;
import org.apache.commons.collections.IteratorUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.testvagrant.devicemanagement.core.Constants.*;

public class MongoWriter extends MongoIO {

    private ObjectMapper objectMapper;
    public MongoWriter() {
        super();
        objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping();
    }

    public void insertDeviceList(List<DeviceDetails> deviceDetailsList) {
        latestBuildID = getLatestBuild();
        for (DeviceDetails deviceDetails : deviceDetailsList) {
            Document insertDocument = createDocument(deviceDetails);
            mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_DEVICES).insertOne(insertDocument.append(KEY_BUILD_ID, latestBuildID));
        }
    }

    public void updateDeviceScreenshot(String udid, byte[] screenshot) {
        if (!screenshotExist(mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_DEVICES), udid)) {
            mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_DEVICES).updateOne(new BasicDBObject(KEY_DEVICES_UDID, udid).append(KEY_BUILD_ID, latestBuildID),
                    new BasicDBObject(QUERY_SET, new Document().append(KEY_SCREENSHOTS_SCREENSHOT, screenshot)));
        }
    }

    public void notifyBOTRegistration(SmartBOT smartBOT) {
        Document document = new Document().append(KEY_SCENARIOS_SCENARIO_NAME, smartBOT.getScenario().getId())
                .append(KEY_SCENARIOS_DEVICE_UDID, smartBOT.getDeviceUdid())
                .append(KEY_SCENARIOS_TAGS, smartBOT.getScenario().getSourceTagNames())
                .append(KEY_SCENARIOS_START_TIME, new Date())
                .append(KEY_BUILD_ID, latestBuildID);
        mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_SCENARIOS).insertOne(document);
    }

    public void notifyScenarioCompletion(SmartBOT smartBOT) {
        Object latestRecord = getLatestRecordFor(smartBOT);

        Date start_time = (Date) getStartTimeOf(latestRecord);
        Date end_time = new Date();

        long seconds = (end_time.getTime() - start_time.getTime()) / 1000;

        mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_SCENARIOS)
                .updateOne(eq(ID, latestRecord), new Document(QUERY_SET, new Document()
                        .append(KEY_SCENARIOS_STATUS, smartBOT.getScenario().getStatus())
                        .append(KEY_SCENARIOS_COMPLETED, true)
                        .append(KEY_SCENARIOS_END_TIME, end_time)
                        .append(KEY_SCENARIOS_TIME_TAKEN, Math.toIntExact(seconds))));

    }

    private Object getStartTimeOf(Object latestRecord) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(ID, latestRecord);

        return mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_SCENARIOS).find(whereQuery).first().get(KEY_SCENARIOS_START_TIME);
    }

    private Object getLatestRecordFor(SmartBOT smartBOT) {

        BasicDBObject andQuery = new BasicDBObject();

        List<BasicDBObject> objects = new ArrayList<>();
        objects.add(new BasicDBObject(KEY_SCENARIOS_SCENARIO_NAME, smartBOT.getScenario().getId()));
        objects.add(new BasicDBObject(KEY_SCENARIOS_DEVICE_UDID, smartBOT.getDeviceUdid()));
        andQuery.put(QUERY_AND, objects);

        Document document = mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_SCENARIOS)
                .find(andQuery)
                .sort(new BasicDBObject(KEY_SCENARIOS_START_TIME, -1)).first();
        return document.get(ID);

    }

    protected Object getLatestRecordFor(ExecutedScenario scenario) {
        BasicDBObject andQuery = new BasicDBObject();
        System.out.println(scenario.getId());
        System.out.println(scenario.getDeviceName());
        List<BasicDBObject> objects = new ArrayList<>();
        objects.add(new BasicDBObject(KEY_SCENARIOS_SCENARIO_NAME, scenario.getId()));
        objects.add(new BasicDBObject(KEY_SCENARIOS_DEVICE_UDID, scenario.getDeviceName()));
        andQuery.put(QUERY_AND, objects);

        Document document = mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_SCENARIOS)
                .find(andQuery)
                .sort(new BasicDBObject(KEY_SCENARIOS_START_TIME, -1)).first();

        return document.get(ID);
    }



    public synchronized DeviceDetails updateFirstAvailableDeviceToEngaged(JSONObject testFeed) throws DeviceEngagedException {
        System.out.println("Updating first available device to Engaged");
        BasicDBObject andQuery = new DeviceMatcherFunction().prepareQuery(testFeed).append(KEY_BUILD_ID, latestBuildID);
        DeviceDetails deviceDetails = null;
        synchronized (Thread.currentThread()) {
            MongoCollection<Document> collection = mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_DEVICES);
            Document first = collection.find(andQuery).first();
            System.out.println(first.toString());
            try {
                collection.updateOne(first, new Document(QUERY_SET, new Document(KEY_DEVICES_STATUS, "Engaged")));
            } catch (Exception e) {
                throw new DeviceEngagedException();
            }

            try {
                deviceDetails = objectMapper.readValue(first.toJson(), DeviceDetails.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(String.format("Updated device %s to engaged",deviceDetails.getDeviceName()));
        return deviceDetails;
    }

    public synchronized DeviceDetails updateFirstAvailableDeviceToEngaged(String udid) throws DeviceEngagedException {
        System.out.println(latestBuildID);
        BasicDBObject andQuery = new BasicDBObject(KEY_SCENARIOS_DEVICE_UDID,udid).append(KEY_BUILD_ID, latestBuildID);
        MongoCollection<Document> collection = mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_DEVICES);

        Document first = collection.find(andQuery).first();
        System.out.println(first.toString());
        try {
            collection.updateOne(first, new Document(QUERY_SET, new Document(KEY_DEVICES_STATUS, "Engaged")));
        } catch (Exception e) {
            throw new DeviceEngagedException();
        }
        DeviceDetails deviceDetails = null;
        try {
            deviceDetails = objectMapper.readValue(first.toJson(), DeviceDetails.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deviceDetails;
    }


    public void updateStatusToAvailableForDevice(String udid) {
        mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_DEVICES)
                .updateOne(new Document(KEY_DEVICES_UDID, udid).append(KEY_BUILD_ID, latestBuildID), new Document(QUERY_SET, new Document(KEY_DEVICES_STATUS, "Available")));
    }

    public void updateExecutionDetailsFor(List<ExecutedScenario> scenarios) {
        for (ExecutedScenario scenario : scenarios) {
            Object latestRecordFor = getLatestRecordFor(scenario);
            Document queryDocument = new Document(ID, latestRecordFor);
            Document updateDocument = new Document(QUERY_SET,
                    new Document(KEY_EXEC_DETAILS_STEPS, new Gson().toJson(scenario.getSteps()))
                            .append(KEY_EXEC_DETAILS_FAILED_SCREEN, scenario.getEmbeddedFailedScreen()));

            mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_SCENARIOS).findOneAndUpdate(queryDocument, updateDocument);
        }
    }

    public void notifyBuildStart() {
        Date buildStartTime = new Date();
        Document build_time = new Document(KEY_BUILDS_BUILD_START_TIME, buildStartTime)
                .append(KEY_BUILDS_BUILD_END_TIME, new Date())
                .append(KEY_BUILDS_SCENARIOS_COUNT, 0)
                .append(KEY_BUILDS_SCENARIO_SUCCESS_RATE, 0.0);
        mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_BUILDS).insertOne(build_time);
    }

    public void notifyBuildEnd() {
        BasicDBObject queryBuildStartTime = new BasicDBObject(ID, latestBuildID);
        Document buildEndTime = new Document(QUERY_SET, new Document(KEY_BUILDS_BUILD_END_TIME, new Date()));
        mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_BUILDS)
                .findOneAndUpdate(queryBuildStartTime, buildEndTime);
    }

    public void updateCrashes(SmartBOT bot, String exceptions, String activity) {
        if(exceptions !=null && exceptions.length()>0) {
            ObjectId latestScenario = (ObjectId) getLatestRecordFor(bot);
            Document queryDocument = new Document(ID, latestScenario);
            Document updateDocument = new Document(KEY_SCENARIO_ID, latestScenario)
                    .append(KEY_CRASHES_STACKTRACE, exceptions)
                    .append(KEY_CRASHES_ACTIVITY, activity);
            mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_SCENARIOS).findOneAndUpdate(queryDocument, updateDocument);
        }
    }

    public void updateBuildWithUniqueScenarios() {
        try {
            DistinctIterable<String> distinctScenarios = mongoClient.getDatabase(DATABASE_NAME)
                    .getCollection(COLLECTION_SCENARIOS)
                    .distinct(KEY_SCENARIOS_SCENARIO_NAME, String.class);

            Collection<String> scenarios = IteratorUtils.toList(distinctScenarios.iterator());
            int numberOfUniqueScenarios = scenarios.size();

            DecimalFormat df = new DecimalFormat("#.0");
            Collection<String> passedScenarios = new ArrayList<>();
            for (String scenario : distinctScenarios) {
                String status = mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_SCENARIOS)
                        .find(new BasicDBObject(KEY_SCENARIOS_SCENARIO_NAME, scenario))
                        .sort(new BasicDBObject(KEY_SCENARIOS_START_TIME, -1)).first().getString(KEY_SCENARIOS_STATUS);
                if (status.equalsIgnoreCase(STATUS_PASSED))
                    passedScenarios.add(scenario);
            }
            int passedScenariosCount = passedScenarios.size();
            float pass_percentage = (passedScenariosCount * 100.0f) / numberOfUniqueScenarios;
            Document queryDocument = new Document(ID, latestBuildID);
            Document updateDocument = new Document(QUERY_SET, new Document(KEY_BUILDS_SCENARIOS_COUNT, numberOfUniqueScenarios).append(KEY_BUILDS_SCENARIO_SUCCESS_RATE, df.format(pass_percentage)));
            mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_BUILDS).findOneAndUpdate(queryDocument, updateDocument);
        } catch (Exception e) {
            //Ignore exception
        }
    }

}
