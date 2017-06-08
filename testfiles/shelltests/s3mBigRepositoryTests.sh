#! /bin/sh
# file: bigtest.sh

#Tests multiple merges with a great number of files
testMultipleMerges()
{
	cp -r big/ $HOME/
	cd $HOME
	rm -rf bigrepo/
	mkdir bigrepo
	cd bigrepo
	git init 
	cp -r ../big/biginitial/* .
	git add .
	git commit -m "initial commit"
	git checkout -b left
	cp -r ../big/bigleft/* .
	git add .
	git commit -m "second left commit"
	git checkout master
	git checkout -b right
	cp -r ../big/bigright/* .
	git add .
	git commit -m "right commit"
	git checkout master
	git merge left
	MERGE_COUNT=$(git merge right | grep -c "finished")
	assertTrue "[ $MERGE_COUNT -eq 3 ]"
	cd .. 
    rm -rf bigrepo
}

#Test multiple merges with some corrupted or invalid java files
testCorruptedFilesMerge()
{
    cp -r big/ $HOME/
	cd $HOME
	rm -rf bigrepo/
	mkdir bigrepo
	cd bigrepo
	git init 
	cp -r ../big/biginitial/* .
	git add .
	git commit -m "initial commit"
	git checkout -b left
	cp -r ../big/bigcorrupted/* .
	git add .
	git commit -m "second left commit"
	git checkout master
	git checkout -b right
	cp -r ../big/bigright/* .
	git add .
	git commit -m "right commit"
	git checkout master
	git merge left
	MERGE_COUNT=$(git merge right | grep -c "finished")
	assertTrue "[ $MERGE_COUNT -eq 3 ]"
	cd .. 
    rm -rf bigrepo
	
}
