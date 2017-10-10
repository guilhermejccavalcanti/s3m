### Status
[![Build Status](https://api.travis-ci.org/guilhermejccavalcanti/jFSTMerge.svg?branch=master)](https://travis-ci.org/guilhermejccavalcanti/jFSTMerge)

jFSTMerge
========

Copyright (c) 2016 by the Federal University of Pernambuco.

A semistructured merge tool for Java applications.

Contact Guilherme Cavalcanti &lt;<gjcc@cin.ufpe.br>&gt;.

Compilation
-----------
This project uses Gradle to manage all dependencies and versioning. JAVA 8 is required.


#### Troubleshooting

If you face error while trying to build the project with gradle, just delete the lines from 10 to 13 of this [file](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/build.gradle) .

Installation
-------------
#### Requisites

The instalation intregates the tool with GIT version control system. So, GIT must be installed and configured. Otherwise, you can run the tool [standalone](https://github.com/guilhermejccavalcanti/jFSTMerge#running-standalone).

#### Automatic installation
Double-click on the jar from the [/installer](https://github.com/guilhermejccavalcanti/jFSTMerge/tree/master/installer) folder.
In case double-click does not work, install with the command 
`java -jar s3mInstaller.jar`

#### Manual installation
1. Copy the [binary](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/binary/jFSTMerge.jar) file to your `$HOME` folder
2. Add the following lines to your `.gitconfig` file (typically localized in the `$HOME` folder)
	```
    [core]
		attributesfile = ~/.gitattributes
	[merge "s3m"]
		name = semi_structured_3_way_merge_tool_for_java
		driver = java  -jar "\"$HOME/jFSTMerge.jar\"" -f %A %O %B -o %A -g
    ```
3. Add the following line to your `.gitattributes` file (also localized in the `$HOME` folder)
	
    `*.java merge=s3m`
    

Usage
-------------

Usage data (such as the number of detected conflicts, number of merged scenarios, and more useful details for studying the benefits and drawbacks of the tool) is stored in the `$HOME/.jfstmerge` folder.  A summary of collected statistics that might help one decide to continue using the tool is available in the `jfstmerge.summary` file.

#### Running with git

After installation, the tool is automatically integrated with git, with no need for further configuration. Then every time you invoke the `git merge` command, the tool is executed.

#### Running standalone

Use the jar from the [/binary](https://github.com/guilhermejccavalcanti/jFSTMerge/tree/master/binary) folder, or from the installed folder.

* Merging 3 files:

  `java -jar pathto/jFSTMerge.jar -f "mine" "base" "theirs" -o "output"`

Where *mine*, *base*, *theirs* and *output* are filepaths.
The attribute -o is optional, if omitted, *theirs* is used as the output file.

* Merging 3 directories:

  `java -jar pathto/jFSTMerge.jar -d "mine" "base" "theirs" -o "output"`

Where *mine*, *base*, *theirs* and *output* are directory paths.
The attribute -o is optional, if omitted, *theirs* is used as the output directory.

<!-- 
For integration with git type the two commands bellow:

   `git config --global merge.tool jfstmerge`
   
   `git config --global mergetool.jfstmerge.cmd 'java -jar pathto/jFSTMerge.jar -f \"$LOCAL\" \"$BASE\" \"$REMOTE\" -o \"$MERGED\"'`

Then, after the "git merge" command detects conflicts, call the tool with:

   `git mergetool -tool=jfstmerge`
-->

Testing
-------------

We provide standalone tests in the [/testfiles/shelltests](https://github.com/guilhermejccavalcanti/jFSTMerge/tree/master/testfiles/shelltests) folder in addition to a few [JUnit tests](https://github.com/guilhermejccavalcanti/jFSTMerge/tree/master/src/br/ufpe/cin/mergers/handlers/tests).
To run theses tests, you will need the shunit2 framework installed:

* On Mac:
1. Install the homebrew packet manager
2. Run the command  `brew install shunit2 `

* On Linux:
1. `sudo apt-get install shunit2`

* On Windows:
1. Install a Linux [enviroment](https://www.howtogeek.com/249966/how-to-install-and-use-the-linux-bash-shell-on-windows-10/) , and follow the manual instalation [instructions](https://github.com/guilhermejccavalcanti/jFSTMerge#manual-installation) above on the Linux enviroment.

To execute the tests, follow the instructions bellow:
1. Go to the tesfiles/shelltests directory inside the jFSTMerge project folder
2. Open the terminal
3. Run the command `shunit2 test_you_want_to_execute.sh`
4. Take a look at the output in the terminal to see the result of your tests

The files "example", "exampletxt" and "big"  should be copied to your $HOME directory during the execution of the tests (you can delete them manually if you want after the execution of the tests).
