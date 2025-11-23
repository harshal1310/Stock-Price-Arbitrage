#!/bin/bash

APP_NAME="arbitrage-app"
DB_CONTAINER="pg-colima"
DB_PORT=5432

echo " Starting Colima..."
colima start

echo " Checking if Postgres container exists..."
if [ "$(docker ps -aq -f name=$DB_CONTAINER)" ]; then
    echo " Postgres container already exists"
    
    if [ ! "$(docker ps -q -f name=$DB_CONTAINER)" ]; then
        echo " Starting existing Postgres container..."
        docker start $DB_CONTAINER
    else
        echo " Postgres is already running"
    fi
else
    echo " Creating new Postgres container..."
    docker run --name $DB_CONTAINER \
      -e POSTGRES_USER=postgres \
      -e POSTGRES_PASSWORD=postgres \
      -e POSTGRES_DB=arbitrage \
      -p 5432:5432 \
      -d postgres:16
fi

echo " Waiting for Postgres to be ready..."
until docker exec $DB_CONTAINER pg_isready -U postgres > /dev/null 2>&1
do
    printf "."
    sleep 1
done

echo ""
echo " Postgres is ready!"

echo " Starting Spring Boot app..."
./gradlew bootRun
