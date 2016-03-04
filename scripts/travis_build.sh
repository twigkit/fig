#!/bin/bash
set -ev
echo "debug requested by Travis CI support:"
echo TRAVIS_PULL_REQUEST = [ $TRAVIS_PULL_REQUEST ]
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	mvn clean install deploy -Dmaven.test.skip=true --quiet --settings settings.xml
else
	echo "building pull request and install has already run so verified tests pass, nothing more to do"
fi