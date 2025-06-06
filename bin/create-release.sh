#!/bin/sh
set -e

# shellcheck disable=SC1091
. ./release.env

RELEASE_NOTE="$RELEASE_TITLE
Version: helm $HELM_CHART_VERSION docker $DOCKER_VERSION jar $MAVEN_JAR_VERSION"

gh release create "$RELEASE_VERSION" --title "$RELEASE_TITLE" --notes "${RELEASE_NOTE}" --latest
