#!/bin/bash

set -ex
curl https://qlty.sh | bash

export PATH="$HOME/.qlty/bin:$PATH"

qlty coverage publish \
--format=jacoco target/site/jacoco/jacoco.xml \
--add-prefix src/main/java/
