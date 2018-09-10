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
    cd $START_PATH
}

mergesProcedure() 
{
    TYPE=$2
    DIRECTORY_PATH=$1

    if [ -z $TYPE ]
    then
	    TYPE="java" 
    fi 

    cd $HOME/repo

    cp $DIRECTORY_PATH/base.$TYPE .
    git add .
    git commit -m "base"
    git checkout -b left
    rm base.$TYPE

    cp $DIRECTORY_PATH/left.$TYPE base.$TYPE
    git add .
    git commit -m "left"
    git checkout master
    git checkout -b right
    rm base.$TYPE

    cp $DIRECTORY_PATH/right.$TYPE base.$TYPE
    git add .
    git commit -m "right"
    git checkout master
    git merge left --no-edit
}

createLogDirectory() 
{
	DIRECTORY_PATH=$1

	rm -rf $HOME/.jfstmerge/
	mkdir $HOME/.jfstmerge/
	cp $DIRECTORY_PATH/jfstmerge.statistics $HOME/.jfstmerge/
}
