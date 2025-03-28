#!/bin/sh
set -e
if ! [ "$(id -u)" -eq 0 ]; then
	echo -e "\033[1;31mrun as root.\033[0m"
	exit 1
fi
if which systemctl &> /dev/null;then
	install -m 755 service_files/systemd/project.service /etc/systemd/system/project.service
	install -m 755 block_daemon.sh /usr/local/bin/block_daemon.sh
	systemctl enable project
elif which rc-status &> /dev/null;then
	install -m 755 service_files/openrc/project /etc/init.d/project
	install -m 755 block_daemon.sh /usr/local/bin/block_daemon.sh
	rc-update add project default
else
	echo "This program only supports systemd and openrc"
	exit 1
fi
if ! [ -d /usr/local/man/man8 ]; then
	mkdir /usr/local/man/man8
fi
cp man_pages/project.8 /usr/local/man/man8/project.8
if ! [ -d /usr/local/man/man5 ]; then
	mkdir /usr/local/man/man5
fi
if ! [ -d /var/lib/project ]; then
	mkdir /var/lib/project
fi
cp defaults.conf /var/lib/project/defaults.conf
cp man_pages/blocklist.conf.5 /usr/local/man/man5/blocklist.conf.5
if [ -d build ]; then
	rm -r build
fi
mkdir build
javac -d build/ Project_main.java
cd build
jar cfm Project_main.jar ../MANIFEST.MF Project_main.class
install -m 744 Project_main.jar /usr/local/bin/project.jar
echo '#!/bin/sh' > /usr/local/bin/project
echo 'java -jar /usr/local/bin/project.jar "$@"' >> /usr/local/bin/project
cd ../
touch /var/log/blockdaemon.log
chown root:root /var/log/blockdaemon.log
chmod 644 /var/log/blockdaemon.log
chmod 744 /usr/local/bin/project
project -s > /dev/null
if which systemctl &> /dev/null;then
	systemctl restart project
elif which rc-status &> /dev/null;then
	rc-service project restart &
fi
echo "tada!!"
