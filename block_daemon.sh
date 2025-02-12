#!/bin/sh
while :
do
	# time in hour and  minutes of last block
	lastBlockHour=$(tail -1 /var/log/blockdaemon.log | awk -F 'T' '{print $2}' | awk -F ':' '{print $1}')
	lastBlockMinute=$(tail -1 /var/log/blockdaemon.log | awk -F 'T' '{print $2}' | awk -F ':' '{print $2}')
	# get day of the week in numerical form
	day=$(date +%u)
	# get hour and minute from the system
	hourRightNow=$(date +%H)
	minuteRightNow=$(date +%M)
	hourminuteRightNow="$hourRightNow$minuteRightNow"
	# get blocking ranges from /etc/blocklist
	extract_after_day=$(grep "^$day=" /etc/blocklist.conf | awk -F '=' '{print $2}')
	first_time=$(echo "$extract_after_day" | awk -F '-' '{print $1}')
	second_time=$(echo "$extract_after_day" | awk -F '-' '{print $2}')
	third_time=$(echo "$extract_after_day" | awk -F '-' '{print $3}')
	fourth_time=$(echo "$extract_after_day" | awk -F '-' '{print $4}')
	# make check for empty log at some point
	# check log to see if it should ignore (assume it's the same day)
	if [ "$(echo "$hourRightNow - $lastBlockHour" | bc)" = 0 ];then
		howManyMinutes="$(echo "$minuteRightNow - $lastBlockMinute" | bc)"
		howManyMinutesInSeconds="$(echo "$howManyMinutes*60" | bc)"
		sleepFor="$(echo "3600 - $howManyMinutesInSeconds" | bc)"
		echo sleepFor $sleepFor
		sleep $sleepFor
	elif [ "$(echo "$hourRightNow - $lastBlockHour" | bc)" = 1 ];then
		minutesToNextHour="$(echo "60 - $lastBlockMinute" | bc)"
		minutesDifference="$(echo "$minutesToNextHour + $minuteRightNow" | bc)"
		echo minutesDifference $minutesDifference 
	else
		echo not so sure
	fi
	echo minute $minute
	echo lastBlockMinute $lastBlockMinute
	echo sleepFor $sleepFor
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
