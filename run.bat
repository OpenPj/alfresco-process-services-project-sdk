@ECHO OFF

SET COMPOSE_FILE_PATH=%CD%\target\docker-compose\docker-compose.yml
SET COMPOSE_ADMIN_FILE_PATH=%CD%\target\docker-compose\docker-compose-activiti-admin.yml

for /f %%i in ('call mvn help:evaluate -Dexpression=aps.version -q -DforceStdout') do SET APS_VERSION=%%i


IF [%M2_HOME%]==[] (
    SET MVN_EXEC=mvn
)

IF NOT [%M2_HOME%]==[] (
    SET MVN_EXEC=%M2_HOME%\bin\mvn
)

IF [%1]==[] (
    echo "Usage for Activiti App only: %0 {build_start|build_start_it_supported|start|stop|purge|tail|reload_aps|build_test|test}"
    echo "Usage for Activiti App with Activiti Admin: %0 {build_start_admin|build_start_it_supported_admin|start_admin|stop_admin|purge_admin|tail_admin|reload_aps_admin|build_test_admin|test_admin}"

    GOTO END
)

IF %1==build_start (
    CALL :down
    CALL :build
    CALL :start
    CALL :tail
    GOTO END
)
IF %1==build_start_admin (
    CALL :down_admin
    CALL :build_admin
    CALL :start_admin
    CALL :tail_admin
    GOTO END
)
IF %1==build_start_it_supported (
    CALL :down
    CALL :build
    CALL :prepare-test
    CALL :start
    CALL :tail
    GOTO END
)
IF %1==build_start_it_supported_admin (
    CALL :down_admin
    CALL :build_admin
    CALL :prepare-test_admin
    CALL :start_admin
    CALL :tail_admin
    GOTO END
)
IF %1==start (
    CALL :start
    CALL :tail
    GOTO END
)
IF %1==start_admin (
    CALL :start_admin
    CALL :tail_admin
    GOTO END
)
IF %1==stop (
    CALL :down
    GOTO END
)
IF %1==stop_admin (
    CALL :down_admin
    GOTO END
)
IF %1==purge (
    CALL:down
    CALL:purge
    GOTO END
)
IF %1==purge_admin (
    CALL:down_admin
    CALL:purge_admin
    GOTO END
)
IF %1==tail (
    CALL :tail
    GOTO END
)

IF %1==reload_aps (
    CALL :build_activiti_app
    CALL :start
    CALL :tail
    GOTO END
)
IF %1==reload_aps_admin (
    CALL :build_activiti_app_admin
    CALL :start_admin
    CALL :tail_admin
    GOTO END
)
IF %1==build_test (
    CALL :down
    CALL :build
    CALL :prepare-test
    CALL :start
    CALL :test
    CALL :tail_all
    CALL :down
    GOTO END
)
IF %1==build_test_admin (
    CALL :down_admin
    CALL :build_admin
    CALL :prepare-test_admin
    CALL :start_admin
    CALL :test_admin
    CALL :tail_all_admin
    CALL :down_admin
    GOTO END
)
IF %1==test (
    CALL :test
    GOTO END
)
IF %1==test_admin (
    CALL :test_admin
    GOTO END
)
echo "Usage for Activiti App only: %0 {build_start|build_start_it_supported|start|stop|purge|tail|reload_aps|build_test|test}"
    echo "Usage for Activiti App with Activiti Admin: %0 {build_start_admin|build_start_it_supported_admin|start_admin|stop_admin|purge_admin|tail_admin|reload_aps_admin|build_test_admin|test_admin}"

:END
EXIT /B %ERRORLEVEL%

:start
    docker volume create aps-db-volume
    docker volume create aps-contentstore-volume
    docker volume create aps-es-volume
    docker-compose -f "%COMPOSE_FILE_PATH%" up --build -d --remove-orphans
