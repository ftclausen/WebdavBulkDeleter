# Introduction

This tool is intended for deleting folders from Webdav; primarily 
orphaned Blackboard Learn course content folders from the */bbcswebdav/courses*
location.

It requires the site be using a valid SSL certificate known to the 
Java JDK running this tool.

# Running

You can use the utility by running the `webdavbulkdeleter.sh` script as
following replacing the example credentials and host name with your own

    ./webdavbulkdeleter.sh --deletion-list /tmp/test --user administrator --password 'secret' --url https://learn.example.com/bbcswebdav/courses

this script will build the tool if necessary and then execute. Some more info
about the options :

* `--deletion-list` - a user supplied text file of one item per line to delete
* `--user` - Username authorised to delete the requested items
* `--password` - Password associated with the above user
* `--url` - A Webdav enabled URL
