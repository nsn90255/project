#!/bin/sh
day=$(date +%u)
hour=$(date +%H)
minute=$(date +%M)
if [ "$( grep monday /etc/blocklist.conf | awk -F ' : ' '{print $2}')" = "all" ]
then
	echo 'blocking all monday'
else 
	echo 'not blocking all monday'
fi 
