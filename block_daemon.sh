#!/bin/sh
while :
do
	day=$(date +%u)
	hour=$(date +%H)
	minute=$(date +%M)
	if [ "$( grep $day /etc/blocklist.conf | awk -F ' : ' '{print $2}')" = "all" ]
		then
			echo 'this is the part where you block'
		else 
			echo 'not blocking'
	fi 
	sleep 60
done
