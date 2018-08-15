#! /bin/sh
# file: s3mGitTests.sh

. ./functions/basicFunctions.sh

# Test to know if s3m semistructured merge is working and detecting conflicts
testSemistructuredMerge()
{
    cp -r exemplo $HOME/
    cp -r exemplotxt $HOME/
    cp -r exemplonodereordering $HOME/
    cd $HOME
    testProcedure "../exemplo"
    HAS_CONFLICT=$(git merge right | grep -c "CONFLICT")
    assertTrue "[ $HAS_CONFLICT -eq 1 ]"
    cd .. 
    rm -rf repo
}


#Test to know if s3m textual merge is working when the semistructured merge doesn't detects conflicts
testTextualMerge()
{
    testProcedure "../exemplotxt"
    IS_RECURSIVE=$(git merge right -m "test merge" | grep -c "recursive")
    assertTrue "[ $IS_RECURSIVE -eq 1 ]"
    cd .. 
    rm -rf repo
}

#Test to know if the s3m tool is not messing up git diff
testWorkingDiff()
{
    testProcedure "../exemplo"
    DIFF_WORKED=$(git diff left right | grep -c "@@ -1,5 +1,9 @@")
    assertTrue "[ $DIFF_WORKED -eq 1 ]"
    cd .. 
    rm -rf repo
}

# Test to know if invalid or malicious log files are crashing the tool
testCryptoIssueAvoidance() 
{   
    createLogDirectory "exemplo"
    testProcedure "../exemplo"
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
    testProcedure "../exemplonodereordering"
    git merge right
    INTERNAL_CLASSES_AND_INTERFACES=$(cat base.java | sed -n 's/.*[class|interface] \([^ \t\n]*\)/\1/p' | tr -d ' ' | tr -d '\n' | tr -d '{')
    assertEquals 'ABCI' "$INTERNAL_CLASSES_AND_INTERFACES"
    cd .. 
}

suite_addTest testSemistructuredMerge
suite_addTest testTextualMerge
suite_addTest testWorkingDiff
suite_addTest testNodeReordering
