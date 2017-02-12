#!/bin/bash
# Please run it from root project directory
./gradlew clean build checkstyle -PdisablePreDex -x :storio-sqlite-annotations-processor-test:test && ./gradlew :storio-sqlite-annotations-processor-test:testDebugUnitTest
