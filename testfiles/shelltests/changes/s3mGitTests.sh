#! /bin/sh
# file: s3mGitTests.sh

. ./functions/basicFunctions.sh

# Test to know if s3m semistructured merge is working and detecting conflicts
testSemistructuredMerge()
{
    cp -r $START_PATH/exemplo $HOME/
    cp -r $START_PATH/exemplotxt $HOME/
    cp -r $START_PATH/exemplonodereordering $HOME/
   
    mergesProcedure "$START_PATH/exemplo"
    HAS_CONFLICT=$(git merge right | grep -c "CONFLICT")

    assertTrue "[ $HAS_CONFLICT -eq 1 ]"
    
    rm -rf $HOME/exemplo
    rm -rf $HOME/exemplotxt
    rm -rf $HOME/exemplonodereordering
}

#Test to know if s3m textual merge is working when the semistructured merge doesn't detects conflicts
testTextualMerge()
{
    mergesProcedure "$START_PATH/exemplotxt"
    IS_RECURSIVE=$(git merge right -m "test merge" | grep -c "recursive")
    
    assertTrue "[ $IS_RECURSIVE -eq 1 ]"
}

#Test to know if the s3m tool is not messing up git diff
testWorkingDiff()
{
    mergesProcedure "$START_PATH/exemplo"
    DIFF_WORKED=$(git diff left right | grep -c "@@ -1,5 +1,9 @@")
    
    assertTrue "[ $DIFF_WORKED -eq 1 ]"
}

# Test to know if invalid or malicious log files are crashing the tool
testCryptoIssueAvoidance() 
{   
    createLogDirectory "$START_PATH/exemplo"
    
    mergesProcedure "$START_PATH/exemplo"
    git merge right
    cd $HOME/.jfstmerge
    CRYPTO_WORKED=$(ls | grep -c "defect")
    
    assertTrue "[ $CRYPTO_WORKED -eq 1 ]"
}

#Test to know if reordering of nodes in superimposition, putting neighbours together, works
testNodeReordering()
{
    mergesProcedure "$START_PATH/exemplonodereordering"
    git merge right --no-edit
    INTERNAL_CLASSES_AND_INTERFACES=$(cat base.java | sed -n 's/.*[class|interface] \([^ \t\n]*\)/\1/p' | tr -d ' ' | tr -d '\n' | tr -d '{')
    
    assertEquals 'ABCI' "$INTERNAL_CLASSES_AND_INTERFACES"
}

suite_addTest testSemistructuredMerge
suite_addTest testTextualMerge
suite_addTest testWorkingDiff
suite_addTest testCryptoIssueAvoidance
suite_addTest testNodeReordering
