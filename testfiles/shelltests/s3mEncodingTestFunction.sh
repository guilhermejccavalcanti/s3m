#! /bin/sh
# file: s3mEncodingTests.sh

# Test to know if s3m semistructured merge is working with multiple encodings.
testMultipleEncodings()
{	
    cp -r otherencodings $HOME/
    cd $HOME
    rm -rf repo
    mkdir repo
    cd repo
    git init
    cp $1/base.java .
    git add .
    git commit -m "base"
    git checkout -b left
    rm base.java
    cp $1/left.java base.java
    git add .
    git commit -m "left"
    git checkout master
    git checkout -b right
    rm base.java
    cp $1/right.java base.java
    git add .
    git commit -m "right"
    git checkout master
    git merge left
    HAS_CONFLICT=$(git merge right | grep -c "CONFLICT")
    assertTrue "[ $HAS_CONFLICT -eq 1 ]"
    cd ..
}
