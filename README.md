# Alfresco Process Services JAR Module - SDK

Current dependencies are configured for APS 1.9.0.5.

Folder structure is based on the same APS project classpath:
 * /src/main/resources/activiti: APS configuration files
 * /src/main/resources/apps: contains your own APS applications extracted
 * /src/test/java: put here your unit and integration tests
 
To run use 'mvn clean install'

 * Runs the embedded container + H2 DB 
 * Runs unit tests with 'mvn clean test'
 * Runs integration tests with 'mvn clean integration-test'
 * Packages both App ZIP and JAR with customization
 
 
# SDK Packages - Runtime - Cross platform
 * com.activiti.extension.api: put here your enterprise custom REST endpoint (authentication required)
 * com.activiti.extension.rest: punt here your internal API (with no authentication)
 * com.activiti.extension.bean: put here your Spring beans (services and components)
 
*Suggested naming conventions for implementations dedicated to a specific app*

 * com.activiti.extension.<yourApp>.listeners: put here your listeners
 * com.acitivit.extension.<yourApp>.service.tasks: put here your service tasks

# SDK Packages - Test Runtime
 * org.alfresco.activiti.unit.tests: put here your unit test (with suffix *Test.java)
 * org.alfresco.activiti.integration.tests: put here your integration tests (with suffix *IT.java)

#Supported Maven Profiles for dependencies management

 * aps1.9  (APS 1.9.0.5 - default)
 * aps1.10 (APS 1.10.0) 

# Prerequisites

 * OpenJDK 11
 * Apache Maven 3.6.x
 * Access to the Alfresco Maven Enterprise Repository
 * A valid APS license installed in your development environment

# Few things to notice

 * You can use all the APS services such as: UserService, GroupService, TenantService and so on...
 * No parent pom
 * Standard JAR packaging and layout
 * Works seamlessly with any IDE

# Contributors
Thanks to Carlo Cavallieri and Jessica Foroni for giving help on isolating the integration tests suite. 
