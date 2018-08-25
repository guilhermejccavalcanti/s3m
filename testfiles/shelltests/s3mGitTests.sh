#! /bin/sh
# file: s3mGitTests.sh

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

# Test to know if s3m semistructured merge is working and detecting conflicts
testSemistructuredMerge()
{
    cp -r exemplo $HOME/
    cp -r exemplotxt $HOME/
    cp -r exemplonodereordering $HOME/

    cp $START_PATH/exemplo/base.java .
    git add .
    git commit -m "base"

    git checkout -b left
    rm base.java
    cp $START_PATH/exemplo/left.java base.java
    git add .
    git commit -m "left"

    git checkout master
    git checkout -b right
    rm base.java
    cp $START_PATH/exemplo/right.java base.java
    git add .
    git commit -m "right"

    git checkout master
    git merge left

    HAS_CONFLICT=$(git merge right | grep -c "CONFLICT")
    assertTrue "[ $HAS_CONFLICT -eq 1 ]"
}


#Test to know if s3m textual merge is working when the semistructured merge doesn't detects conflicts
testTextualMerge()
{
    cp $START_PATH/exemplotxt/base.java .
    git add .
    git commit -m "base"

    git checkout -b left
    rm base.java
    cp $START_PATH/exemplotxt/left.java base.java
    git add .
    git commit -m "left"

    git checkout master
    git checkout -b right
    rm base.java
    cp $START_PATH/exemplotxt/right.java base.java
    git add .
    git commit -m "right"

    git checkout master
    git merge left

    IS_RECURSIVE=$(git merge right -m "test merge" | grep -c "recursive")
    assertTrue "[ $IS_RECURSIVE -eq 1 ]"
}

#Test to know if the s3m tool is not messing up git diff
testWorkingDiff()
{
    cp $START_PATH/exemplo/base.java .
    git add .
    git commit -m "base"

    git checkout -b left
    rm base.java
    cp $START_PATH/exemplo/left.java base.java
    git add .
    git commit -m "left"

    git checkout master
    git checkout -b right
    rm base.java
    cp $START_PATH/exemplo/right.java base.java
    git add .
    git commit -m "right"

    git checkout master
    git merge left

    DIFF_WORKED=$(git diff left right | grep -c "@@ -1,5 +1,9 @@")
    assertTrue "[ $DIFF_WORKED -eq 1 ]"
}

# Test to know if invalid or malicious log files are crashing the tool
testCryptoIssueAvoidance() 
{   
    rm -rf $HOME/.jfstmerge
    mkdir $HOME/.jfstmerge
    cp $START_PATH/exemplo/jfstmerge.statistics .jfstmerge/

    cp $START_PATH/exemplo/base.java .
    git add .
    git commit -m "base"

    git checkout -b left
    rm base.java
    cp $START_PATH/exemplo/left.java base.java
    git add .
    git commit -m "left"

    git checkout master
    git checkout -b right
    rm base.java
    cp $START_PATH/exemplo/right.java base.java
    git add .
    git commit -m "right"

    git checkout master
    git merge left
    git merge right

    cd $HOME/.jfstmerge
    CRYPTO_WORKED=$(ls | grep -c "defect")
    #assertTrue "[ $CRYPTO_WORKED -eq 1 ]"
}

#Test to know if reordering of nodes in superimposition, putting neighbours together, works
testNodeReordering()
{
    cp $START_PATH/exemplonodereordering/base.java .
    git add .
    git commit -m "base"

    git checkout -b left
    rm base.java
    cp $START_PATH/exemplonodereordering/left.java base.java
    git add .
    git commit -m "left"

    git checkout master
    git checkout -b right
    rm base.java
    cp $START_PATH/exemplonodereordering/right.java base.java
    git add .
    git commit -m "right"

    git checkout master
    git merge left
    git merge right

    INTERNAL_CLASSES_AND_INTERFACES=$(cat base.java | sed -n 's/.*[class|interface] \([^ \t\n]*\)/\1/p' | tr -d ' ' | tr -d '\n' | tr -d '{')
    assertEquals 'ABCI' "$INTERNAL_CLASSES_AND_INTERFACES"
}

