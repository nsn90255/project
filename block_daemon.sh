#!/bin/sh
day=$(date | awk -F ' ' '{print $1}')
hour=$(date)
minute=$(date)
echo $day
