#!/bin/bash
# Please run it from root project directory
# For some reason test for annotation processor are failing on a regular CI setup.
# So we had to exclude test task for it from the main build process and execute it as a separate command.
./gradlew clean build checkstyle -PdisablePreDex -x :storio-sqlite-annotations-processor-test:test && ./gradlew :storio-sqlite-annotations-processor-test:testDebugUnitTest
