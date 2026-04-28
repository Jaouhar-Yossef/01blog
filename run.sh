#!/bin/bash

set -e

echo "🧹 Stopping containers..."
docker ps -q | xargs -r docker stop

echo "🧹 Removing containers..."
docker ps -aq | xargs -r docker rm

echo "🚀 Starting full stack with Docker Compose..."
docker compose up --build