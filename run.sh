#!/bin/bash


echo "Stopping containers..."
docker stop $(docker ps -q)

echo "Removing containers..."
docker rm $(docker ps -aq)

echo "Removing images..."
docker rmi -f 01blog-backend:latest 01blog-frontend:latest postgres:16 postgres:latest

echo "Done."

echo "🚀 Starting full stack with Docker Compose..."

docker compose up --build