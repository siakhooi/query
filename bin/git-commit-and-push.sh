#!/bin/sh
set -e

# shellcheck disable=SC1091
. ./release.env

git commit -m "$RELEASE_TITLE"

git push
