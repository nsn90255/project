#!/bin/sh
while :
do
	# time in hour and  minutes of last block
	lastBlockHour=$(tail -1 /var/log/blockdaemon.log | awk -F 'T' '{print $2}' | awk -F ':' '{print $1}')
	lastBlockMinute=$(tail -1 /var/log/blockdaemon.log | awk -F 'T' '{print $2}' | awk -F ':' '{print $2}')
	# get day of the week in numerical form
	day=$(date +%u)
	# get hour and minute from the system
	hour=$(date +%H)
	minute=$(date +%M)
	hourminute="$hour$minute"
	# get blocking ranges from /etc/blocklist
	extract_after_day=$(grep "^$day=" /etc/blocklist.conf | awk -F '=' '{print $2}')
	first_time=$(echo "$extract_after_day" | awk -F '-' '{print $1}')
	second_time=$(echo "$extract_after_day" | awk -F '-' '{print $2}')
	third_time=$(echo "$extract_after_day" | awk -F '-' '{print $3}')
	fourth_time=$(echo "$extract_after_day" | awk -F '-' '{print $4}')
	# make check for empty log at some point
	# check log to see if it should ignore (works for the same day for now)
	if [ "$(echo "$hour - $lastBlockHour" | bc)" = 0 ];then
		minuteDifference="$(echo "$minute - $lastBlockMinute" | bc)"
		echo minute $minute
		echo lastBlockMinute $lastBlockMinute
		echo minuteDifference $minuteDifference
	else
		echo not so sure
	fi
	# check the config at /etc/blocklist.conf to see if it's okay to block
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
