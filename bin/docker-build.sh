#!/bin/bash

set -euxo pipefail

# shellcheck disable=SC1091
. ./release.env

(
	cp target/"${JAR_NAME}-${MAVEN_JAR_VERSION}.jar" .

	docker build . -f deploy/docker/Dockerfile \
		-t "$DOCKER_IMAGE_NAME:latest" \
		-t "$DOCKER_IMAGE_NAME:$DOCKER_VERSION"
) 2>&1 | tee docker-build.log
