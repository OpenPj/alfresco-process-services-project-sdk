# Alfresco Process Services SDK Project

| APS SDK Version  | Supported APS versions | Artifact | Upgrade steps | License Management |
| ------------- | ------------- | ------------- | ------------- | ------------- |
| [APS SDK 3.x (3.x branch)](https://github.com/OpenPj/alfresco-process-services-project-sdk/tree/3.x)  | 24.4.2, 24.4.1, 24.4.0, 24.3.0, 24.2.1, 24.2, 24.1 | [Download APS SDK v3.0.5](https://github.com/OpenPj/alfresco-process-services-project-sdk/releases/tag/v3.0.5) | Recreate the APS SDK project from scratch and import your extensions from APS SDK 2.x (or 1.x) | Native support |
| [APS SDK 2.x (2.x branch)](https://github.com/OpenPj/alfresco-process-services-project-sdk/tree/2.x)  | 2.4.5, 2.4.4, 2.4.3, 2.4.2.15, 2.4.2.14, 2.4.2.13, 2.4.2.12, 2.4.2.11, 2.4.2.10, 2.4.2.9, 2.4.2.8, 2.4.2.7, 2.4.2.6, 2.4.2.5, 2.4.2.4, 2.4.2.3, 2.4.2.2, 2.4.2.1, 2.4.2, 2.4.1.2, 2.4.1.1, 2.4.1, 2.4.0, 2.3.10, 2.3.9, 2.3.8.7, 2.3.8.6, 2.3.8.5, 2.3.8.4, 2.3.8.3, 2.3.8.2, 2.3.8.1, 2.3.8, 2.3.7.2, 2.3.7.1, 2.3.7, 2.3.6.2, 2.3.6.1, 2.3.6, 2.3.5.1, 2.3.5, 2.3.4, 2.3.3, 2.3.2, 2.3.1, 2.3.0, 2.2.0.1, 2.2.0, 2.1.0, 2.0.1, 2.0.0  | [Download APS SDK v2.6.2](https://github.com/OpenPj/alfresco-process-services-project-sdk/releases/tag/v2.6.2) | [Upgrading APS SDK 2.x project to support APS 2.2.0 (only for APS SDK versions earlier than 2.0.8 and until to APS 2.2.0)](https://github.com/OpenPj/alfresco-process-services-project-sdk/wiki/Upgrading-APS-SDK-2.x-project-to-support-APS-2.2.0-(only-for-APS-SDK-versions-earlier-than-2.0.8)) | [Adding the License Management to your current project (only for APS SDK versions earlier than 2.0.9)](https://github.com/OpenPj/alfresco-process-services-project-sdk/wiki/Adding-the-License-Management-to-your-current-project-(only-for-APS-SDK-versions-earlier-than-2.0.9-or-1.7.4)) | 
| [APS SDK 1.x (master branch)](https://github.com/OpenPj/alfresco-process-services-project-sdk)  | 1.11.5, 1.11.4.3, 1.11.4.2, 1.11.4.1, 1.11.4, 1.11.3, 1.11.2, 1.11.1.1, 1.11.1, 1.11.0, 1.10.0.1, 1.10.0, 1.9.0.5 | [Download APS SDK v1.8.0](https://github.com/OpenPj/alfresco-process-services-project-sdk/releases/tag/v.1.8.0) | | [Adding the License Management to your current project (only for APS SDK versions earlier than 1.7.4)](https://github.com/OpenPj/alfresco-process-services-project-sdk/wiki/Adding-the-License-Management-to-your-current-project-(only-for-APS-SDK-versions-earlier-than-2.0.9-or-1.7.4))|

# Alfresco Process Services SDK Project 1.8.0

The project consists of the following Maven submodules:

 * APS extensions JAR (`aps-extensions-jar`): put here your Java extensions
 * Activiti App Overlay WAR (`activiti-app-overlay-war`): it will generate activiti-app WAR overlay with APS Extensions JAR embedded
 * Activiti App Swagger Client (`activiti-app-swagger-client`): generate the APS Java Swagger client
 * Activiti App Overlay Docker (`activiti-app-overlay-docker`): it will put your overlayed WAR into the APS Docker container
 * Activiti App Integration Tests (`activiti-app-integration-tests`): integration tests based on the Java Swagger client

## Capabilities
 * Full support of Arm64 CPUs (Apple Silicon M1) with native Docker containers and a transparent Maven profile 
 * Running Docker will also create persistent volumes for each storage component (contentstore, db and ElasticSearch) for making the development approach in APS consistent and reliable

# Prerequisites
 * OpenJDK 11
 * Apache Maven 3.9.0
 * Docker (optional)
 * Put valid  _activiti.lic_  and  _Aspose.Total.Java.lic_  in the `/license` folder for running unit / integration tests and for building containers
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
	<server>
	  <id>alfresco-artifacts-repository</id>
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

Another good practice could be creating a separated Maven module with a `pom.xml` totally dedicatd to build your container for production release.

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

Build and deploy with Docker the Activiti Admin App with the activiti-admin Maven profile:

`mvn docker:build -Pactiviti-admin`

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

# Activiti App Integration Tests Module
This module includes tests for interacting with the APS Docker using the generated Swagger client.

Put your Java test classes in the following package:
`/activiti-app-integration-tests/src/test/java`

# Supported Maven Profiles for dependencies management and packaging (JAR and WAR)

In order to build you have to define a Maven profile for choosing the version of APS:
 * `aps1.11.5` (APS 1.11.5 - default)
 * `aps1.11.4.3` (APS 1.11.4.3)
 * `aps1.11.4.2` (APS 1.11.4.2)
 * `aps1.11.4.1` (APS 1.11.4.1)
 * `aps1.11.4` (APS 1.11.4)
 * `aps1.11.3` (APS 1.11.3)
 * `aps1.11.2` (APS 1.11.2)
 * `aps1.11.1.1` (APS 1.11.1.1)
 * `aps1.11.1` (APS 1.11.1)
 * `aps1.11.0` (APS 1.11.0)
 * `aps1.10.0.1` (APS 1.10.0.1)
 * `aps1.10.0` (APS 1.10.0)
 * `aps1.9.0.5`  (APS 1.9.0.5)
 
Build and test with unit tests execution for APS 1.11.5 with:
`mvn clean test`

Build and test with unit tests execution for APS 1.11.0 with:
`mvn clean test -Paps.1.11.0`

Build and test with unit tests execution for APS 1.10.0 with:
`mvn clean test -Paps1.10.0`

Build and test with unit tests execution for APS 1.9.0.5 with:
`mvn clean test -Paps1.9.0.5`

Build and package with integration tests execution for APS 1.11.5 with:
`mvn clean install`

Build and package with integration tests execution for APS 1.11 with:
`mvn clean install -Paps1.11.0`

Build and package with integration tests execution for APS 1.10 with:
`mvn clean install -Paps1.10.0`

Build and package with integration tests execution for APS 1.9.0.5 with:
`mvn clean install -Paps1.9.0.5`

Build your Docker container with:
`mvn docker:build`

Start your APS Docker containers with:
`mvn docker:start`

Stop all the APS Docker containers with:
`mvn docker:stop`

Build, test, create and start all the APS containers with:
`mvn clean install docker:build docker:start`

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
Thanks to Luca Stancapiano, Carlo Cavallieri and Jessica Foroni for giving help on isolating the embedded integration tests suite. 

# Enterprise support
Official maintenance and support of this project is delivered by Zia Consulting
