# To compile jar file

$ javac Project_main.java

$ jar cfm Project_main.jar MANIFEST.MF Project_main.class

# To add to /bin/ 

\# cp Project_main.jar /usr/local/bin/project.jar

\# chmod +x /usr/local/bin/project.jar

## And write this to /bin/project :

#!/bin/bash
java -jar /usr/local/bin/project.jar "$@"