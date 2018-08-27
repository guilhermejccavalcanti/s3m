#! /bin/sh

. ./functions/basicFunctions.sh

multipleLanguages() {
   
    cp -r $START_PATH/otherlanguages $HOME/
    cd $HOME
    createLogDirectory "$START_PATH/otherlanguages"
    testProcedure "$START_PATH/otherlanguages" "$2"
    
    HAS_CONFLICT=$(git merge right | grep -c "CONFLICT")
    assertTrue "[ $HAS_CONFLICT -eq 1 ]"
    
    cd $HOME/.jfstmerge
    CRYPTO_WORKED=$(ls | grep -c "defect")
    assertTrue "[ $CRYPTO_WORKED -eq 0 ]"

    rm -rf $HOME/otherlanguages
}
