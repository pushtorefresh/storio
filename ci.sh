#!/bin/bash
set -e

# Please run it from root project directory

# For some reason test for annotation processor are failing on a regular CI setup.
# So we had to exclude test task for it from the main build process and execute it as a separate command.
./gradlew clean build checkstyle -PdisablePreDex -x :storio-sqlite-annotations-processor-test:test -x :storio-content-resolver-annotations-processor-test:test
./gradlew :storio-sqlite-annotations-processor-test:testDebugUnitTest
./gradlew :storio-content-resolver-annotations-processor-test:testDebugUnitTest

if git describe --exact-match --tags $(git log -n1 --pretty='%h') ; then
    echo "Git tag detected, launching release process..."

    if [ -z "$GPG_SECRET_KEYS" ]; then
        echo "Put base64 encoded gpg secret key for signing into GPG_SECRET_KEYS env variable."
        exit 1
    fi

    if [ -z "$GPG_OWNERTRUST" ]; then
        echo "Put base64 encoded gpg ownertrust for signing into GPG_OWNERTRUST env variable."
        exit 1
    fi

    echo $GPG_SECRET_KEYS | base64 --decode | gpg --import;
    echo $GPG_OWNERTRUST | base64 --decode | gpg --import-ownertrust;

    ./gradlew uploadArchives closeAndReleaseRepository --info
fi
