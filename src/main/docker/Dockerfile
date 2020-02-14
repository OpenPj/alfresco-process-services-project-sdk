FROM alfresco/process-services:1.10.0

ARG TOMCAT_DIR=/usr/local/tomcat

USER root

# Copy Dockerfile to avoid an error if no JARs exist
COPY extensions/aps-extensions-jar-1.2.0.jar $TOMCAT_DIR/webapps/activiti-app/WEB-INF/lib

# Copy Dockerfile to avoid an error if no license file exists
COPY license/*.* $TOMCAT_DIR/lib