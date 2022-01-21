FROM tomcat:8.5.75-jdk8-openjdk

ARG TOMCAT_DIR=/usr/local/tomcat

USER root

COPY extensions/activiti-admin.war $TOMCAT_DIR/webapps
COPY properties/activiti-admin.properties $TOMCAT_DIR/lib
COPY tomcat/conf/server.xml $TOMCAT_DIR/conf