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
package org.eclipse.persistence.build.packager.test;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.build.packager.model.TestEntity;

public class LibsTest extends JUnitTestCase {

    public LibsTest() {
        super();
    }

    public LibsTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("EclipseLink Packager Test Suite");
        suite.addTest(new LibsTest("testCreate"));
        return suite;
    }

    public void testCreate() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            TestEntity testEntity = new TestEntity(1, "abcde");
            em.persist(testEntity);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    public void testFind() {
        EntityManager em = createEntityManager();
        try {
            TestEntity testEntity = em.find(TestEntity.class, 1);
            assertEquals(1, testEntity.getId());
            assertEquals("abcde", testEntity.getName());
            commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }
}