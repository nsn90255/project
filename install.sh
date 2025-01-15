#!/bin/sh
set -e
if [ "$(basename $(pwd))" != project ]; then
	echo "Run in main project directory"
	exit 1
fi
if which systemctl &> /dev/null;then
	cp service_files/systemd/project.service /etc/systemd/system/project.service
	cp block_daemon.sh /usr/local/bin/block_daemon.sh
	systemctl enable project
elif which rc-status &> /dev/null;then
	cp service_files/openrc/project /etc/init.d/project
	cp block_daemon.sh /usr/local/bin/block_daemon.sh
	chmod 755 /etc/init.d/project
	rc-update add project default
else
	echo "This program only supports systemd and openrc"
	exit 1
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
chmod 744 /usr/local/bin/project
project -s > /dev/null
if which systemctl &> /dev/null;then
	systemctl restart project
elif which rc-status &> /dev/null;then
	rc-service project restart
fi
echo "tada!!"