EXIT /B 0
:start_admin
    docker volume create aps-db-volume
    docker volume create aps-contentstore-volume
    docker volume create aps-es-volume
    docker-compose -f "%COMPOSE_ADMIN_FILE_PATH%" up --build -d --remove-orphans
EXIT /B 0
:start_activiti_app
    docker volume create aps-db-volume
    docker volume create aps-contentstore-volume
    docker volume create aps-es-volume
    docker-compose -f "%COMPOSE_FILE_PATH%" up --build -d aps-current-project --remove-orphans
EXIT /B 0
:start_activiti_app_admin
    docker volume create aps-db-volume
    docker volume create aps-contentstore-volume
    docker volume create aps-es-volume
    docker-compose -f "%COMPOSE_ADMIN_FILE_PATH%" up --build -d aps-current-project --remove-orphans
EXIT /B 0
:down
    if exist "%COMPOSE_FILE_PATH%" (
        docker-compose -f "%COMPOSE_FILE_PATH%" down
    )
EXIT /B 0
:down_admin
    if exist "%COMPOSE_ADMIN_FILE_PATH%" (
        docker-compose -f "%COMPOSE_ADMIN_FILE_PATH%" down
    )
EXIT /B 0
:build
    call %MVN_EXEC% clean package -Paps%APS_VERSION%
EXIT /B 0
:build_admin
    call %MVN_EXEC% clean package -Paps%APS_VERSION%,activiti-admin
EXIT /B 0
:build_activiti_app
    docker-compose -f "%COMPOSE_FILE_PATH%" kill aps-current-project
    docker-compose -f "%COMPOSE_FILE_PATH%" rm -f aps-current-project
    call %MVN_EXEC% clean package -pl aps-extensions-jar,activiti-app-overlay-war,activiti-app-overlay-docker -Paps%APS_VERSION%
EXIT /B 0
:build_activiti_app_admin
    docker-compose -f "%COMPOSE_ADMIN_FILE_PATH%" kill aps-current-project
    docker-compose -f "%COMPOSE_ADMIN_FILE_PATH%" rm -f aps-current-project
    call %MVN_EXEC% clean package -pl aps-extensions-jar,activiti-app-overlay-war,activiti-app-overlay-docker -Paps%APS_VERSION%,activiti-admin
EXIT /B 0
:tail
    docker-compose -f "%COMPOSE_FILE_PATH%" logs -f
EXIT /B 0
:tail_admin
    docker-compose -f "%COMPOSE_ADMIN_FILE_PATH%" logs -f
EXIT /B 0
:tail_all
    docker-compose -f "%COMPOSE_FILE_PATH%" logs --tail="all"
EXIT /B 0
:tail_all_admin
    docker-compose -f "%COMPOSE_ADMIN_FILE_PATH%" logs --tail="all"
EXIT /B 0
:prepare-test
    call %MVN_EXEC% verify -DskipTests=true -pl aps-extensions-jar,activiti-app-overlay-war,activiti-app-overlay-docker -Paps%APS_VERSION%
EXIT /B 0
:prepare-test_admin
    call %MVN_EXEC% verify -DskipTests=true -pl aps-extensions-jar,activiti-app-overlay-war,activiti-app-overlay-docker -Paps%APS_VERSION%,activiti-admin
EXIT /B 0
:test
    call %MVN_EXEC% verify -pl aps-extensions-jar,activiti-app-overlay-war,activiti-app-overlay-docker -Paps%APS_VERSION%
EXIT /B 0
:test_admin
    call %MVN_EXEC% verify -pl aps-extensions-jar,activiti-app-overlay-war,activiti-app-overlay-docker -Paps%APS_VERSION%,activiti-admin
EXIT /B 0
:purge
    docker volume rm aps-db-volume
    docker volume rm aps-contentstore-volume
    docker volume rm aps-es-volume
EXIT /B 0
:purge_admin
    docker volume rm aps-db-volume
    docker volume rm aps-contentstore-volume
    docker volume rm aps-es-volume
EXIT /B 0