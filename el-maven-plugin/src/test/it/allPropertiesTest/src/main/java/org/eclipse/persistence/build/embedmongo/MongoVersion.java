/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.build.embedmongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.BsonDocument;
import org.bson.BsonString;

public class MongoVersion {

    public String getVersion(String mongoDbPort) {
        MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder();
        MongoClientSettings settings = settingsBuilder
                .applyConnectionString(new ConnectionString("mongodb://localhost:" + mongoDbPort))
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        String version = mongoClient.getDatabase("ecl_build_tools_test")
                .runCommand(new BsonDocument("buildinfo", new BsonString("")))
                .get("version")
                .toString();
        return version;
    }
}