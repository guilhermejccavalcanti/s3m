#! /bin/sh
# file: basicFunctions.sh

testProcedure() {

   if [ -z $2 ]
   then
	 set "$1" "java" 
   fi 

    rm -rf repo
    mkdir repo
    cd repo
    git init
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
    git merge left
}

createLogDirectory() {
	rm -rf .jfstmerge/
	mkdir .jfstmerge/
	cp $1/jfstmerge.statistics ./.jfstmerge/
}
