#!/bin/sh

printf "APPLICATION_VERSION=%s" $(./gradlew :properties | sed -n -e 's/^.*version: //p') >> $GITHUB_ENV
