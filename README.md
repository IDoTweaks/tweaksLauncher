# tweaksLauncher
this is my first publicly available project its meant for linux users and its a launcher for all of your apps

# HOW TO RUN
you will need to build it in the following way:
open a terminal in the project root and run this:javac -d out src/*.java

then to create the jar run: jar cfm tweaksLauncher.jar manifest.txt -C out/ .

and to run it: java -jar tweaksLauncher.jar



--IF YOU DONT HAVE JAVA INSTALLED 
run: sudo apt install openjdk-25-jdk

# WHY?
well i dont want to specify my age but im young, i want to get to some place that will require experience and i belive that if i start early then i will get a lead
also i never touched things like guis and most libraries and never did anything involving linux systems(ubuntu cause its what im on) so this project is more of a learning expirience for me that will also be usefull just for myself

# ABOUT
this launcher should just be like steam but with WAYYY less features just kinda like the add own game and the play button lol

# do i recommend using it?
no at least not for now but if you want you can look around the code and laugh at me:D












# for future me


# things i had to learn
VERY basic swing and how to tell chatgpt to do the little bit harder part.
json reading + writing(no creating tho the file comes with it).
adding try and catch + throw ioexception anytime inteliji orders me to.
using processbuilder and some linux commands.

# UPDATE
i just finished the first version and im very proud of myself my code is kind of messy and with no comments BUT in the gui part which i made 70% on my own but then light mode burnt my eyes and i couldnt figure out how to make dynamic buttons so yeah that part's chatgpt work not mine but the programHandler is 100% me.
current features:
you can add games by paths.
you can add games by name and if its easily findable(for linux i use a command idk how it works lol) then it adds it.
complete gui that looks like garbage and will forever.
multiple apps in the same time --unintentional i kinda thought it wont work but for some reason it does.
# new
just added flatpak support

current "features"
it can search your whole system! but it fails 99% of the time and you cant cancel it .//removed it:D
for some unkown reason i didnt want to research it all opens under the inteliji thing so you need to hover on it and pick your program.//except for flatpaks
dont do things wrong it will crash everything.//most of the time not always


# UPDATE 2 
well it should be 3 but i just added the previous one to include both of them so i:
fixed the infinite search problem
added a remove program function + a remove mode 
when built and ran it opens app as themselves and not as my app
i asked chatgpt to make the ui cooler literly that and it broke some things but it was easy to fix

# things i plan to add
option to edit the places of the apps which will just mean i copy 2 chunks of the json save 1 as temp and just switch places which i hope is easy
option to add icons to apps which just means in the json file i need to add a path to the icon
repair button i have everything for it i just need to add the actual button
