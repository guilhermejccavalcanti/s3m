#! /bin/sh

. ./functions/basicFunctions.sh

multipleLanguages() {
   
    cp -r otherlanguages $HOME/
    cd $HOME
    createLogDirectory "otherlanguages"
    testProcedure "../otherlanguages" "$2"
    HAS_CONFLICT=$(git merge right | grep -c "CONFLICT")
    cd ../.jfstmerge
    CRYPTO_WORKED=$(ls | grep -c "defect")
    assertTrue "[ $HAS_CONFLICT -eq 1 ]"
    assertTrue "[ $CRYPTO_WORKED -eq 0 ]"
    cd ..
    rm -rf repo
    rm -rf .jfstmerge
}
