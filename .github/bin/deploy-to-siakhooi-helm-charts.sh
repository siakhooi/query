#!/bin/sh
set -e

# shellcheck disable=SC1091
. ./release.env

PATH_TO_FILE=./"${HELM_CHART_NAME}-${HELM_CHART_VERSION}.tgz"
HELM_CHART_SOURCE_PATH=$(realpath "$PATH_TO_FILE")
HELM_CHART_PACKAGE_FILE=$(basename "$PATH_TO_FILE")

TMPDIR=$(mktemp -d)

TARGETPATH=docs/$HELM_CHART_NAME
TARGETURL=https://${PUBLISH_TO_GITHUB_REPO_TOKEN}@github.com/siakhooi/helm-charts.git
TARGETBRANCH=main
TARGETDIR=helm-charts-repo
TARGET_GIT_EMAIL=$PROJECT_NAME@siakhooi.github.io
TARGET_GIT_USERNAME=s$PROJECT_NAME
TARGET_COMMIT_MESSAGE="$PROJECT_NAME: Auto deploy [$(date)]"

(
  cd "$TMPDIR"
  git config --global user.email "$TARGET_GIT_EMAIL"
  git config --global user.name "$TARGET_GIT_USERNAME"

  git clone -n --depth=1 -b "$TARGETBRANCH" "$TARGETURL" "$TARGETDIR"
  cd "$TARGETDIR"
  git remote set-url origin "$TARGETURL"
  git restore --staged .
  mkdir -p "$TARGETPATH"
  cp -v -f "$HELM_CHART_SOURCE_PATH" "$TARGETPATH/$HELM_CHART_PACKAGE_FILE"
  git add "$TARGETPATH/$HELM_CHART_PACKAGE_FILE"
  git status
  git commit -m "$TARGET_COMMIT_MESSAGE"
  git push
)
find .
