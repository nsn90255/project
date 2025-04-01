#!/bin/sh

# Function to extract block times for a specific day from the configuration file
extract_times_from_config() {
    day="$1"
    block_time=$(grep "^$day=" /etc/blocklist.conf | cut -d '=' -f2)
    
    # Split block_time into four components (if available)
    set -- $(echo "$block_time" | tr '-' ' ')
    first_time="$1"
    second_time="$2"
    third_time="$3"
    fourth_time="$4"
}

# Function to get current time in HHMM format
get_current_time() {
    hour=$(date +%H)
    minute=$(date +%M)
    echo "$hour$minute"
}

# Function to calculate the sleep time based on the difference in times
calculate_sleep_time() {
    last_block_hour="$1"
    last_block_minute="$2"
    current_hour="$3"
    current_minute="$4"

    if [ "$current_hour" -eq "$last_block_hour" ]; then
        # Same hour, calculate minute difference
        minutes_diff=$((current_minute - last_block_minute))
        sleep_time=$((60 - minutes_diff)) # Sleep until the next minute
    elif [ "$((current_hour - last_block_hour))" -eq 1 ]; then
        # One hour difference, sleep till the next full hour
        sleep_time=$((60 - last_block_minute + current_minute))
    else
        # Default sleep time of 10 seconds if unsure
        sleep_time=10
    fi

    echo "$sleep_time"
}

# Main loop
while :; do
    # Extract the last block time from the log
    last_block_time=$(tail -n 1 /var/log/blockdaemon.log | cut -d 'T' -f2)
    last_block_hour=$(echo "$last_block_time" | cut -d ':' -f1)
    last_block_minute=$(echo "$last_block_time" | cut -d ':' -f2)
    
    # Get current day of the week (1-7)
    current_day=$(date +%u)
    
    # Get current time in HHMM format
    current_time=$(get_current_time)
    
    # Get block time ranges for the current day
    extract_times_from_config "$current_day"
    
    # Calculate how long to sleep based on the last block time
    sleep_time=$(calculate_sleep_time "$last_block_hour" "$last_block_minute" $(date +%H) $(date +%M))

    # Sleep for the calculated time
    echo "Sleeping for $sleep_time seconds"
    sleep "$sleep_time"

    # Check if it's time to block or unblock
    if [ "$first_time" = "all" ] || { [ "$first_time" -le "$current_time" ] && [ "$second_time" -ge "$current_time" ]; } || { [ "$third_time" -le "$current_time" ] && [ "$fourth_time" -ge "$current_time" ]; }; then
        echo "Blocking websites"
        project -b > /dev/null
    else
        echo "Unblocking websites"
        project -u > /dev/null
    fi
    
    # Wait a short time before checking again
    sleep 10
done
