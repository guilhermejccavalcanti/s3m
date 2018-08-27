#! /bin/sh
# file: basicFunctions.sh

oneTimeSetUp()
{
    START_PATH=`pwd`
}

setUp()
{
    mkdir $HOME/repo
    cd $HOME/repo
    git init
}

tearDown()
{
    rm -rf $HOME/repo
    rm -rf $HOME/big 
    rm -rf $HOME/bigrepo
    cd $START_PATH
}

testProcedure() {

   if [ -z $2 ]
   then
	 set "$1" "java" 
   fi 

    tearDown
    setUp

    cp $1/base.$2 .
    git add .
    git commit -m "base"
    git checkout -b left
    rm base.$2

    cp $1/left.$2 base.$2
    git add .
    git commit -m "left"
    git checkout master
    git checkout -b right
    rm base.$2

    cp $1/right.$2 base.$2
    git add .
    git commit -m "right"
    git checkout master
    git merge left --no-edit
}

createLogDirectory() {
	rm -rf $HOME/.jfstmerge/
	mkdir $HOME/.jfstmerge/
	cp $1/jfstmerge.statistics $HOME/.jfstmerge/
}
