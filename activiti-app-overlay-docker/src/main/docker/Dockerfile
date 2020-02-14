FROM alfresco/process-services:${aps.docker.version}

ARG TOMCAT_DIR=/usr/local/tomcat

USER root

RUN rm -rf $TOMCAT_DIR/webapps/activiti-app

#COPY extensions/aps-extensions-jar-${project.version}.jar $TOMCAT_DIR/webapps/activiti-app/WEB-INF/lib

COPY extensions/activiti-app.war $TOMCAT_DIR/webapps

#COPY license/*.* $TOMCAT_DIR/lib