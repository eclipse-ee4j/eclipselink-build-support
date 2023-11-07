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

import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * When invoked, this goal stop an instance of mongo.
 *
 */
@Mojo(name="stop-mongo", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class StopMojo extends AbstractEmbeddedMongoMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        RunningMongodProcess runningMongodProcess = (RunningMongodProcess)(getPluginContext().get(StartMojo.MONGOD_CONTEXT_PROPERTY_NAME));
        if (runningMongodProcess != null) {
            runningMongodProcess.stop();
        } else {
            throw new MojoFailureException("No mongod process found, it appears embedmongo:start was not called");
        }
    }

}
