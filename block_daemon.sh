#!/bin/sh
while :
do
	day=$(date +%u)
	hour=$(date +%H)
	minute=$(date +%M)
	comandino=$(grep "^$day=" /etc/blocklist.conf | awk -F '=' '{print $2}')
	comandino2=$(echo "$comandino" | awk -F '-' '{print $1}')
	comandino3=$(echo "$comandino" | awk -F '-' '{print $2}')
	if [ "$comandino" = "all" ];then
		echo first if
		$(project -b > /dev/null)
	elif [ "$comandino2" -lt "$hour$minute" && "$comandino2" -gt "$hour$minute" ]; then
		echo second if
		$(project -b > /dev/null)
	else 
		echo else
		$(project -u > /dev/null)
	fi 
	sleep 10
done
