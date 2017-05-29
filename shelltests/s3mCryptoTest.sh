#! /bin/sh
# file: s3mCryptoTest.sh

# Test to know if invalid or malicious log files are crashing the tool
testCryptoIssueAvoidance()
{
    rm -rf $HOME/.jfstmerge
    cp -r exemplo/ $HOME/
    cp -r .jfstmerge/ $HOME/
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
    git merge right
    cd ..
    cd .jfstmerge
    CRYPTO_WORKED=$(ls | grep -c "defect")
    assertTrue "[ $CRYPTO_WORKED -gt 0 ]"
}
