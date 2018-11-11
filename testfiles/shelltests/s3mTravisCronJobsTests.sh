#! /bin/sh
# file: s3mTravisCronJobsTests.sh
# This file should not be executed in the regular way.

oneTimeSetUp()
{
    START_PATH=`pwd`/s3m-test-samples
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
    rm -rf $HOME/.jfstmerge/
    cd ${START_PATH}
}

testBigRepository()
{
    # Configuration phase.
    cp -r ${START_PATH}/big/biginitial/* .
	git add .
	git commit -m "initial commit"

	git checkout -b left
	cp -r ${START_PATH}/big/bigleft/* .
	git add .
	git commit -m "second left commit"

	git checkout master
	git checkout -b right
	cp -r ${START_PATH}/big/bigright/* .
	git add .
	git commit -m "right commit"

    # Merge phase.
	git checkout master
	git merge left --no-edit --quiet
	git merge right --no-edit --quiet

    # Evaluation phase.
	cd $HOME/.jfstmerge
	NUM_JAVA_FILES=$(cat jfstmerge.summary | grep -Eo "[0-9]+ JAVA files" | grep -Eo "[0-9]+")
	assertEquals 3 ${NUM_JAVA_FILES}

	FP_AVOIDED=$(cat jfstmerge.summary | grep -Eo "least [0-9]+ false positive\(s\)" | grep -Eo "[0-9]+")
	assertEquals 2 ${FP_AVOIDED}

	FN_AVOIDED=$(cat jfstmerge.summary | grep -Eo "[0-9]+ false negative\(s\)" | grep -Eo "[0-9]+")
	assertEquals 1 ${FN_AVOIDED}

	S3M_NUM_CONFLICTS=$(cat jfstmerge.summary | grep -Eo "reported [0-9]+ conflicts" | grep -Eo "[0-9]+")
	assertEquals 1 ${S3M_NUM_CONFLICTS}

	S3M_NUM_CONFLICTING_LOC=$(cat jfstmerge.summary | grep -Eo "totaling [0-9]+ conflicting" | grep -Eo "[0-9]+")
	assertEquals 2 ${S3M_NUM_CONFLICTING_LOC}

	UNSTR_NUM_CONFLICTS=$(cat jfstmerge.summary | grep -Eo "to [0-9]+ conflicts" | grep -Eo "[0-9]+")
	assertEquals 2 ${UNSTR_NUM_CONFLICTS}

	UNSTR_NUM_CONFLICTING_LOC=$(cat jfstmerge.summary | grep -Eo "and [0-9]+ conflicting" | grep -Eo "[0-9]+")
	assertEquals 5 ${UNSTR_NUM_CONFLICTING_LOC}

	FP_REDUCTION=$(cat jfstmerge.summary | grep -Eo "A reduction of [0-9]+[.,][0-9]+%" | grep -Eo "[0-9]+[.,][0-9]+%")
	assertTrue "[ '$FP_REDUCTION' = '100.00%' ] || [ '$FP_REDUCTION' = '100,00%' ]"

	FN_REDUCTION=$(cat jfstmerge.summary | grep -Eo "And a reduction of [0-9]+[.,][0-9]+%" | grep -Eo "[0-9]+[.,][0-9]+%")
	assertTrue "[ '$FN_REDUCTION' = '100.00%' ] || [ '$FN_REDUCTION' = '100,00%' ]"
}

testMergeRevisionsSample()
{
    # Configuration phase.
    cp -r ${START_PATH}/sample/sampleleft/* .
	git add .
	git commit -m "initial commit"

	git checkout -b left
	cp -r ${START_PATH}/sample/samplebase/* .
	git add .
	git commit -m "left commit"

	git checkout master
	git checkout -b right
	cp -r ${START_PATH}/sample/sampleright/* .
	git add .
	git commit -m "right commit"

    # Merge phase.
	git checkout master
	git merge left --no-edit --quiet
	git merge right --no-edit --quiet

    # Evaluation phase.
	cd $HOME/.jfstmerge
	NUM_JAVA_FILES=$(cat jfstmerge.summary | grep -Eo "[0-9]+ JAVA files" | grep -Eo "[0-9]+")
	assertEquals 1797 ${NUM_JAVA_FILES}

	FP_AVOIDED=$(cat jfstmerge.summary | grep -Eo "least [0-9]+ false positive\(s\)" | grep -Eo "[0-9]+")
	assertEquals 1723 ${FP_AVOIDED}

	FN_AVOIDED=$(cat jfstmerge.summary | grep -Eo "[0-9]+ false negative\(s\)" | grep -Eo "[0-9]+")
	assertEquals 57 ${FN_AVOIDED}

    FP_EXTRA=$(cat jfstmerge.summary | grep -Eo "([0-9]+|no) extra false positive\(s\)" | grep -Eo "([0-9]+|no)")
	assertEquals "no" ${FP_EXTRA}

	FN_EXTRA=$(cat jfstmerge.summary | grep -Eo "[0-9]+ potential extra false negative\(s\)" | grep -Eo "[0-9]+")
	assertEquals 537 ${FN_EXTRA}

	S3M_NUM_CONFLICTS=$(cat jfstmerge.summary | grep -Eo "reported [0-9]+ conflicts" | grep -Eo "[0-9]+")
	assertEquals 1868 ${S3M_NUM_CONFLICTS}

	S3M_NUM_CONFLICTING_LOC=$(cat jfstmerge.summary | grep -Eo "totaling [0-9]+ conflicting" | grep -Eo "[0-9]+")
	assertEquals 31118 ${S3M_NUM_CONFLICTING_LOC}

	UNSTR_NUM_CONFLICTS=$(cat jfstmerge.summary | grep -Eo "to [0-9]+ conflicts" | grep -Eo "[0-9]+")
	assertEquals 3498 ${UNSTR_NUM_CONFLICTS}

	UNSTR_NUM_CONFLICTING_LOC=$(cat jfstmerge.summary | grep -Eo "and [0-9]+ conflicting" | grep -Eo "[0-9]+")
	assertEquals 51703 ${UNSTR_NUM_CONFLICTING_LOC}

	FP_REDUCTION=$(cat jfstmerge.summary | grep -Eo "A reduction of [0-9]+[.,][0-9]+%" | grep -Eo "[0-9]+[.,][0-9]+%")
	assertTrue "[ '$FP_REDUCTION' = '100.00%' ] || [ '$FP_REDUCTION' = '100,00%' ]"

	FN_REDUCTION=$(cat jfstmerge.summary | grep -Eo "And (a reduction of [0-9]+[.,][0-9]+%|no reduction of false negatives)" | grep -Eo "([0-9]+[.,][0-9]+%|no)")
	assertTrue "[ '$FN_REDUCTION' = 'no' ]"

	cat jfstmerge.summary
}

