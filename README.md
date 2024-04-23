# Alfresco Process Services SDK Project 3.0.0

The project consists of the following Maven submodules:

 * APS extensions JAR (`aps-extensions-jar`): put here your Java extensions
 * Activiti App Overlay WAR (`activiti-app-overlay-war`): generate activiti-app WAR overlay with APS Extensions JAR embedded
 * Activiti App Overlay Docker (`activiti-app-overlay-docker`): put your overlayed WAR into the APS Docker container
 * Activiti App Integration Tests (`activiti-app-integration-tests`): integration tests based on the Java Swagger client

## Capabilities
 * Full support of Arm64 CPUs (Apple Silicon M1) with native Docker containers and a transparent Maven profile 
 * Running Docker will also create persistent volumes for each storage component (contentstore, db and ElasticSearch) for making the development approach in APS consistent and reliable
 * Two different quickstarts: run scripts or using Full Maven lifecycle

# Prerequisites
 * OpenJDK 17
 * Apache Maven 3.9.6
 * Docker (optional)
 * Put valid  _activiti.lic_  and  _transform.lic_  (or  _Aspose.Total.Java.lic_  )  in the `/license` folder for running unit / integration tests and for building containers 
 * Access to the Alfresco Nexus Repositories (credentials provided by Alfresco)
 * Configure your Maven servers settings.xml with credentials for these repositories:
 
 ``` 
    <server>
	  <id>activiti-enterprise-releases</id>
	  <username>yourAlfrescoUsername</username>
	  <password>yourAlfrescoPassword</password>
	</server>
	<server>
	  <id>enterprise-releases</id>
	  <username>yourAlfrescoUsername</username>
	  <password>yourAlfrescoPassword</password>
	</server>
	<server>
	  <id>internal-thirdparty</id>
	  <username>yourAlfrescoUsername</username>
	  <password>yourAlfrescoPassword</password>
	</server>
  ```
  
# Quickstart using run scripts

Run scripts are provided in order to make easier and faster APS platform development. 
In order to use both these generated Docker Compose templates you can use the following run scripts:
 
 * `run.sh`: for Linux and Mac OS
 * `run.bat`: for Windows (Beta version)
 
 The APS version is automatically taken from `pom.xml`, you only need to set the APS version enabling the active profile of a specific version.
 Run scripts support the following commands:
 
 * Usage for Activiti App only: `./run.sh (.bat) {build_start|build_start_it_supported|start|stop|purge|tail|reload_aps|build_test|test}`
 * Usage for Activiti App with Activiti Admin: `./run.sh (.bat) {build_start_admin|build_start_it_supported_admin|start_admin|stop_admin|purge_admin|tail_admin|reload_aps_admin|build_test_admin|test_admin}`

For building, packaging and deploying APS containers:

 * `./run.sh build_start`
 
Stop all the Docker containers with:
 
 * `./run.sh stop`

For building, packaging and deploying APS containers including Activiti Admin app:

 * `./run.sh build_start_admin`
 
Stop all the Docker containers with:

 * `./run.sh stop_admin`
 
## Docker Compose templates
Docker compose templates are dynamically generated from the Maven poms for each build in the `target/docker-compose` folder:

 * `target/docker-compose/docker-compose.yml`: deploying Activiti App, Postgres and Elasticsearch
 * `target/docker-compose/docker-compose-activiti-admin`: deploying Activiti Admin, Activiti App, Postgres and Elasticsearch

# Quickstart using Full Maven Lifecycle

Full Maven lifecycle command:

 * `mvn clean install docker:build docker:start`
 
Stop all the Docker containers with:
 
 * `mvn docker:stop`

Full Maven lifecycle command building and deploying also the Activiti Admin application, this is needed at least for the first build run:

 * `mvn clean install docker:build docker:start -Pactiviti-admin`
 
Stop all the Docker containers with:

 * `mvn docker:stop -Pactiviti-admin`
 
Full Maven lifecycle command deploying also the Activiti Admin app without rebuilding its container, very useful after a first build run, for making the entire build faster:

 * `mvn clean install docker:build docker:start -Pactiviti-admin,skip.admin`
 
 Stop all the Docker containers with:

 * `mvn docker:stop -Pactiviti-admin`

## Remote Debugging

A debug port is available on port `5005` from the `aps-sdk/alfresco-process-services` container including the custom `activiti-app.war`.

Remember to eventually disable remote debugging for your production release commenting `CATALINA_OPTS` and `port` elements in the `/activiti-app-overlay-docker/pom.xml` as shown in the following snippets:

```xml
<run>
	<env>
		<!-- <CATALINA_OPTS>${catalina.opts.debug}</CATALINA_OPTS> -->
	</env>
	....
	<ports>
		<port>${docker.tomcat.port.external}:${docker.tomcat.port.internal}</port>
		<!-- <port>${aps.debug.port}:${aps.debug.port}</port> -->
	</ports>					
```

