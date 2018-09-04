#! /bin/sh
# file: s3mEncodingTests.sh

. ./functions/basicFunctions.sh

# Test to know if s3m semistructured merge is working with multiple encodings.
multipleEncodingsFunction()
{
    ENCODING_PATH=$1
    CONFLICT=$2	

    cp -r $START_PATH/otherencodings $HOME/
    cd $HOME
    createLogDirectory "$START_PATH/otherencodings"

    mergesProcedure "$ENCODING_PATH"
    HAS_CONFLICT=$(git merge right | grep -c "CONFLICT")
    cd ../.jfstmerge
    CRYPTO_WORKED=$(ls | grep -c "defect")
    
    assertTrue "[ $HAS_CONFLICT -eq $CONFLICT ]"
    assertTrue "[ $CRYPTO_WORKED -eq 1 ]"
    
    cd ..
    rm -rf .jfstmerge
    rm -rf $HOME/otherencodings
}
