package com.testvagrant.devicemanagement.core;

import com.testvagrant.commons.entities.OptimusConfiguration;

import java.io.File;
import java.io.IOException;

import static com.testvagrant.commons.utils.OptimusConfigMapper.mapper;

public class MongoPort {

    private MongoPort() {

    }

    public static MongoPort mongoPort() {
        return new MongoPort();
    }

    public int getPort() {
        File file = new File(System.getProperty("user.dir") + "/src/test/resources/META-INF/Optimus.yaml");
        if(file.exists()) {
            OptimusConfiguration configuration = null;
            try {
                configuration = mapper(file).map(OptimusConfiguration.class);
                if(configuration.getMongoPort()!=0) {
                    return configuration.getMongoPort();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 27017;
    }
}