Another good practice could be creating a separated Maven module with a `pom.xml` totally dedicated to build your container for production.

## Changing the default extensions deployment method
By default the APS SDK is deploying extensions as a unique JAR embedded in the `activiti-app.war`.
If you want to deploy your extensions in the `tomcat/lib` folder check out the following steps:

 * Remove or comment the `aps-extensions-jar` dependency in the `/activiti-app-overlay-war/pom.xml`
 * Uncomment the `COPY` command of the related Dockerfile from the folder: `/activiti-app-overlay-docker/src/main/docker`

# APS Extensions JAR Module

Folder structure is based on the same APS project classpath:
 * `/aps-extensions-jar/src/main/resources/activiti`: APS configuration files
 * `/aps-extensions-jar/src/main/resources/apps`: contains your own APS applications extracted
 * `/aps-extensions-jar/src/test/java`: put here your unit and integration tests
 
To run use `mvn clean install -DskipITs`

 * Runs the embedded container + H2 DB 
 * Runs unit tests with `mvn clean test`
 * Packaging of App ZIP, JAR and WAR with extensions
 
# SDK Packages - Runtime - Cross platform
 * `com.activiti.extension.api`: put here your enterprise custom REST endpoint (authentication required)
 * `com.activiti.extension.rest`: put here your internal API (with no authentication)
 * `com.activiti.extension.bean`: put here your Spring beans (services and components)
 
*Suggested naming conventions for implementations dedicated to a specific app*

 * `com.activiti.extension.*your_app*.listeners`: put here your listeners
 * `com.acitivit.extension.*your_app*.service.tasks`: put here your service tasks

# SDK Packages - Embedded Test Runtime

 * `org.alfresco.activiti.unit.tests`: put here your unit test (with suffix *Test.java)

# Activiti App WAR Overlay Module

This module is responsible for generating the final WAR artifact including all the extensions implemented in the APS extensions JAR Module.

# Activiti App Overlay Docker Module

For building the Docker container with your custom Activiti App WAR:

`mvn docker:build`

For building the Docker container adding also your custom Activiti Admin WAR use the activiti-admin Maven profile:

`mvn docker:build -Pactiviti-admin`

Packaging of Activiti App Docker containers with extensions
 
`mvn docker:build docker:start`

Start your Activiti App Docker container with the following architecture:

  * PostgreSQL
  * ElasticSearch
  * aps-db-volume: Docker volume for PostgreSQL
  * aps-es-volume: Docker volume for ElasticSearch
  * aps-contentstore-volume: Docker volume for attachments
  
If you want to build and start also the Activiti Admin WAR container:

`mvn docker:build docker:start -Pactiviti-admin`

  
  * Stop your Docker container:

`mvn docker:stop`

  * Purge all the Docker volumes:
  
`mvn clean -Ppurge-volumes`

# Activiti App Integration Tests Module
This module includes tests for interacting with the APS Docker using the generated Swagger client.

Put your Java test classes in the following package:
`/activiti-app-integration-tests/src/test/java`

# Supported Maven Profiles for dependencies management and packaging (JAR, WAR and Docker containers)

In order to build the project, you can declare a Maven profile related to a specific APS version:

 * `aps24.2.0` (APS 24.2.0 - default)
 * `aps24.1.0` (APS 24.1.0)
 

Build and test with unit tests execution for APS 24.2 with:
`mvn clean test`

Build and test with unit tests execution for APS 2.3.1 with:
`mvn clean test -Paps24.1.0`

Build your Docker container with:
`mvn docker:build`

Start your APS Docker containers with:
`mvn docker:start`

Build, execute embedded test runtime, create and start all the APS containers executing integration tests:
`mvn clean install docker:build docker:start`

After the integration tests execution stop all the APS containers with:
`mvn docker:stop`

Skip the build of the Activiti Admin container with:
`mvn clean install docker:build docker:start -Pactiviti-admin,skip.admin`

# Building your Docker container (optional)
 * Update if you need  _log4j2-dev.properties_ and _log4j2.properties_ in `/activiti-app-overlay-war/src/main/webapp/WEB-INF/classes`
 * Update if you need your  _activiti-app.properties_  in `/activiti-app-overlay-docker/src/main/docker/properties`

# Few things to notice

 * You can use all the APS services such as: UserService, GroupService, TenantService and so on...
 * No parent pom
 * Standard JAR, WAR packaging with also Docker container generation
 * Works seamlessly with any IDE
 * Test your extensions with a consistent APS architecture running with Docker volumes

# Contributors
 * Jeff Potts: updated the documentation
 * Luca Stancapiano: testing and suggested new features
 * Bindu Wavell: testing and created tools for introducing new capabilities
 * Stanley Arnold: Fixed the Maven configuration

# Enterprise support
Official maintenance and support of this project is delivered by Zia Consulting
