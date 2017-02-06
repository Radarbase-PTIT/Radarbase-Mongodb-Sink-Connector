/*
 *  Copyright 2016 Kings College London and The Hyve
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.radarcns.mongodb;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import org.apache.kafka.common.config.AbstractConfig;
import org.bson.Document;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.radarcns.mongodb.MongoDbSinkConnector.*;

public class MongoWrapperTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void checkConnectionWithoutCredentials() throws Exception {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put(MONGO_USERNAME, null);
        configMap.put(MONGO_PASSWORD, null);
        configMap.put(MONGO_PORT, MONGO_PORT_DEFAULT + 1000);
        configMap.put(MONGO_HOST, "localhost");
        configMap.put(MONGO_DATABASE, "mydb");
        configMap.put(COLLECTION_FORMAT, "{$topic}");

        Field credentialsField = MongoWrapper.class.getDeclaredField("credentials");
        credentialsField.setAccessible(true);
        MongoClientOptions timeout = MongoClientOptions.builder()
                .connectTimeout(1)
                .socketTimeout(1)
                .serverSelectionTimeout(1)
                .build();
        MongoWrapper wrapper = new MongoWrapper(new AbstractConfig(configMap), timeout);

        assertThat((List<?>)credentialsField.get(wrapper), empty());
        assertFalse(wrapper.checkConnection());

        thrown.expect(MongoException.class);
        try {
            wrapper.store("mytopic", new Document());
        } finally {
            wrapper.close();
        }


    }

    @Test
    public void checkConnectionWithEmptyCredentials() throws Exception {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put(MONGO_USERNAME, "");
        configMap.put(MONGO_PASSWORD, "");
        configMap.put(MONGO_PORT, MONGO_PORT_DEFAULT + 1000);
        configMap.put(MONGO_HOST, "localhost");
        configMap.put(MONGO_DATABASE, "mydb");
        configMap.put(COLLECTION_FORMAT, "{$topic}");

        Field credentialsField = MongoWrapper.class.getDeclaredField("credentials");
        credentialsField.setAccessible(true);
        MongoClientOptions timeout = MongoClientOptions.builder()
                .connectTimeout(1)
                .socketTimeout(1)
                .serverSelectionTimeout(1)
                .build();
        MongoWrapper wrapper = new MongoWrapper(new AbstractConfig(configMap), timeout);

        assertThat((List<?>)credentialsField.get(wrapper), empty());
        assertFalse(wrapper.checkConnection());

        thrown.expect(MongoException.class);
        try {
            wrapper.store("mytopic", new Document());
        } finally {
            wrapper.close();
        }
    }

    @Test
    public void checkConnectionWithCredentials() throws Exception {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put(MONGO_USERNAME, "myuser");
        configMap.put(MONGO_PASSWORD, "mypassword");
        configMap.put(MONGO_PORT, MONGO_PORT_DEFAULT + 1000);
        configMap.put(MONGO_HOST, "localhost");
        configMap.put(MONGO_DATABASE, "mydb");
        configMap.put(COLLECTION_FORMAT, "{$topic}");

        Field credentialsField = MongoWrapper.class.getDeclaredField("credentials");
        credentialsField.setAccessible(true);
        MongoClientOptions timeout = MongoClientOptions.builder()
                .connectTimeout(1)
                .socketTimeout(1)
                .serverSelectionTimeout(1)
                .build();
        MongoWrapper wrapper = new MongoWrapper(new AbstractConfig(configMap), timeout);

        assertThat((List<?>)credentialsField.get(wrapper), hasSize(1));
        assertFalse(wrapper.checkConnection());

        thrown.expect(MongoException.class);
        try {
            wrapper.store("mytopic", new Document());
        } finally {
            wrapper.close();
        }
    }
}