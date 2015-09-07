#!/usr/bin/env bash

if [[ ! -x "../${PWD##*/}/gradlew" ]]; then
    echo "Please run me from the WebdavBulkDeleter Git repo"
    exit 1
fi

type -p java &>/dev/null
if (( $? != 0 )); then
    echo "No \"java\" command in path - please add the java JRE or JDK \"bin\" directory to your path variable"
    exit 1
fi

binary="build/libs/WebdavBulkDeleter-all.jar"
if [[ ! -f "$binary" ]]; then
    ./gradlew shadowJar
fi

java -Dlog4j.configuration=log4j.properties -jar "$binary" $@

