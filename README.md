# Alfresco Process Services JAR Module - SDK

Current dependencies are configured for APS 1.9.0.5.

Folder structure is based on the same APS project classpath:
 * /src/main/resources/activiti: APS configuration files
 * /src/main/resources/app: contains your own APS Applications extracted
 * /src/test/java: put here your unit and integration tests
 
To run use 'mvn clean install'

 * Runs the embedded container + H2 DB 
 * Runs unit and integration tests
 * Packages both App ZIP and JAR with customization

Supported Maven Profiles for dependencies management

 * aps1.9  (APS 1.9.0.5 - default)
 * aps1.10 (APS 1.10.0) 

# Prerequisites

 * JDK 1.8
 * Apache Maven 3.x
 * Access to the Alfresco Maven Enterprise Repository
 * A valid APS license installed in your local system

# Few things to notice

 * You can use all the APS services such as: UserService, GroupService, TenantService and so on...
 * No parent pom
 * Standard JAR packaging and layout
 * Works seamlessly with Eclipse and IntelliJ IDEA
 
