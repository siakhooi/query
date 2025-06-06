#!/bin/sh
set -e

# shellcheck disable=SC1091
. ./release.env

cp target/"${JAR_NAME}-${MAVEN_JAR_VERSION}.jar" .

docker build . -f deploy/docker/Dockerfile \
  -t "$DOCKER_IMAGE_NAME:latest" \
  -t "$DOCKER_IMAGE_NAME:$DOCKER_VERSION"
