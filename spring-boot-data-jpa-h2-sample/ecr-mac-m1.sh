#!/bin/bash

## Set environment variables
export ECR_URL=296754545385.dkr.ecr.us-east-1.amazonaws.com
export ECR_REPOSITORY=cjrequena/spring-boot-data-jpa-h2-sample
export DOCKER_IMAGE_VERSION=latest
export IMAGE=${ECR_URL}/${ECR_REPOSITORY}:${DOCKER_IMAGE_VERSION}

# Authenticate Docker to the Amazon ECR registry
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ${ECR_URL}

# Create docker builder
docker buildx create --name aws-linux-builder

# Build the Docker image
docker buildx use aws-linux-builder
docker buildx build --platform linux/amd64,linux/arm64 -t ${IMAGE} .

# Push the Docker image to ECR
docker buildx build --push --platform linux/amd64,linux/arm64 -t ${IMAGE} .

# Delete docker builder
docker buildx create --name aws-linux-builder
