#!/bin/sh
set -e

# shellcheck disable=SC1091
. ./release.env
# shellcheck disable=SC1091
. ./release-docker.env

mvn versions:set -DnewVersion="$MAVEN_JAR_VERSION"

imageCreatedDate=$(date --rfc-3339=ns)

sed -i 'deploy/docker/Dockerfile'  -e 's@ARG APP_VERSION=.*@ARG APP_VERSION='"$MAVEN_JAR_VERSION"'@g'
sed -i 'deploy/docker/Dockerfile'  -e 's@ARG JAR_NAME=.*@ARG JAR_NAME='"$JAR_NAME"'@g'
sed -i 'deploy/docker/Dockerfile'  -e 's@LABEL org.opencontainers.image.version=.*@LABEL org.opencontainers.image.version='"$DOCKER_VERSION"'@g'
sed -i 'deploy/docker/Dockerfile'  -e 's@LABEL org.opencontainers.image.revision=.*@LABEL org.opencontainers.image.revision='"$RELEASE_VERSION"'@g'
sed -i 'deploy/docker/Dockerfile'  -e 's@LABEL org.opencontainers.image.created=.*@LABEL org.opencontainers.image.created="'"$imageCreatedDate"'"@g'

sed -i "deploy/helm/$HELM_CHART_NAME/Chart.yaml" -e 's@appVersion:.*@appVersion: "'"$MAVEN_JAR_VERSION"'"@g'

sed -i "deploy/helm/$HELM_CHART_NAME/Chart.yaml" -e 's@version:.*@version: '"$HELM_CHART_VERSION"'@g'

sed -i "deploy/helm/$HELM_CHART_NAME/values.yaml" -e 's@    tag:.*@    tag: "'"$DOCKER_VERSION"'"@g'

# Update publish-to-helm-charts.yml
sed -i 'publish-to-helm-charts.yml' -e 's@query-[0-9.]*.tgz@query-'"$HELM_CHART_VERSION"'.tgz@g'
sed -i 'publish-to-helm-charts.yml' -e 's@docs/query/query-[0-9.]*.tgz@docs/query/query-'"$HELM_CHART_VERSION"'.tgz@g'
