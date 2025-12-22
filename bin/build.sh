#!/bin/sh
set -e

# shellcheck disable=SC1091
. ./release.env

mvn verify
