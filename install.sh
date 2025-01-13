#!/bin/sh
set -e
if [ "$(basename $(pwd))" != project ]; then
	echo "Run in main project directory"
	exit 1
fi
systemctl > /dev/null
if [ $? -ne 0 ];then
	echo "This program only supports systemd or openrc"
	exit 1
else
	cp service_files/systemd/project.service /etc/systemd/system/project.service
	cp block_daemon.sh /usr/local/bin/block_daemon.sh
	systemctl enable project
	systemctl start project
fi
rc-status > /dev/null
if [ $? -ne 0 ];then
	echo "This program only supports systemd or openrc"
	exit 1
else
	cp service_files/openrc/project /etc/init.d/project
	cp block_daemon.sh /usr/local/bin/block_daemon.sh
	rc-update add project default
	rc-service project start
fi
if ! [ -d /usr/local/man/man8 ]; then
	mkdir /usr/local/man/man8
fi
cp man_pages/project.8 /usr/local/man/man8/project.8
if [ -d build ]; then
	rm -r build
fi
mkdir build
javac -d build/ Project_main.java
cd build
jar cfm Project_main.jar ../MANIFEST.MF Project_main.class
cp Project_main.jar /usr/local/bin/project.jar
chmod 744 /usr/local/bin/project.jar
echo '#!/bin/sh' > /usr/local/bin/project
echo 'java -jar /usr/local/bin/project.jar "$@"' >> /usr/local/bin/project
cd ../
echo "tada!!"
