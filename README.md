# Install
Run the install script as root **in the main project directory**.
```
# ./install
```
### Dependencies
You will need openjdk and bc installed on your system.
# Post install set up
Allow members of a specific group (users as an example here) to use the ignore flag without root permissions
```
%users ALL=(ALL) NOPASSWD: /usr/local/bin/project -i
```
## Note
This has only been tested on debian and artix(openrc)
