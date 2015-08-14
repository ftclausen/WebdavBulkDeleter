# Introduction

This tool is intended for deleting folders from Webdav and its usage takes the
following form

* `--deletion-list` - a text file of one item per line to delete
* `--user` - Username authorised to delete the requested items
* `--password` - Password associated with the above user
* `--url` - A Webdav enabled URL

the primary usage for this tool is deleting orphaned course content in
Blackboard Learn.

# Running

You can use the utility by running the `webdavbulkdeleter.sh` script as
following replacing the example credentials and host name with your own

    ./webdavbulkdeleter.sh --deletion-list /tmp/test --user administrator --password 'secret' --url https://learn.example.com/bbcswebdav/courses

this script will build the tool if necessary and then execute.

