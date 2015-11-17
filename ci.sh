#!/bin/bash
# Please run it from root project directory
./gradlew clean build jacocoReport checkstyle -PdisablePreDex

# TODO REMOVE
cat storio-common/build/outputs/lint-results.xml