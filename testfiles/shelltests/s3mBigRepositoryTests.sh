#! /bin/sh
# file: s3mBigRepositoryTests.sh

. ./functions/basicFunctions.sh

tearDownBig()
{
    rm -rf $HOME/big 
    rm -rf $HOME/bigrepo
    cd $START_PATH
}

setUpBig()
{
	mkdir $HOME/bigrepo
	cd $HOME/bigrepo
	git init
}

bigRepositoryMergeProcedure() 
{	
	cp -r $START_PATH/big/biginitial/* .
	git add .
	git commit -m "initial commit"

	git checkout -b left
	cp -r $START_PATH/big/bigleft/* .
	git add .
	git commit -m "second left commit"

	git checkout master
	git checkout -b right
	cp -r $START_PATH/big/bigright/* .
	git add .
	git commit -m "right commit"

	git checkout master
	git merge left --no-edit
  
}

#Tests multiple merges with a great number of files
testMultipleMerges()
{
	setUpBig

	bigRepositoryMergeProcedure
	MERGE_COUNT=$(git merge right | grep -c "finished")

	assertTrue "[ $MERGE_COUNT -eq 3 ]"

	tearDownBig
}

#Test multiple merges with some corrupted or invalid java files
testCorruptedFilesMerge()
{
	setUpBig

	cp -r $START_PATH/big/biginitial/* .
	git add .
	git commit -m "initial commit"
	git checkout -b left

	cp -r $START_PATH/big/bigcorrupted/* .
	git add .
	git commit -m "second left commit"

	git checkout master
	git checkout -b right
	cp -r $START_PATH/big/bigright/* .
	git add .
	git commit -m "right commit"
	
	git checkout master
	git merge left --no-edit

	MERGE_COUNT=$(git merge right | grep -c "finished")
	assertTrue "[ $MERGE_COUNT -eq 3 ]"

	tearDownBig
}

#Test log correctness with big repositories
testLogCorrectness() {
	setUpBig

 	rm -rf $HOME/.jfstmerge
	bigRepositoryMergeProcedure
	git merge right --no-edit

	cd $HOME/.jfstmerge
	NUM_JAVA_FILES=$(cat jfstmerge.summary | grep -Eo "[0-9]+ JAVA files" | grep -Eo "[0-9]")
	assertTrue "[ '$NUM_JAVA_FILES' = '3' ]"

	FP_AVOIDED=$(cat jfstmerge.summary | grep -Eo "least [0-9]+ false positive\(s\)" | grep -Eo "[0-9]")
	assertTrue "[ '$FP_AVOIDED' = '1' ]"

	FN_AVOIDED=$(cat jfstmerge.summary | grep -Eo "[0-9]+ false negative\(s\)" | grep -Eo "[0-9]")
	assertTrue "[ '$FN_AVOIDED' = '1' ]"

	S3M_NUM_CONFLICTS=$(cat jfstmerge.summary | grep -Eo "reported [0-9]+ conflicts" | grep -Eo "[0-9]")
	assertTrue "[ '$S3M_NUM_CONFLICTS' = '1' ]"

	S3M_NUM_CONFLICTING_LOC=$(cat jfstmerge.summary | grep -Eo "totaling [0-9]+ conflicting" | grep -Eo "[0-9]")
	assertTrue "[ '$S3M_NUM_CONFLICTING_LOC' = '2' ]"

	UNSTR_NUM_CONFLICTS=$(cat jfstmerge.summary | grep -Eo "to [0-9]+ conflicts" | grep -Eo "[0-9]")
	assertTrue "[ '$UNSTR_NUM_CONFLICTS' = '1' ]"

	UNSTR_NUM_CONFLICTING_LOC=$(cat jfstmerge.summary | grep -Eo "and [0-9]+ conflicting" | grep -Eo "[0-9]")
	assertTrue "[ '$UNSTR_NUM_CONFLICTING_LOC' = '2' ]"

	FP_REDUCTION=$(cat jfstmerge.summary | grep -Eo "A reduction of [0-9]+[.,][0-9]+%" | grep -Eo "[0-9]+[.,][0-9]+%")
	assertTrue "[ '$FP_REDUCTION' = '100.00%' ] || [ '$FP_REDUCTION' = '100,00%' ]"

	FN_REDUCTION=$(cat jfstmerge.summary | grep -Eo "And a reduction of [0-9]+[.,][0-9]+%" | grep -Eo "[0-9]+[.,][0-9]+%")
	assertTrue "[ '$FN_REDUCTION' = '100.00%' ] || [ '$FN_REDUCTION' = '100,00%' ]"

	tearDownBig
}

suite_addTest testMultipleMerges
suite_addTest testCorruptedFilesMerge
suite_addTest testLogCorrectness
