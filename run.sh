#!/bin/sh

export COMPOSE_FILE_PATH="${PWD}/target/docker-compose/docker-compose.yml"
export COMPOSE_ADMIN_FILE_PATH="${PWD}/target/docker-compose/docker-compose-activiti-admin.yml"
export APS_VERSION=$(mvn help:evaluate -Dexpression=aps.version -q -DforceStdout)

if [ -z "${M2_HOME}" ]; then
  export MVN_EXEC="mvn"
else
  export MVN_EXEC="${M2_HOME}/bin/mvn"
fi

start() {
    docker volume create aps-db-volume
    docker volume create aps-contentstore-volume
	  docker volume create aps-es-volume
    docker compose -f "$COMPOSE_FILE_PATH" up --build -d --remove-orphans
}

start_activiti_app() {
    docker volume create aps-db-volume
    docker volume create aps-contentstore-volume
    docker volume create aps-es-volume
    docker compose -f "$COMPOSE_FILE_PATH" up --build -d aps-current-project --remove-orphans 
}

start_activiti_app_admin() {
    docker volume create aps-db-volume
    docker volume create aps-contentstore-volume
    docker volume create aps-es-volume
    docker compose -f "$COMPOSE_ADMIN_FILE_PATH" up --build -d aps-current-project --remove-orphans 
}

start_admin() {
    docker volume create aps-db-volume
    docker volume create aps-contentstore-volume
    docker volume create aps-es-volume
    docker compose -f "$COMPOSE_ADMIN_FILE_PATH" up --build -d --remove-orphans
}

down() {
    if [ -f "$COMPOSE_FILE_PATH" ]; then
        docker compose -f "$COMPOSE_FILE_PATH" down
    fi
}

down_admin() {
    if [ -f "$COMPOSE_ADMIN_FILE_PATH" ]; then
        docker compose -f "$COMPOSE_ADMIN_FILE_PATH" down
    fi
}

purge() {
    docker volume rm aps-db-volume
    docker volume rm aps-contentstore-volume
	docker volume rm aps-es-volume
}

purge_admin() {
    docker volume rm aps-db-volume
    docker volume rm aps-contentstore-volume
    docker volume rm aps-es-volume
}

build() {
    $MVN_EXEC clean package -Paps$APS_VERSION
}

build_admin() {
    $MVN_EXEC clean package -Paps$APS_VERSION,activiti-admin
}

build_activiti_app(){
  docker compose -f "$COMPOSE_FILE_PATH" kill aps-current-project
    yes | docker compose -f "$COMPOSE_FILE_PATH" rm -f aps-current-project
    $MVN_EXEC clean package -pl aps-extensions-jar,activiti-app-overlay-war,activiti-app-overlay-docker -Paps$APS_VERSION
}

build_activiti_app_admin(){
  docker compose -f "$COMPOSE_ADMIN_FILE_PATH" kill aps-current-project
    yes | docker compose -f "$COMPOSE_ADMIN_FILE_PATH" rm -f aps-current-project
    $MVN_EXEC clean package -pl aps-extensions-jar,activiti-app-overlay-war,activiti-app-overlay-docker -Paps$APS_VERSION,activiti-admin
}

tail() {
    docker compose -f "$COMPOSE_FILE_PATH" logs -f
}

tail_admin() {
    docker compose -f "$COMPOSE_ADMIN_FILE_PATH" logs -f
}

tail_all() {
    docker compose -f "$COMPOSE_FILE_PATH" logs --tail="all"
}

tail_all_admin() {
    docker compose -f "$COMPOSE_ADMIN_FILE_PATH" logs --tail="all"
}

prepare_test() {
    $MVN_EXEC verify -DskipTests=true -pl aps-extensions-jar,activiti-app-overlay-war,activiti-app-overlay-docker -Paps$APS_VERSION
}

prepare_test_admin() {
    $MVN_EXEC verify -DskipTests=true -pl aps-extensions-jar,activiti-app-overlay-war,activiti-app-overlay-docker -Paps$APS_VERSION,activiti-admin
}

test() {
    $MVN_EXEC verify -pl aps-extensions-jar
}

test_admin() {
    $MVN_EXEC verify -pl aps-extensions-jar -Pactiviti-admin
}

case "$1" in
  build_start)
    down
    build
    start
    tail
    ;;
  build_start_admin)
    down_admin
    build_admin
    start_admin
    tail_admin
    ;;
  build_start_it_supported)
    down
    build
    prepare_test
    start
    tail
    ;;
  build_start_it_supported_admin)
    down_admin
    build_admin
    prepare_test_admin
    start_admin
    tail_admin
    ;;
  build_test)
    down
    build
    prepare_test
    start
    test
    tail_all
    down
    ;;
  build_test_admin)
    down_admin
    build_admin
    prepare_test_admin
    start_admin
    test_admin
    tail_all_admin
    down_admin
    ;;
  start)
    start
    tail
    ;;
  start_admin)
    start_admin
    tail_admin
    ;;
  stop)
    down
    ;;
  stop_admin)
    down_admin
    ;;
  purge)
    down
    purge
    ;;
  purge_admin)
    down_admin
    purge_admin
    ;;
  tail)
    tail
    ;;
  tail_admin)
    tail_admin
    ;;
  reload_aps)
    build_activiti_app
    start
    tail
    ;;
  reload_aps_admin)
    build_activiti_app_admin
    start_admin
    tail_admin
    ;;
  *)
    echo "Usage for Activiti App only: $0 {build_start|build_start_it_supported|start|stop|purge|tail|reload_aps|build_test|test}"
    echo "Usage for Activiti App with Activiti Admin: $0 {build_start_admin|build_start_it_supported_admin|start_admin|stop_admin|purge_admin|tail_admin|reload_aps_admin|build_test_admin|test_admin}"
esac