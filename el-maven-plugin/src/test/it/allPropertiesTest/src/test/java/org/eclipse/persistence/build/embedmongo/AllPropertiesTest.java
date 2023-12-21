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

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AllPropertiesTest {

    //Simple test to verify that specified MongoDB version is up and available at the specified port
    @Test
    public void testVersion() throws Exception {
        assertEquals(System.getProperty("test.mongo.version"), new MongoVersion().getVersion(System.getProperty("test.mongo.port")));
    }

    @Test
    public void testLogFileExistence() throws Exception {
        File file = new File(System.getProperty("test.mongo.logFile"));
        assertTrue(file.isFile());
    }
}