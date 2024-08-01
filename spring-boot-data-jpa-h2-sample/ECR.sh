#!/bin/bash

# Set environment variables
export ECR_URL=296754545385.dkr.ecr.us-east-1.amazonaws.com
export ECR_REPOSITORY=cjrequena/spring-boot-data-jpa-h2-sample
export DOCKER_IMAGE_VERSION=latest
export IMAGE=${ECR_URL}/${ECR_REPOSITORY}:${DOCKER_IMAGE_VERSION}

# Authenticate Docker to the Amazon ECR registry
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ${ECR_URL}

# Build the Docker image
docker build -t ${IMAGE} .

# Push the Docker image to ECR
docker push ${IMAGE}
