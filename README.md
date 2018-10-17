### Status
[![Build Status](https://api.travis-ci.org/guilhermejccavalcanti/jFSTMerge.svg?branch=master)](https://travis-ci.org/guilhermejccavalcanti/jFSTMerge)

jFSTMerge
========

Copyright (c) 2016 by the Federal University of Pernambuco.

A semistructured merge tool for Java applications.

Contact Guilherme Cavalcanti &lt;<gjcc@cin.ufpe.br>&gt;.

## Getting Started

### Downloading
This project uses [Git Large File Storage](https://git-lfs.github.com/) to version binaries. 
- After installing it, you may additionally run ``git lfs install --skip-smudge`` to avoid downloading the project binaries.
- It's always possible to retrieve their most updated versions by running `git lfs pull`.

### Compilation
This project uses Gradle to manage all dependencies and versioning. JAVA 8 is required.

### Installation

#### Requisites

Installation integrates the tool with GIT version control system. So, GIT must be installed and configured. Otherwise, you can run the tool [standalone](https://github.com/guilhermejccavalcanti/jFSTMerge#running-standalone).

#### Automatic installation
Double-click on the jar from the [/installer](https://github.com/guilhermejccavalcanti/jFSTMerge/tree/master/installer) folder.
In case double-click does not work, install with the command 
`java -jar s3mInstaller.jar`.

#### Manual installation
1. Copy the [binary](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/binary/jFSTMerge.jar) file to your `$HOME` folder.
2. Add the following lines to your `.gitconfig` file (typically localized in the `$HOME` folder).
	```
    [core]
		attributesfile = ~/.gitattributes
	[merge "s3m"]
		name = semi_structured_3_way_merge_tool_for_java
		driver = java  -jar "\"$HOME/jFSTMerge.jar\"" -f %A %O %B -o %A -g
    ```
3. Add the following line to your `.gitattributes` file (also localized in the `$HOME` folder)
	
    `*.java merge=s3m`
    

## Usage

#### Statistics
- Usage data (such as the number of detected conflicts, number of merged scenarios, and more useful details for studying the benefits and drawbacks of the tool) is stored in the `$HOME/.jfstmerge` folder.
- A summary of collected statistics that might help one decide to continue using the tool is available in the `jfstmerge.summary` file.

#### Running with _git_

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

## Development

### Testing

We provide standalone shell tests in addition to a few [JUnit tests](https://github.com/guilhermejccavalcanti/jFSTMerge/tree/master/src/br/ufpe/cin/tests). To execute those, the [shunit2](https://github.com/kward/shunit2/) framework is required:

* Mac:
1. Install [Homebrew](https://brew.sh/) for package managing.
2. Run `brew install shunit2 `.

* Linux:
1. Run `sudo apt-get install shunit2`.

* Windows:
1. Run `git config --global core.autocrlf true` to avoid line-ending issues, as shell tests use primarily LF line-endings.
2. Install a Linux [environment](https://www.howtogeek.com/249966/how-to-install-and-use-the-linux-bash-shell-on-windows-10/) and follow the [manual installation instructions](https://github.com/guilhermejccavalcanti/jFSTMerge#manual-installation) above on it.

To execute the tests, follow the instructions below:
1. Open terminal in [testfiles/shelltests](https://github.com/guilhermejccavalcanti/jFSTMerge/tree/master/testfiles/shelltests) directory.
2. Run `shunit2 s3mTests.sh` to execute all of the tests. It's possible to execute isolated tests by replacing `s3mTests` for any `.sh` file. 

<!--The files "exemplo", "exemplotxt" and "big"  should be copied to your $HOME directory during the execution of the tests (you can delete them manually if you want after the execution of the tests).-->

### Binaries updating
The main and installer binaries ([jFSTMerge](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/binary/jFSTMerge.jar) and [s3mInstaller](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/installer/s3mInstaller.jar), respectively) are updated on every build.
 - It's possible to update the main binary by running `gradle copyBinary`.
 - It's possible to update the installer binary by running `gradle updateInstaller`.
 On both tasks, the files are assembled but no test is executed.