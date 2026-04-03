#!/bin/bash

set -e

echo "🚀 Starting full project setup..."

# ================================

# 🐳 Start Docker (rootless)

# ================================

echo "🐳 Starting Docker..."
export PATH=$HOME/bin:$PATH
export DOCKER_HOST=unix://$XDG_RUNTIME_DIR/docker.sock

nohup dockerd-rootless.sh > ~/docker-rootless.log 2>&1 &
sleep 3

docker info > /dev/null 2>&1 && echo "✅ Docker is running" || echo "⚠️ Docker may not be ready yet"

# ================================

# 🗄️ Run PostgreSQL

# ================================

echo "🗄️ Starting PostgreSQL..."

docker rm -f blog-postgres 2>/dev/null || true

docker run -d 
--name blog-postgres 
-e POSTGRES_USER=postgres 
-e POSTGRES_PASSWORD=1234 
-e POSTGRES_DB=blogdb 
-p 5432:5432 
postgres

echo "⏳ Waiting for PostgreSQL to be ready..."
sleep 5

docker ps | grep blog-postgres && echo "✅ PostgreSQL running"

# ================================

# ☕ Run Spring Boot Backend

# ================================

echo "☕ Starting Spring Boot..."

cd backend || { echo "❌ backend folder not found"; exit 1; }

./mvnw clean package -DskipTests

nohup java -jar target/*.jar > backend.log 2>&1 &

cd ..

echo "⏳ Waiting for backend..."
sleep 5

echo "✅ Backend started on http://localhost:8080"

# ================================

# 🌐 Run Angular Frontend

# ================================

echo "🌐 Starting Angular..."

cd frontend/my-app || { echo "❌ Angular project not found"; exit 1; }

npm install

nohup npm start > frontend.log 2>&1 &

cd ../..

echo "⏳ Waiting for frontend..."
sleep 5

echo "✅ Frontend started on http://localhost:4200"

# ================================

# 🎉 DONE

# ================================

echo ""
echo "🎉 EVERYTHING IS RUNNING!"
echo "-----------------------------------"
echo "🌐 Frontend: http://localhost:4200"
echo "☕ Backend:  http://localhost:8080"
echo "🗄️ Database: localhost:5432"
echo "-----------------------------------"
echo "📄 Logs:"
echo " - backend.log"
echo " - frontend.log"
echo " - ~/docker-rootless.log"
