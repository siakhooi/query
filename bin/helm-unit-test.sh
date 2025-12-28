#!/bin/bash

set -euxo pipefail

# shellcheck disable=SC1091
. ./release.env

readonly output_file="${PWD}/helm-unit-test.xml"

(
	cd deploy/helm
	helm unittest "$HELM_CHART_NAME" --output-file "$output_file"
) 2>&1 | tee helm-unit-test.log
