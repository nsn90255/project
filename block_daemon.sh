#!/bin/sh
while :
do
	ignore=$(grep ignore /etc/blocklist.conf | awk -F '=' '{print $2}') 
	lastblock=$(tail -1 /home/normal-user/project/blockdeamon.log)
	day=$(date +%u)
	hourminute=$(date +%H%M)
	extract_after_day=$(grep "^$day=" /etc/blocklist.conf | awk -F '=' '{print $2}')
	first_time=$(echo "$extract_after_day" | awk -F '-' '{print $1}')
	second_time=$(echo "$extract_after_day" | awk -F '-' '{print $2}')
	third_time=$(echo "$extract_after_day" | awk -F '-' '{print $3}')
	fourth_time=$(echo "$extract_after_day" | awk -F '-' '{print $4}')
	# check ignore at /etc/blocklist to see it should ignore it
	#if [ "$ignore" = "true" ]; then
		#echo ignoring	
		#echo "Ignoring at $(date) until #$(date +%m.%e.)$(echo "$(date +%H)+1" | bc).$(date +%M)" >> /var/log/blockdeamon.log
		#$(project -u > /dev/null)
		#sleep 3600
	#fi
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
