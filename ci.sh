#!/bin/bash
set -e

# You can run it from any directory.
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_DIR="$DIR"

pushd "$PROJECT_DIR"

# Export "PUBLISH_RELEASE=true" to initiate release process.
if [ "$PUBLISH_RELEASE" != "true" ]; then
    echo "Running non-release build...".

    # For some reason test for annotation processor are failing on a regular CI setup.
    # So we had to exclude test task for it from the main build process and execute it as a separate command.
    ./gradlew clean build checkstyle -PdisablePreDex --exclude-task :storio-sqlite-annotations-processor-test:test --exclude-task :storio-content-resolver-annotations-processor-test:test
    ./gradlew :storio-sqlite-annotations-processor-test:test
    ./gradlew :storio-content-resolver-annotations-processor-test:test
else
    echo "Launching release publishing process..."

    if [ -z "$GPG_SECRET_KEYS" ]; then
        echo "Put base64 encoded gpg secret key for signing into GPG_SECRET_KEYS env variable."
        exit 1
    fi

    if [ -z "$GPG_OWNERTRUST" ]; then
        echo "Put base64 encoded gpg ownertrust for signing into GPG_OWNERTRUST env variable."
        exit 1
    fi

    if [ -z "$GPG_KEY_ID" ]; then
        echo "Put GPG key id into GPG_KEY_ID env variable."
        exit 1
    fi

    if [ -z "$GPG_PASSPHRASE" ]; then
        echo "Put GPG passphrase into GPG_PASSPHRASE env variable."
        exit 1
    fi

    set +e
    echo "$GPG_SECRET_KEYS" | base64 --decode | gpg --import
    gpg_import_result=$?
    set -e

    # Code '2' means that keys were already in local keychain.
    if [ "$gpg_import_result" == "0" ] || [ "$gpg_import_result" == "2" ]; then
        echo "GPG keys imported successfully."
    else
        echo "Failed to import GPG keys."
        exit 1
    fi

    echo 'Importing GPG ownertrust...'
    echo $GPG_OWNERTRUST | base64 --decode | gpg --import-ownertrust;
    echo 'GPG ownertrust imported successfully.'

    ./gradlew clean uploadArchives closeAndReleaseRepository -Psigning.keyId="$GPG_KEY_ID" -Psigning.password="$GPG_PASSPHRASE" -Psigning.secretKeyRingFile="$HOME/.gnupg/secring.gpg"
fi
