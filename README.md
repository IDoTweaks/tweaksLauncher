# tweaksLauncher
this is my first publicly available project its meant for linux users and its a launcher for all of your apps
currently supports steam + epic + flatpak + if you got any apps like zoom or something

# HOW TO RUN
you will need to build it in the following way:
open a terminal in the project root and run this:

javac -d out src/*.java

then to create the jar run: 

jar cfm tweaksLauncher.jar manifest.txt -C out/ .

and to run it: 

java -jar tweaksLauncher.jar



--IF YOU DONT HAVE JAVA INSTALLED 
run: sudo apt install openjdk-25-jdk
