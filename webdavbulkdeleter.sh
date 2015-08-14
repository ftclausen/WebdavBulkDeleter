#!/usr/bin/env bash

if [[ ! -x "../${PWD##*/}/gradlew" ]]; then
    echo "Please run me from the WebdavBulkDeleter Git repo"
    exit 1
fi

binary="build/libs/WebdavBulkDeleter-all.jar"
if [[ ! -f "$binary" ]]; then
    ./gradlew shadowJar
fi

java -Dlog4j.configuration=log4j.properties -jar "$binary" $@

