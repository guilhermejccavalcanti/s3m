### Status
[![Build Status](https://api.travis-ci.org/guilhermejccavalcanti/jFSTMerge.svg?branch=master)](https://travis-ci.org/guilhermejccavalcanti/jFSTMerge)

jFSTMerge
========

Copyright (c) 2016 by the Federal University of Pernambuco.

A semistructured merge tool for Java applications.

Contact Guilherme Cavalcanti &lt;<gjcc@cin.ufpe.br>&gt;.

Compilation
-----------
This project uses Gradle to manage all dependencies and versioning. 

Installation
-------------
Double-click on the jar from the /installer folder.
After installed, the tool is automatically integrated with git, with no need for futher configurations.
It means that every time you call the `git merge` command, the tool will be executed.
You can also use the tool standalone as described bellow.

* in case double-click does not work, install with the command `java -jar`

Usage
-------------
Use the jar from the /binary folder or from the installed folder.

* Merging 3 files:

   `java -jar pathto/jFSTMerge.jar -f "mine" "base" "theirs" -o "output"`

Where *mine*, *base*, *theirs* and *output* are filepaths.
The attribute -o is optional, if omitted, *theirs* will be used as output file.

* Merging 3 directories:

   `java -jar pathto/jFSTMerge.jar -d "mine" "base" "theirs" -o "output"`
 
Where *mine*, *base*, *theirs* and *output* are directory paths.
The attribute -o is optional, if omitted, *theirs* will be used as output directory.

<!-- 
For integration with git type the two commands bellow:

   `git config --global merge.tool jfstmerge`
   
   `git config --global mergetool.jfstmerge.cmd 'java -jar pathto/jFSTMerge.jar -f \"$LOCAL\" \"$BASE\" \"$REMOTE\" -o \"$MERGED\"'`

Then, after the "git merge" command detects conflicts, call the tool with:

   `git mergetool -tool=jfstmerge`
-->
