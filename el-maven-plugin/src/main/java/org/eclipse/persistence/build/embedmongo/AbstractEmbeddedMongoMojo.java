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

import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.distribution.Versions;
import de.flapdoodle.embed.mongo.packageresolver.Feature;
import de.flapdoodle.embed.process.io.ProcessOutput;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.eclipse.persistence.build.embedmongo.log.Loggers;

import java.util.stream.Stream;

public abstract class AbstractEmbeddedMongoMojo extends AbstractMojo {

    private static final String PACKAGE_NAME = StartMojo.class.getPackage().getName();
    public static final String MONGOD_CONTEXT_PROPERTY_NAME = PACKAGE_NAME + ".mongod";

    @Parameter(property = "el.embedmongo.skip", defaultValue = "false")
    private boolean skip;

    /**
     * The port MongoDB should run on.
     */
    @Parameter(property = "el.embedmongo.port", defaultValue = "27017")
    private int port;

    /**
     * The version of MongoDB to run e.g. 7.0.0, 2.1.1. Not any version is allowed, but only versions available in the <code>de.flapdoodle.embed.mongo.distribution.Version</code>
     */
    @Parameter(property = "el.embedmongo.version", defaultValue = "7.0.0")
    private String version;

    /**
     * The flapdoodle features required for download e.g. sync_delay,no_http_interface_arg
     */
    @Parameter(property = "el.embedmongo.features")
    private String features;

    /**
     * Logging output. Should be "console" DEFAULT or "file" or "none".
     */
    @Parameter(property = "el.embedmongo.logging", defaultValue = "console")
    private String logging;

    /**
     * Log file name.
     */
    @Parameter(property = "el.embedmongo.logFile", defaultValue = "embedmongo.log")
    private String logFile;

    /**
     * Logging file encoding.
     */
    @Parameter(property = "el.embedmongo.logFileEncoding", defaultValue = "utf-8")
    private String logFileEncoding;

    /**
     * The base URL to be used when downloading MongoDB. This is first part of the URL. Second part is evaluated dynamically (based on current OS and specified version).
     */
    @Parameter(property = "el.embedmongo.downloadPath", defaultValue = "http://fastdl.mongodb.org/")
    private String downloadPath;

    @Parameter( defaultValue = "${settings}", readonly = true )
    protected Settings settings;


    @Parameter( defaultValue = "${project}", readonly = true )
    protected MavenProject project;

    @Override
    public abstract void execute() throws MojoExecutionException, MojoFailureException;

    protected void onSkip() {
        // Nothing to do, this is just to allow do things if mojo is skipped
    }

    public String getFeatures() {
        return features;
    }

    protected IFeatureAwareVersion getVersion() {
        String versionEnumName = this.version.toUpperCase().replaceAll("\\.", "_");

        if (versionEnumName.charAt(0) != 'V') {
            versionEnumName = "V" + versionEnumName;
        }


        Feature[] features = new Feature[0];
        if (this.features != null) {
            try {
                features = Stream.of(this.features.split(",")).map(String::trim).map(String::toUpperCase).map(Feature::valueOf).toArray(Feature[]::new);
            } catch (IllegalArgumentException e) {
                getLog().warn("Unrecognised feature '" + this.features + ". Attempting download anyway...");
            }
        }

        try {
            return Versions.withFeatures(Version.valueOf(versionEnumName));
        } catch (IllegalArgumentException e) {
            getLog().warn("Unrecognised MongoDB version '" + this.version + "', this might be a new version that we don't yet know about. Attempting download anyway...");
            return Versions.withFeatures(() -> version);
        }
    }

    protected Integer getPort() {
        String portStr = project.getProperties().getProperty("el.embedmongo.port");

        if(StringUtils.isNotBlank(portStr)){
            return Integer.valueOf(portStr);
        }else{
            return port;
        }
    }

    public boolean isSkip() {
        return skip;
    }

    public MavenProject getProject() {
        return project;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    protected ProcessOutput getOutputConfig() throws MojoFailureException {

        Loggers.LoggingOutput loggingOutput = Loggers.LoggingOutput.valueOf(logging.toUpperCase());

        switch (loggingOutput) {
            case CONSOLE:
                return Loggers.console();
            case FILE:
                return Loggers.file(logFile, logFileEncoding);
            case NONE:
                return Loggers.none();
            default:
                throw new MojoFailureException("Unexpected logging output specified: \"" + logging + "\" -> " + loggingOutput);
        }
    }
}
