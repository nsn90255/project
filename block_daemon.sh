#!/bin/sh
while :
do
	day=$(date +%u)
	hour=$(date +%H)
	minute=$(date +%M)
	extract_after_day=$(grep "^$day=" /etc/blocklist.conf | awk -F '=' '{print $2}')
	first_time=$(echo "$extract_after_day" | awk -F '-' '{print $1}')
	second_time=$(echo "$extract_after_day" | awk -F '-' '{print $2}')
	if [ "$extract_after_day" = "all" ];then
		echo first if
		$(project -b > /dev/null)
	elif [ "$first_time" -le "$hour$minute" ] && [ "$second_time" -ge "$hour$minute" ]; then
		echo second if
		$(project -b > /dev/null)
	else 
		echo else
		$(project -u > /dev/null)
	fi 
	sleep 10
done
