#! /bin/sh
# file: s3mGitTests.sh


# Test to know if s3m semistructured merge is working and detecting conflicts
testSemistructuredMerge()
{
    cp -r exemplo $HOME/
    cp -r exemplotxt $HOME/
    cp -r exemplonodereordering $HOME/
    cd $HOME
    rm -rf repo
    mkdir repo
    cd repo
    git init
    cp ../exemplo/base.java .
    git add .
    git commit -m "base"
    git checkout -b left
    rm base.java
    cp ../exemplo/left.java base.java
    git add .
    git commit -m "left"
    git checkout master
    git checkout -b right
    rm base.java
    cp ../exemplo/right.java base.java
    git add .
    git commit -m "right"
    git checkout master
    git merge left
    HAS_CONFLICT=$(git merge right | grep -c "CONFLICT")
    assertTrue "[ $HAS_CONFLICT -eq 1 ]"
    cd .. 
    rm -rf repo
}


#Test to know if s3m textual merge is working when the semistructured merge doesn't detects conflicts
testTextualMerge()
{
    rm -rf repo
    mkdir repo
    cd repo
    git init
    cp ../exemplotxt/base.java .
    git add .
    git commit -m "base"
    git checkout -b left
    rm base.java
    cp ../exemplotxt/left.java base.java
    git add .
    git commit -m "left"
    git checkout master
    git checkout -b right
    rm base.java
    cp ../exemplotxt/right.java base.java
    git add .
    git commit -m "right"
    git checkout master
    git merge left
    IS_RECURSIVE=$(git merge right -m "test merge" | grep -c "recursive")
    assertTrue "[ $IS_RECURSIVE -eq 1 ]"
    cd .. 
    rm -rf repo
}

#Test to know if the s3m tool is not messing up git diff
testWorkingDiff()
{
    rm -rf repo
    mkdir repo
    cd repo
    git init
    cp ../exemplo/base.java .
    git add .
    git commit -m "base"
    git checkout -b left
    rm base.java
    cp ../exemplo/left.java base.java
    git add .
    git commit -m "left"
    git checkout master
    git checkout -b right
    rm base.java
    cp ../exemplo/right.java base.java
    git add .
    git commit -m "right"
    git checkout master
    git merge left
    DIFF_WORKED=$(git diff left right | grep -c "@@ -1,5 +1,9 @@")
    assertTrue "[ $DIFF_WORKED -eq 1 ]"
    cd .. 
    rm -rf repo
}

# Test to know if invalid or malicious log files are crashing the tool
testCryptoIssueAvoidance() 
{   
    rm -rf .jfstmerge
    mkdir .jfstmerge
    cp exemplo/jfstmerge.statistics .jfstmerge/
    rm -rf repo
    mkdir repo
    cd repo
    git init
    cp ../exemplo/base.java .
    git add .
    git commit -m "base"
    git checkout -b left
    rm base.java
    cp ../exemplo/left.java base.java
    git add .
    git commit -m "left"
    git checkout master
    git checkout -b right
    rm base.java
    cp ../exemplo/right.java base.java
    git add .
    git commit -m "right"
    git checkout master
    git merge left
    git merge right
    cd ..
    cd .jfstmerge
    CRYPTO_WORKED=$(ls | grep -c "defect")
    assertTrue "[ $CRYPTO_WORKED -eq 1 ]"
    cd .. 
    rm -rf repo
}

#Test to know if reordering of nodes in superimposition, putting neighbours together, works
testNodeReordering()
{
    rm -rf repo
    mkdir repo
    cd repo
    git init
    cp ../exemplonodereordering/base.java .
    git add .
    git commit -m "base"
    git checkout -b left
    rm base.java
    cp ../exemplonodereordering/left.java base.java
    git add .
    git commit -m "left"
    git checkout master
    git checkout -b right
    rm base.java
    cp ../exemplonodereordering/right.java base.java
    git add .
    git commit -m "right"
    git checkout master
    git merge left
    git merge right
    INTERNAL_CLASSES_AND_INTERFACES=$(cat base.java | sed -n 's/.*[class|interface] \([^ \t\n]*\)/\1/p' | tr -d ' ' | tr -d '\n' | tr -d '{')
    assertEquals 'ABCI' "$INTERNAL_CLASSES_AND_INTERFACES"
    cd .. 
}

