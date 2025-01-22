#!/bin/sh
while :
do
	ignore=$(grep ignore /etc/blocklist.conf | awk -F '=' '{print $2}') 
	if [ "$ignore" = "true" ]; then
		sleep 60
	fi
	day=$(date +%u)
	hourminute=$(date +%H%M)
	extract_after_day=$(grep "^$day=" /etc/blocklist.conf | awk -F '=' '{print $2}')
	first_time=$(echo "$extract_after_day" | awk -F '-' '{print $1}')
	second_time=$(echo "$extract_after_day" | awk -F '-' '{print $2}')
	third_time=$(echo "$extract_after_day" | awk -F '-' '{print $3}')
	fourth_time=$(echo "$extract_after_day" | awk -F '-' '{print $4}')
	if [ "$extract_after_day" = "all" ];then
		echo first if
		$(project -b > /dev/null)
	elif [ "$first_time" -le "$hourminute" ] && [ "$second_time" -ge "$hourminute" ]; then
		echo second if
		$(project -b > /dev/null)
	elif [ "$third_time" -le "$hourminute" ] && [ "$fourth_time" -ge "$hourminute" ]; then
		echo third if
		$(project -b > /dev/null)
	else 
		echo else
		$(project -u > /dev/null)
	fi 
	sleep 10
done
