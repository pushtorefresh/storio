#!/bin/bash
set -e

# You can run it from any directory.
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_DIR="$DIR"

pushd "$PROJECT_DIR"

# Pass "publish=true" as first argument to initiate release process.
SHOULD_PUBLISH_RELEASE="$1"

# For some reason test for annotation processor are failing on a regular CI setup.
# So we had to exclude test task for it from the main build process and execute it as a separate command.
./gradlew clean build checkstyle -PdisablePreDex --exclude-task :storio-sqlite-annotations-processor-test:test --exclude-task :storio-content-resolver-annotations-processor-test:test
./gradlew :storio-sqlite-annotations-processor-test:test
./gradlew :storio-content-resolver-annotations-processor-test:test

if [ "$SHOULD_PUBLISH_RELEASE" == "publish=true" ]; then
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
