#!/bin/bash

# Run StockFlow with Docker Compose
echo "🚀 Starting StockFlow with Docker Compose..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker Desktop first."
    exit 1
fi

# Start services
docker-compose up -d

if [ $? -eq 0 ]; then
    echo "✅ Services started successfully!"
    echo ""
    echo "📊 Service URLs:"
    echo "   Application:  http://localhost:8080"
    echo "   SonarQube:    http://localhost:9000 (admin/admin)"
    echo "   Jenkins:      http://localhost:8081"
    echo "   PostgreSQL:   localhost:5432 (postgres/postgres)"
    echo ""
    echo "📋 Check logs:"
    echo "   docker-compose logs -f app"
    echo ""
    echo "🛑 Stop services:"
    echo "   docker-compose down"
else
    echo "❌ Failed to start services!"
    exit 1
fi
