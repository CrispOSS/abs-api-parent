#!/bin/bash

# Make sure no pending change is on the working directory
modified_count=`git status --porcelain | wc -l`
if [[ "${modified_count}" -ne "0" ]]; then
   echo "Your working directoy contains uncommitted changes."
   exit 1
fi

# Make sure no pull can be made before release
LOCAL_ORIGIN=$(git rev-parse @)
REMOTE_ORIGIN=$(git rev-parse @{u})
if [[ "${LOCAL_ORIGIN}" != "${REMOTE_ORIGIN}" ]]; then
   echo "Your local branch is not synchronized with its remote branch."
   exit 1
fi

# How to use the script
function usage {
   echo "Release using Apache Maven"
   echo ""
   echo "Usage:"
   echo "   release.sh REL_VERSION DEV_VERSION GPG_KEYNAME GPG_PASS"
   echo ""
   echo "      REL_VERSION    The version to use for the release"
   echo "      DEV_VERSION    The version to use for the development"
   echo "      GPG_KEYNAME    The GPG key name"
   echo "      GPG_PASS       The GPG passphrase"
   echo ""
}

if [[ $# -lt "3" ]]; then
   usage
   exit 1
fi

REL_VERSION="$1"
DEV_VERSION="$2"
GPG_PASS="$3"
# GPG keyname for 'nobeh'
GPG_KEYNAME="${4:-2C3A8E57}"
REL_TAG="v${REL_VERSION}"

mvn clean
mvn -Prelease release:prepare -DreleaseVersion="${REL_VERSION}" -DdevelopmentVersion="${DEV_VERSION}" -Dtag=${REL_TAG} -Darguments="-Dgpg.passphrase=${GPG_PASS} -Dgpg.keyname=${GPG_KEYNAME}"
mvn -Prelease release:perform -DreleaseVersion="${REL_VERSION}" -DdevelopmentVersion="${DEV_VERSION}" -Dtag=${REL_TAG} -Darguments="-Dgpg.passphrase=${GPG_PASS} -Dgpg.keyname=${GPG_KEYNAME}"
mvn -Prelease deploy -Dgpg.passphrase=${GPG_PASS} -Dgpg.keyname=${GPG_KEYNAME}

echo "Released version:       ${REL_VERSION}"
echo "Release tag:            ${REL_TAG}"
echo "Development version:    ${DEV_VERSION}"
