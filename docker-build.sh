#!/bin/bash

# Build and tag Docker image for StockFlow
echo "🚀 Building StockFlow Docker image..."

# Get version from pom.xml (or use default)
VERSION=${1:-latest}
IMAGE_NAME="stockflow"
FULL_IMAGE_NAME="${IMAGE_NAME}:${VERSION}"

# Build the image
docker build -t ${FULL_IMAGE_NAME} .

if [ $? -eq 0 ]; then
    echo "✅ Docker image built successfully: ${FULL_IMAGE_NAME}"
    
    # Tag as latest
    if [ "$VERSION" != "latest" ]; then
        docker tag ${FULL_IMAGE_NAME} ${IMAGE_NAME}:latest
        echo "✅ Also tagged as: ${IMAGE_NAME}:latest"
    fi
    
    # Show image details
    echo ""
    echo "📦 Image details:"
    docker images | grep ${IMAGE_NAME}
    
    echo ""
    echo "🎯 To run the container:"
    echo "   docker run -d -p 8080:8080 --name stockflow-app ${FULL_IMAGE_NAME}"
    echo ""
    echo "🎯 Or use docker-compose:"
    echo "   docker-compose up -d"
else
    echo "❌ Docker build failed!"
    exit 1
fi
