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
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.testvagrant.commons.entities.DeviceDetails;
import com.testvagrant.commons.exceptions.DeviceMatchingException;
import com.testvagrant.devicemanagement.core.MongoBase;
import org.bson.Document;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static com.testvagrant.devicemanagement.core.Constants.*;

public class MongoReader extends MongoIO {

    MongoClient mongoClient;
    ObjectMapper objectMapper;

    public MongoReader() {
        mongoClient = MongoBase.getInstance();
        objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping();
    }

    public List<DeviceDetails> getAllDevices() {
        FindIterable<Document> documents = mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_DEVICES).find(new Document(KEY_BUILD_ID,latestBuildID));
        List<DeviceDetails> deviceDetailsList = new ArrayList<>();
        for (Document document : documents) {
            if(document!=null) {
                DeviceDetails deviceDetails = readDocument(document);
                deviceDetailsList.add(deviceDetails);
            }
        }
        return deviceDetailsList;
    }



    public DeviceDetails getDeviceByUdid(String udid) throws DeviceMatchingException {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(KEY_DEVICES_UDID, udid);
        Document first = mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_DEVICES).find(whereQuery).first();
        if(first!=null)
            return readDocument(first);
        throw new DeviceMatchingException(udid);
    }


}

