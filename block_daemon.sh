#!/bin/sh
while :
do
	day=$(date +%u)
	hour=$(date +%H)
	minute=$(date +%M)
	if [ "$( grep $day /etc/blocklist.conf | awk -F ' : ' '{print $2}')" = "all" ]
		then
			$(project -b > /dev/null)
		else 
			$(project -u > /dev/null)
	fi 
	sleep 60
done
