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

import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.embed.process.config.DownloadConfig;
import de.flapdoodle.embed.process.io.ProcessOutput;
import de.flapdoodle.embed.process.net.HttpProxyFactory;
import de.flapdoodle.embed.process.net.ProxyFactory;
import de.flapdoodle.embed.process.transitions.DownloadPackage;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.transitions.Start;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.settings.Settings;

import java.net.URI;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.codehaus.plexus.util.StringUtils.contains;

/**
 * When invoked, this goal starts an instance of mongo. The required binaries
 * are downloaded if no mongo release is found in <code>~/.embedmongo</code>.
 *
 */
@Mojo(name="start-mongo", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST, threadSafe = false)
public class StartMojo extends AbstractEmbeddedMongoMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Mongod mongod = null;
        ProxyFactory proxyFactory = getProxyFactory(settings);
        if (proxyFactory == null) {
            mongod = Mongod.builder()
                    .net(Start.to(Net.class).initializedWith(Net.defaults()
                            .withPort(getPort())))
                    .processOutput(Start.to(ProcessOutput.class).initializedWith(getOutputConfig()))
                    .downloadPackage(DownloadPackage.withDefaults()
                            .withDownloadConfig(DownloadConfig.defaults()))
                    .build();
        } else {
            mongod = Mongod.builder()
                    .net(Start.to(Net.class).initializedWith(Net.defaults()
                            .withPort(getPort())))
                    .processOutput(Start.to(ProcessOutput.class).initializedWith(getOutputConfig()))
                    .downloadPackage(DownloadPackage.withDefaults()
                            .withDownloadConfig(DownloadConfig.defaults()
                                    .withProxyFactory(getProxyFactory(settings))))
                    .build();
        }

        TransitionWalker.ReachedState<RunningMongodProcess> running = mongod.start(getVersion());
        getPluginContext().put(MONGOD_CONTEXT_PROPERTY_NAME, running.current());
    }

    public ProxyFactory getProxyFactory(Settings settings) {
        URI downloadUri = URI.create(getDownloadPath());
        final String downloadHost = downloadUri.getHost();
        final String downloadProto = downloadUri.getScheme();

        if (settings.getProxies() != null) {
            for (org.apache.maven.settings.Proxy proxy : settings.getProxies()) {
                if (proxy.isActive()
                        && equalsIgnoreCase(proxy.getProtocol(), downloadProto)
                        && !contains(proxy.getNonProxyHosts(), downloadHost)) {
                    return new HttpProxyFactory(proxy.getHost(), proxy.getPort());
                }
            }
        }
        return null;
    }
}
