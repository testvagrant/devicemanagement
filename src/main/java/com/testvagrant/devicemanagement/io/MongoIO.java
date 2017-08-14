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


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.mongodb.util.JSON;
import com.testvagrant.commons.entities.DeviceDetails;
import com.testvagrant.commons.entities.device.DeviceType;
import com.testvagrant.commons.entities.device.OSVersion;
import com.testvagrant.commons.entities.device.Platform;
import com.testvagrant.commons.entities.device.Status;
import com.testvagrant.devicemanagement.core.MongoBase;
import com.testvagrant.mdb.utils.OSVersionMatcher;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;

import static com.testvagrant.devicemanagement.core.Constants.*;


public class MongoIO {
    protected static ObjectId latestBuildID;
    protected final MongoClient mongoClient;

    public MongoIO() {
        mongoClient = MongoBase.getInstance();
        System.out.println("MongoClient:"+mongoClient.hashCode());
        if(latestBuildID==null) {
            getLatestBuild();
        }
    }

    protected static boolean screenshotExist(MongoCollection<Document> coll, String udid) {
        BasicDBObject query = new BasicDBObject(KEY_SCREENSHOTS_SCREENSHOT, new BasicDBObject(QUERY_NE, "")
                .append(QUERY_EXISTS, true)).append(KEY_DEVICES_UDID, udid).append(KEY_BUILD_ID,latestBuildID);
        return coll.count(query) == 1;
    }

    ObjectId getLatestBuild() {
        Document myDoc = mongoClient.getDatabase(DATABASE_NAME)
                .getCollection(COLLECTION_BUILDS)
                .find().sort(new BasicDBObject(KEY_BUILDS_BUILD_START_TIME, -1)).first();
        if(myDoc!=null) {
            latestBuildID = (ObjectId) myDoc.get("_id");
        }

        return latestBuildID;
    }

    private boolean fileExists(GridFS gridFS, GridFSInputFile file) {
        List<GridFSDBFile> gridFSDBFiles = gridFS.find((DBObject) JSON
                .parse(String.format("{ _id : '%s' }", file.getId())));
        if (gridFSDBFiles.size() > 0) {
            return true;
        }
        return false;
    }



    protected Document createDocument(DeviceDetails deviceDetails) {
        return new Document(KEY_DEVICES_PLATFORM,deviceDetails.getPlatform().name())
                .append(KEY_DEVICES_DEVICENAME,deviceDetails.getDeviceName())
                .append(KEY_DEVICES_STATUS,deviceDetails.getStatus().name())
                .append(KEY_DEVICES_PLATFORM_VERSION,deviceDetails.getOsVersion().getVersion())
                .append(KEY_DEVICES_RUNSON,deviceDetails.getDeviceType().name())
                .append(KEY_DEVICES_UDID,deviceDetails.getUdid());
    }

    protected DeviceDetails readDocument(Document document) {
        Platform platform = Platform.valueOf(document.getString(KEY_DEVICES_PLATFORM));
        OSVersion osVersion = new OSVersionMatcher().getOSVersion(platform,(String) document.get(KEY_DEVICES_PLATFORM_VERSION));
        Status status = Status.valueOf(document.getString(KEY_DEVICES_STATUS));
        DeviceType deviceType = DeviceType.valueOf(document.getString(KEY_DEVICES_RUNSON));
        DeviceDetails deviceDetails = new DeviceDetails();
        deviceDetails.setPlatform(platform);
        deviceDetails.setStatus(status);
        deviceDetails.setOsVersion(osVersion);
        deviceDetails.setDeviceName(document.getString(KEY_DEVICES_DEVICENAME));
        deviceDetails.setDeviceType(deviceType);
        deviceDetails.setUdid(document.getString(KEY_DEVICES_UDID));
        return deviceDetails;
    }
}
