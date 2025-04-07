/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.build.packager.testcase;

import org.junit.Test;

import java.io.File;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.junit.Assert.assertTrue;

public class PackagerITCase {

    @Test
    public void testBasic() throws Exception {
        File projectRoot = new File(System.getProperty("it.projects.dir"));
        File basicTestProjectEar = new File(projectRoot, "packagerBasicTest/target/eclipselink-testbuild-plugin-packager-basic-test.ear.ear");
        JarFile earFile = new JarFile(basicTestProjectEar);
        //Check for mandatory libs
        assertTrue(isFileAvailable(earFile.entries(), "eclipselink-testbuild-plugin-packager-basic-test.ear_ejb.jar"));
        assertTrue(isFileAvailable(earFile.entries(), "^lib/junit.*.jar$"));
        assertTrue(isFileAvailable(earFile.entries(), "^lib/org.eclipse.persistence.core.test.framework.*.jar$"));
    }

    @Test
    public void testLibs() throws Exception {
        File projectRoot = new File(System.getProperty("it.projects.dir"));
        File basicTestProjectEar = new File(projectRoot, "packagerLibsTest/target/eclipselink-testbuild-plugin-packager-libs-test.ear.ear");
        JarFile earFile = new JarFile(basicTestProjectEar);
        //Check for mandatory libs
        assertTrue(isFileAvailable(earFile.entries(), "eclipselink-testbuild-plugin-packager-libs-test.ear_ejb.jar"));
        assertTrue(isFileAvailable(earFile.entries(), "^lib/junit.*.jar$"));
        assertTrue(isFileAvailable(earFile.entries(), "^lib/org.eclipse.persistence.core.test.framework.*.jar$"));
        //Check for additional libs
        assertTrue(isFileAvailable(earFile.entries(), "^lib/aspectjrt.*.jar$"));
        assertTrue(isFileAvailable(earFile.entries(), "^lib/aspectjweaver.*.jar$"));
    }

    private boolean isFileAvailable(Enumeration<JarEntry> earContent, String fileNamePattern) {
        while (earContent.hasMoreElements()) {
            JarEntry jarEntry = earContent.nextElement();
            if (jarEntry.getName().matches(fileNamePattern)) {
                return true;
            }
        }
        return false;
    }
}
