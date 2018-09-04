#! /bin/sh

. ./functions/basicFunctions.sh

multipleLanguages() 
{
    LANGUAGE_PATH=$1
    FILE_TYPE=$2
   
    cp -r $START_PATH/otherlanguages $HOME/
    cd $HOME
    createLogDirectory "$LANGUAGE_PATH"
    
    mergesProcedure "$LANGUAGE_PATH" "$FILE_TYPE"
    HAS_CONFLICT=$(git merge right | grep -c "CONFLICT")
    cd $HOME/.jfstmerge
    CRYPTO_WORKED=$(ls | grep -c "defect")
    
    assertTrue "[ $CRYPTO_WORKED -eq 0 ]"
    assertTrue "[ $HAS_CONFLICT -eq 1 ]"

    rm -rf $HOME/otherlanguages
}
