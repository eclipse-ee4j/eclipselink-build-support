# eclipselink-build-support

## eclipselink-testbuild-plugin

### goal - package-testapp
A maven plugin goal to package eclipselink test application(s) for the deployment on the application
server. It produces EJB jar and/or EAR application from the project structure with the content
as is outlined bellow and attaches built artifacts to the project.

mode (property: `el.packager.mode`):
* EAR (default)
  * `org.eclipse.persistence.core.test.framework` included by default
  * `junit` included by default
  * created ejb jar is placed under the root
  * `member_X` dependencies are placed under the root
  * resources from `${earConf}` (default: `${project.basedir}/src/main/resources-ear`)
  * classifier: ear
* EJB
  * content of `org.eclipse.persistence.jpa.test.framework` is expanded under the root. By default,
    if there is one persistence unit defined in the persistence descriptor, only `TestRunnerXBeans`
    are excluded, if there are none or more persistence units defined, `SingleUnitTestRunnerBean`
    is excluded as well. Exclusion can also be controlled by `el.packager.fwk.exclusionFilter` property.
  * `model` (classifier)/`test-jar` (type) dependencies are expanded under the root
  * model from classes (exclude `*.jar`, `META-INF/persistence.xml`, `META-INF/sessions.xml`)
  * tests from testClasses
  * default project resources excluding `persistence.xml`
  * resources from `${ejbConf}` (default: `${project.basedir}/src/main/resources-ejb`)
  * if `persistence.xml` and/or `ejb-jar.xml` descriptors are not found under `${ejbConf}` directory,
    they are generated from the project's default `persistence.xml` found under project's default resources;
    `el.packager.descriptors` property can be set to `false` to explicitly disable generation of these descriptors
  * classifier: ejb

#### Usage
```xml
<plugin>
    <groupId>org.eclipse.persistence</groupId>
    <artifactId>eclipselink-testbuild-plugin</artifactId>
    <executions>
        <execution>
            <id>package-server-tests</id>
            <goals>
                <goal>package-testapp</goal>
            </goals>
            <phase>package</phase>
            <configuration>
                <mode>EAR</mode>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### goals - start-mongo, stop-mongo
Download, start, stop specified version of embedded MongoDB.
Main purpose is use it in testing environment to start MongoDB before tests and stop after tests (integration testing in Maven environment).
This plugin was inspired by https://github.com/joelittlejohn/embedmongo-maven-plugin project which doesn't seem to be active now.
But number of plugin configuration parameters is limited per requirements of the EclipseLink project.
This plugin is wrapper for the [flapdoodle.de embedded MongoDB API](http://github.com/flapdoodle-oss/embedmongo.flapdoodle.de).

Minimal Java version is 11

```xml
<plugin>
    <groupId>org.eclipse.persistence</groupId>
    <artifactId>eclipselink-testbuild-plugin</artifactId>
    <executions>
        <execution>
            <id>start</id>
            <phase>process-test-classes</phase>
            <goals>
                <goal>start-mongo</goal>
            </goals>
            <configuration>
                <port>37017</port>
                <!-- database port, optional, default 27017 -->
              
                <version>6.0.8</version>
                <!-- database version which will be downloaded or used if downloaded before, optional, default 7.0.0 -->
              
                <features>STORAGE_ENGINE</features>
                <!-- flapdoodle.embed.mongo features, for example to build Windows download URLs, optional, default is none -->
              
                <logging>file</logging>
                <!-- Logging output. Possible values are (file|console|none), optional, default console -->
              
                <logFile>target/mongoTest.log</logFile>
                <!-- If logging output is `file`, path relative or absolute where log file will be created, optional, default `embedmongo.log` -->
              
                <logFileEncoding>utf-8</logFileEncoding>
                <!-- If logging output is `file`, log file encoding, optional, default `utf-8` -->

                <downloadPath>http://fastdl.mongodb.org/</downloadPath>
                <!-- The base URL to be used when downloading MongoDB. This is first part of the URL. Second part is evaluated dynamically (based on current OS and specified version), optional, default is `http://fastdl.mongodb.org/` -->
                
                <skip>false</skip>
                <!-- skip plugin execution, optional, default `false` -->
            </configuration>
        </execution>
        <execution>
            <id>stop</id>
            <phase>prepare-package</phase>
            <goals>
                <goal>stop-mongo</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```