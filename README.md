# Alfresco Process Services SDK Project

The project consists of the following Maven submodules:

 * APS extensions JAR (`aps-extensions-jar`): put here your Java extensions
 * Activiti App Overlay WAR (`activiti-app-overlay-war`): it will generate activiti-app WAR overlay with APS Extensions JAR embedded
 * Activiti App Overlay Docker (`activiti-app-overlay-docker`): it will put your overlayed WAR into the APS Docker container
 
Running Docker will also create volumes for each storage component (contentstore, db and ElasticSearch) for making the development approach in APS consistent and reliable.

# APS Extensions JAR Module

Folder structure is based on the same APS project classpath:
 * `/aps-extensions-jar/src/main/resources/activiti`: APS configuration files
 * `/aps-extensions-jar/src/main/resources/apps`: contains your own APS applications extracted
 * `/aps-extensions-jar/src/test/java`: put here your unit and integration tests
 
To run use `mvn clean install`

 * Runs the embedded container + H2 DB 
 * Runs unit tests with `mvn clean test`
 * Runs integration tests with `mvn clean integration-test`
 * Packaging of App ZIP, JAR and WAR with extensions
 
# SDK Packages - Runtime - Cross platform
 * `com.activiti.extension.api`: put here your enterprise custom REST endpoint (authentication required)
 * `com.activiti.extension.rest`: put here your internal API (with no authentication)
 * `com.activiti.extension.bean`: put here your Spring beans (services and components)
 
*Suggested naming conventions for implementations dedicated to a specific app*

 * `com.activiti.extension.*your_app*.listeners`: put here your listeners
 * `com.acitivit.extension.*your_app*.service.tasks`: put here your service tasks

# SDK Packages - Test Runtime

 * `org.alfresco.activiti.unit.tests`: put here your unit test (with suffix *Test.java)
 * `org.alfresco.activiti.integration.tests`: put here your integration tests (with suffix *IT.java)

# Activiti App WAR Overlay Module

This module is responsible for generating the final WAR artifact including all the extensions implemented in the APS extensions JAR Module.

# Activiti App Overlay Docker Module

For building the Docker container with your custom Activiti App WAR:

`mvn docker:build`

 * Packaging of Activiti App and Activiti Admin Docker containers with extensions
 
`mvn docker:start`

Start your Activiti App Docker container with the following architecture:

  * Activiti Admin (with H2 embedded)
  * PostgreSQL 10.9
  * ElasticSearch
  * aps-db-volume: Docker volume for PostgreSQL
  * aps-es-volume: Docker volume for ElasticSearch
  * aps-contentstore-volume: Docker volume for attachments
  
  * Stop your Docker container:

`mvn docker:stop`

  * Purge all the Docker volumes:
  
`mvn clean -Ppurge-volumes`

# Supported Maven Profiles for dependencies management and packaging (JAR and WAR)

In order to build you have to define a Maven profile for choosing the version of APS:
 * `aps2.0.1`  (APS 2.0.1 - default)
 * `aps1.11.4` (APS 1.11.4)
 * `aps1.11` (APS 1.11.0)
 * `aps1.10` (APS 1.10.0)
 * `aps1.9`  (APS 1.9.0.5)
 
Build and test with unit tests execution for APS 2.0.1 with:
`mvn clean test`

Build and test with unit tests execution for APS 1.11.4 with:
`mvn clean test -Paps.1.11.4`

Build and test with unit tests execution for APS 1.11 with:
`mvn clean test -Paps.1.11`

Build and test with unit tests execution for APS 1.10 with:
`mvn clean test -Paps1.10`

Build and test with unit tests execution for APS 1.9.0.5 with:
`mvn clean test -Paps1.9`

Build and package with integration tests execution for APS 2.0.1 with:
`mvn clean install`

Build and package with integration tests execution for APS 1.11.4 with:
`mvn clean install -Paps1.11.4`

Build and package with integration tests execution for APS 1.11 with:
`mvn clean install -Paps1.11`

Build and package with integration tests execution for APS 1.10 with:
`mvn clean install -Paps1.10`

Build and package with integration tests execution for APS 1.9.0.5 with:
`mvn clean install -Paps1.9`

Build your Docker container with:
`mvn docker:build`

Start your APS Docker containers with:
`mvn docker:start`

Stop all the APS Docker containers with:
`mvn docker:stop`

Build, test, create and start all the APS containers with:
`mvn clean install docker:build docker:start`

# Prerequisites
 * OpenJDK 11.0.5
 * Apache Maven 3.6.3
 * Docker (optional)
 * A valid APS license installed in your development environment `<USER_HOME>/.activiti/license` (for testing purpose)
 * A valid Aspose license installed in your development environment `<USER_HOME>/.activiti/license` (for testing purpose)
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

# Building your Docker container (optional)
 * Put a valid APS and Aspose license in `/activiti-app-overlay-docker/src/main/docker/license`
 * Put your logback.xml in `/activiti-app-overlay-war/src/main/webapp/WEB-INF/classes`
 * Put your activiti-app.properties in `/activiti-app-overlay-docker/src/main/docker/properties`

# Few things to notice

 * You can use all the APS services such as: UserService, GroupService, TenantService and so on...
 * No parent pom
 * Standard JAR, WAR packaging with also Docker container generation
 * Works seamlessly with any IDE
 * Test your extensions with a consistent APS architecture running with Docker volumes

# Contributors
Thanks to Carlo Cavallieri and Jessica Foroni for giving help on isolating the integration tests suite. 
