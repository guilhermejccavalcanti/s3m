### Status
[![Build Status](https://travis-ci.org/guilhermejccavalcanti/jFSTMerge.png)] (https://travis-ci.org/guilhermejccavalcanti/jFSTMerge)

jFSTMerge
========

Copyright (c) 2016 by the Federal University of Pernambuco.

A semistructured merge tool for Java applications.

Contact Guilherme Cavalcanti &lt;<gjcc@cin.ufpe.br>&gt;.

Compilation
-----------
This project uses Gradle to manage all dependencies and versioning. 

Usage
-------------
Use the jar from the /binary folder.

* Merging 3 files:

   `java -jar jFSTMerge.jar -f mine base theirs -o output`

Where *mine*, *base*, *theirs* and *output* are filepaths.
The attribute -o is optional, if omitted, *theirs* will be used as output file.

* Merging 3 directories:

   `java -jar jFSTMerge.jar -d mine base theirs -o output`
 
* For integration with git type the two commands bellow:

   `git config --global merge.tool jfstmerge`
   
   `git config --global mergetool.jfstmerge.cmd 'java -jar jFSTMerge.jar -f \"$LOCAL\" \"$BASE\" \"$REMOTE\" -o \"$MERGED\"'`

Where *mine*, *base*, *theirs* and *output* are directory paths.
The attribute -o is optional, if omitted, "theirs" will be used as output directory.
