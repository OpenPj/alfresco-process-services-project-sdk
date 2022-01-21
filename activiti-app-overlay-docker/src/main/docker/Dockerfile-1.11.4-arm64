FROM tomcat:8.5.75-jdk11-openjdk

ENV ACTIVITI_DATASOURCE_USERNAME: ${docker.aps.database.username}
ENV ACTIVITI_DATASOURCE_PASSWORD: ${docker.aps.database.password}
ENV ACTIVITI_DATASOURCE_DRIVER: ${docker.aps.database.driver}
ENV ACTIVITI_HIBERNATE_DIALECT: ${docker.aps.database.dialect}
ENV ACTIVITI_DATASOURCE_URL: ${docker.aps.database.url}
ENV ACTIVITI_CSRF_DISABLED: ${docker.aps.database.csrf.disabled}
ENV ACTIVITI_CORS_ENABLED: ${docker.aps.database.cors.enabled}
ENV ACTIVITI_ES_SERVER_TYPE: ${docker.aps.es.server.type}
ENV ACTIVITI_ES_DISCOVERY_HOSTS: ${docker.aps.es.discovery.host}
ENV ACTIVITI_ES_CLUSTER_NAME: ${docker.aps.es.cluster.name}

ARG TOMCAT_DIR=/usr/local/tomcat

USER root

RUN rm -rf $TOMCAT_DIR/webapps/activiti-app

COPY extensions/aps-extensions-jar-${project.version}.jar $TOMCAT_DIR/lib

COPY logging/logback.xml $TOMCAT_DIR/lib

COPY properties/activiti-app.properties $TOMCAT_DIR/lib

COPY extensions/activiti-app.war $TOMCAT_DIR/webapps

COPY license/*.* $TOMCAT_DIR/lib/