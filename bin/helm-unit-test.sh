#!/bin/sh

# shellcheck disable=SC1091
. ./release.env

set -ex

readonly output_file="${PWD}/helm-unit-test.xml"

cd deploy/helm
helm unittest "$HELM_CHART_NAME" --output-file "$output_file"
