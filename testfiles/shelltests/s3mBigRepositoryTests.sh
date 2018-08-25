#! /bin/sh
# file: s3mBigRepositoryTests.sh

oneTimeSetUp()
{
	START_PATH=`pwd`
}

setUp()
{
	mkdir $HOME/bigrepo
	cd $HOME/bigrepo
	git init
}

tearDown()
{
	rm -rf $HOME/big
	rm -rf $HOME/bigrepo
	cd $START_PATH
}

#Tests multiple merges with a great number of files
testMultipleMerges()
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

	MERGE_COUNT=$(git merge right | grep -c "finished")
	assertTrue "[ $MERGE_COUNT -eq 3 ]"
}

#Test multiple merges with some corrupted or invalid java files
testCorruptedFilesMerge()
{
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
}

suite_addTest testMultipleMerges
suite_addTest testCorruptedFilesMerge
