# Alfresco Process Services SDK Project

The project consists of the following Maven submodules:

 * APS Extensions JAR (`aps-extensions-jar`): put here your Java extensions
 * Activiti App Overlay WAR (`activiti-app-overlay-war`): it will generate the activiti-app WAR overlay with APS Extensions JAR embedded

# APS Extensions JAR Module

Folder structure is based on the same APS project classpath:
 * `/aps-extensions-jar/src/main/resources/activiti`: APS configuration files
 * `/aps-extensions-jar/src/main/resources/apps`: contains your own APS applications extracted
 * `/aps-extensions-jar/src/test/java`: put here your unit and integration tests
 
To run use 'mvn clean install'

 * Runs the embedded container + H2 DB 
 * Runs unit tests with `mvn clean test`
 * Runs integration tests with `mvn clean integration-test`
 * Packages both App ZIP, JAR and WAR with extensions
 
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

# Activiti App Overlay Module

This module is responsible for generating the final WAR artifact including all the extensions implemented in the APS Extensions JAR Module.

# Supported Maven Profiles for dependencies management and packaging (JAR and WAR)

In order to build you have to define a Maven profile for choosing the version of APS:
 * `aps1.9`  (APS 1.9.0.5 - default)
 * `aps1.10` (APS 1.10.0)
 
Build and test with unit tests execution for APS 1.9.0.5 with:
`mvn clean test` or `mvn clean test -Paps1.9`

Build and test with unit tests execution for APS 1.10 with:
`mvn clean test -Paps1.10`

Build and package with integration tests execution for APS 1.9.0.5 with:
`mvn clean install` or `mvn clean install -Paps1.9`

Build and package with integration tests execution for APS 1.10 with:
`mvn clean install -Paps1.10`

# Prerequisites

 * OpenJDK 11.0.5
 * Apache Maven 3.6.3
 * Access to the Alfresco Nexus Repository (Maven settings.xml with credentials provided by Alfresco)
 * A valid APS license installed in your development environment

# Few things to notice

 * You can use all the APS services such as: UserService, GroupService, TenantService and so on...
 * No parent pom
 * Standard JAR and WAR packaging and layout
 * Works seamlessly with any IDE

# Contributors
Thanks to Carlo Cavallieri and Jessica Foroni for giving help on isolating the integration tests suite. 
