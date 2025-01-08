#!/bin/sh
set -e
if [ "$(basename $(pwd))" != project ]; then
	echo "Run in main project directory"
	exit 1
fi
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
cp block_daemon.sh /usr/local/bin/block_daemon.sh
if [ -d /lib/systemd ]; then
	cp systemd/project.service /etc/systemd/system/project.service
else
	echo "This program only supports systemd for now."
fi
echo "tada!!"
