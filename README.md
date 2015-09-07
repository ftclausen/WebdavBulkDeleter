# Introduction

This tool is intended for deleting folders from Webdav; primarily 
orphaned Blackboard Learn course content folders from the */bbcswebdav/courses*
location.

It requires the site be using a valid SSL certificate known to the 
Java JDK running this tool.

# Linux/Unix Quickstart

You can use the utility by running the `webdavbulkdeleter.sh` script as
following replacing the example credentials and host name with your own

    ./webdavbulkdeleter.sh --deletion-list /tmp/test --user administrator --password 'secret' --url https://learn.example.com/bbcswebdav/courses

this script will build the tool if necessary and then execute. Some more info
about the options :

* `--deletion-list` - a user supplied text file of one item per line to delete
* `--user` - Username authorised to delete the requested items
* `--password` - Password associated with the above user
* `--url` - A Webdav enabled URL

# Windows

There is not yet a quick launch wrapper script for windows so you'll have to 
build it manually then run the Jar file directly.

**All the commands below should happen from the base of the WebdavBuildDeleter
Git repo**

## Building

You can build the tool by running the Gradle wrapper manually as follows

`gradlew.bat shadowJar`

## Running

You can run the tool as follows

`java -jar build\libs\WebdavBulkDeleter-all.jar`


