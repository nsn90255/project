#!/bin/sh
set -e
if ! [ "$(id -u)" -eq 0 ]; then
	echo -e "\033[1;31mrun as root.\033[0m"
	exit 1
fi
if which systemctl &> /dev/null;then
	install -m 755 service_files/systemd/dbt.service /etc/systemd/system/dbt.service
	install -m 755 daemon/block_daemon.sh /usr/local/bin/block_daemon.sh
	systemctl enable dbt
elif which rc-status &> /dev/null;then
	install -m 755 service_files/openrc/dbt /etc/init.d/dbt
	install -m 755 daemon/block_daemon.sh /usr/local/bin/block_daemon.sh
	rc-update add dbt default
else
	echo "This program only supports systemd and openrc"
	exit 1
fi
if ! [ -d /usr/local/man/man8 ]; then
	mkdir /usr/local/man/man8
fi
cp man_pages/dbt.8 /usr/local/man/man8/dbt.8
if ! [ -d /usr/local/man/man5 ]; then
	mkdir /usr/local/man/man5
fi
if ! [ -d /var/lib/dbt ]; then
	mkdir /var/lib/dbt
fi
cp defaults.conf /var/lib/dbt/defaults.conf
cp man_pages/blocklist.conf.5 /usr/local/man/man5/blocklist.conf.5
if [ -d build ]; then
	rm -r build
fi
mkdir build
javac -d build/ Project_main.java
cd build
jar cfm Project_main.jar ../MANIFEST.MF Project_main*.class
install -m 744 Project_main.jar /usr/local/bin/dbt.jar
cd ..
rm build/*
javac -d build/ daemon/BlockDaemon.java
cd build
jar cfm BlockDaemon.jar ../daemon/MANIFEST.MF BlockDaemon.class
install -m 744 BlockDaemon.jar /usr/local/bin/blockdaemon.jar
cp dbt /usr/local/bin/dbt
cd ../
touch /var/log/blockdaemon.log
chown root:root /var/log/blockdaemon.log
chmod 644 /var/log/blockdaemon.log
chmod 744 /usr/local/bin/dbt
dbt -s > /dev/null
if which systemctl &> /dev/null;then
	systemctl restart dbt
elif which rc-status &> /dev/null;then
	rc-service dbt restart &
fi
echo "tada!!"
