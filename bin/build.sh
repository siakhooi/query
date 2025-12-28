#!/bin/bash

set -euxo pipefail

# shellcheck disable=SC1091
. ./release.env

mvn -U verify 2>&1 | tee build.log
