#!/bin/bash
set -e

# You can run it from any directory.
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_DIR="$DIR"

pushd "$PROJECT_DIR"

# For some reason test for annotation processor are failing on a regular CI setup.
# So we had to exclude test task for it from the main build process and execute it as a separate command.
./gradlew clean build checkstyle -PdisablePreDex -x :storio-sqlite-annotations-processor-test:test -x :storio-content-resolver-annotations-processor-test:test
./gradlew :storio-sqlite-annotations-processor-test:testDebugUnitTest
./gradlew :storio-content-resolver-annotations-processor-test:testDebugUnitTest

# Export "PUBLISH_RELEASE=true" to initiate release process.
if [ "$PUBLISH_RELEASE" == "true" ]; then
    echo "Launching release publishing process..."

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
