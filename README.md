# Alfresco Process Services SDK Project 2.1.5

The project consists of the following Maven submodules:

 * APS extensions JAR (`aps-extensions-jar`): put here your Java extensions
 * Activiti App Overlay WAR (`activiti-app-overlay-war`): generate activiti-app WAR overlay with APS Extensions JAR embedded
 * Activiti App Swagger Client (`activiti-app-swagger-client`): generate the APS Java Swagger client
 * Activiti App Overlay Docker (`activiti-app-overlay-docker`): put your overlayed WAR into the APS Docker container
 * Activiti App Integration Tests (`activiti-app-integration-tests`): integration tests based on the Java Swagger client

## Capabilities
 * Full support of Arm64 CPUs (Apple Silicon M1) with native Docker containers and a transparent Maven profile 
 * Running Docker will also create persistent volumes for each storage component (contentstore, db and ElasticSearch) for making the development approach in APS consistent and reliable

# Prerequisites
 * OpenJDK 11
 * Apache Maven 3.8.5
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

# Quickstart

Full Maven lifecycle command:

 * `mvn clean install docker:build docker:start`
 
Stop all the Docker containers with:
 
 * `mvn docker:stop`

Full Maven lifecycle command deploying also Activiti Admin:

 * `mvn clean install docker:build docker:start -Pactiviti-admin`
 
Stop all the Docker containers with:

 * `mvn docker:stop -Pactiviti-admin`


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

# Supported Maven Profiles for dependencies management and packaging (JAR and WAR)

In order to build the project, you can declare a Maven profile related to a specific APS version:
 * `aps2.3.1` (APS 2.3.1 - default)
 * `aps2.3.0` (APS 2.3.0)
 * `aps2.2.0.1` (APS 2.2.0.1)
 * `aps2.2.0` (APS 2.2.0)
 * `aps2.1.0` (APS 2.1.0)
 * `aps2.0.1` (APS 2.0.1)
 * `aps2.0.0` (APS 2.0.0)
 
Build and test with unit tests execution for APS 2.3.1 with:
`mvn clean test`

Build and test with unit tests execution for APS 2.3.0 with:
`mvn clean test -Paps2.3.0`

Build and test with unit tests execution for APS 2.2.0 with:
`mvn clean test -Paps2.2.0`

Build and test with unit tests execution for APS 2.1.0 with:
`mvn clean test -Paps2.1.0`

Build and test with unit tests execution for APS 2.0.1 with:
`mvn clean test -Paps2.0.1`

Build and package with integration tests execution for APS 2.3.0 with:
`mvn clean install`

Build and package with integration tests execution for APS 2.2.0 with:
`mvn clean install -Paps2.2.0`

Build and package with integration tests execution for APS 2.1.0 with:
`mvn clean install -Paps2.1.0`

Build and package with integration tests execution for APS 2.0.1 with:
`mvn clean install -Paps2.0.1`

Build your Docker container with:
`mvn docker:build`

Start your APS Docker containers with:
`mvn docker:start`

Build, execute embedded test runtime, create and start all the APS containers executing integration tests:
`mvn clean install docker:build docker:start`

After the integration tests execution stop all the APS containers with:
`mvn docker:stop`

# Building your Docker container (optional)
 * Update if you need  _logback.xml_  in `/activiti-app-overlay-war/src/main/webapp/WEB-INF/classes`
 * Update if you need your  _activiti-app.properties_  in `/activiti-app-overlay-docker/src/main/docker/properties`

# Few things to notice

 * You can use all the APS services such as: UserService, GroupService, TenantService and so on...
 * No parent pom
 * Standard JAR, WAR packaging with also Docker container generation
 * Works seamlessly with any IDE
 * Test your extensions with a consistent APS architecture running with Docker volumes

# Contributors
Thanks to Luca Stancapiano for testing and contributing on recent improvements. 

# Enterprise support
Official maintenance and support of this project is delivered by Zia Consulting
