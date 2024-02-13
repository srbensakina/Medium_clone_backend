#!/bin/bash

declare dc_app="Medium Clone Backend/docker-compose.yml"

function build_api() {
    cd 'Medium Clone Backend'
    ./mvnw clean package -DskipTests
    cd ..
}

function start() {
    build_api
    echo "Starting all docker containers...."
    docker compose -f "${dc_app}" up --build -d
    docker compose -f "${dc_app}" logs -f
}

function stop() {
    build_api
    echo "Stopping all docker containers...."
    docker compose -f "${dc_app}" stop
    docker compose -f "${dc_app}" rm -f
}

function restart() {
    stop
    sleep 3
    start
}

action="start"

if [[ "$#" != "0"  ]]
then
    action=$@
fi

eval ${action}
