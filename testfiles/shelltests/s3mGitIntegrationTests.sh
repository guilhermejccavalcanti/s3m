#!/bin/sh
# file: s3mTests.sh

workDir=$(pwd)

setUp() {
    mv $HOME/.jfstmerge $HOME/.jfstmerge1

    tmpDir=$(mktemp -d 2>/dev/null || mktemp -d -t 'tmpDir')
    cd ${tmpDir}
    git init
}

tearDown() {
    cd ${workDir}
    cleanup
}

cleanup() {
    rm -rf $HOME/.jfstmerge
    mv -f $HOME/.jfstmerge1 $HOME/.jfstmerge
    rm -rf ${tmpDir}
}

### GIT INTEGRATION TESTS ###
testWellFunctioningOfSemistructuredMerge() {
    local mergeResult=$(initBranchesAndMerge "${workDir}/semistructuredmerge")
    local numConflicts=$(countWord "${mergeResult}" "CONFLICT")

    assertEquals 1 ${numConflicts}
}

testFailingOfSemistructuredMerge() {
    local mergeResult=$(initBranchesAndMerge "${workDir}/semistructuredmerge-invalidfiles")
    local numConflicts=$(countWord "${mergeResult}" "CONFLICT")

    assertNotEquals 1 ${numConflicts}
}

testNoConflictingFiles() {
    local mergeResult=$(initBranchesAndMerge "${workDir}/noconflicts")
    local numConflicts=$(countWord "${mergeResult}" "CONFLICT")
    local recursiveMergeHasBeenCalled=$(countWord "${mergeResult}" "recursive")

    assertEquals 0 ${numConflicts}
    assertEquals 1 ${recursiveMergeHasBeenCalled}
}

testWellFunctioningOfGitDiffAfterMerge() {
    local mergeResult=$(initBranchesAndMerge "${workDir}/semistructuredmerge")
    local diffResult=$(git diff left right | grep -c "@@ -1,5 +1,9 @@")

    assertEquals 1 ${diffResult}
}

testWellFunctioningOfCryptography() {
    local mergeResult=$(initBranchesAndMerge "${workDir}/semistructuredmerge")
    cd $HOME/.jfstmerge
    local numDefectuousFiles=$(ls | grep -c "defect")

    assertEquals 0 ${numDefectuousFiles}
}

testNodeReordering() {
    local mergeResult=$(initBranchesAndMerge "${workDir}/nodereordering")
    local classesAndInterfaces=$(cat Test.java | sed -n 's/.*[class|interface] \([^ \t\n]*\)/\1/p' | tr -d ' ' | tr -d '\n' | tr -d '{')

    assertEquals 'ABCI' ${classesAndInterfaces}
}
##############################

### BIG REPOSITORY TESTS ###
testWellFunctioningOfSemistructuredMergeAgainstBigRepository() {
    local mergeResult=$(initBranchesAndMerge "${workDir}/big")
    local numConflicts=$(countWord "${mergeResult}" "CONFLICT")
    local numFileMerges=$(countWord "${mergeResult}" "finished")

    assertEquals 1 ${numConflicts}
    assertEquals 3 ${numFileMerges}
}

testFailingOfSemistructuredMergeAgainstBigRepository() {
    local mergeResult=$(initBranchesAndMerge "${workDir}/bigcorrupted")
    local numConflicts=$(countWord "${mergeResult}" "CONFLICT")
    local numFileMerges=$(countWord "${mergeResult}" "finished")

    assertEquals 1 ${numConflicts}
    assertEquals 1 ${numFileMerges}
}

testLogCorrectness() {
    local mergeResult=$(initBranchesAndMerge "${workDir}/big")

    local numJavaFiles=$(countStat "[0-9]+JAVAfiles" "[0-9]")
    assertEquals 'NumJavaFiles' '3' "${numJavaFiles}"

    local numAvoidedFP=$(countStat "least[0-9]+falsepositive\(s\)" "[0-9]")
    assertEquals 'NumAvoidedFP' '2' "${numAvoidedFP}"

    local numAvoidedFN=$(countStat "[0-9]+falsenegative\(s\)" "[0-9]")
    assertEquals 'NumAvoidedFN' '1' ${numAvoidedFN}

    local numS3MConflicts=$(countStat "reported[0-9]+conflicts" "[0-9]")
    assertEquals 'NumS3MConflicts' '1' "${numS3MConflicts}"

    local numS3MConflictingLOC=$(countStat "totaling[0-9]+conflicting" "[0-9]")
    assertEquals 'NumS3MConflictingLOC' '2' "${numS3MConflictingLOC}"

    local numTextualConflicts=$(countStat "to[0-9]+conflicts" "[0-9]")
    assertEquals 'NumTextualConflicts' '2' "${numTextualConflicts}"

    local numTextualConflictingLOC=$(countStat "and[0-9]+conflicting" "[0-9]")
    assertEquals 'NumTextualConflictingLOC' '5' "${numTextualConflictingLOC}"

    local fpReductionPercentage=$(countStat "Areductionof[0-9]+[.,][0-9]+%" "[0-9]+[.,][0-9]+%")
    assertTrue 'fpReductionPercentage' "[ '${fpReductionPercentage}' = '100.00%' ] || [ '${fpReductionPercentage}' = '100,00%' ]"

    local fnReductionPercentage=$(countStat "Andareductionof[0-9]+[.,][0-9]+%" "[0-9]+[.,][0-9]+%")
    assertTrue 'fnReductionPercentage' "[ '${fnReductionPercentage}' = '100.00%' ] || [ '${fnReductionPercentage}' = '100,00%' ]"
}
##############################

### ENCODING TESTS ###
testMergeWithASCIIEncoding() {
    local mergeResult=$(initBranchesAndMerge "${workDir}/otherencodings/ASCII")
    local numConflicts=$(countWord "${mergeResult}" "CONFLICT")

    assertEquals 1 ${numConflicts}
}

testMergeWithISOEncoding() {
    local mergeResult=$(initBranchesAndMerge "${workDir}/otherencodings/ISO1")
    local numConflicts=$(countWord "${mergeResult}" "CONFLICT")

    assertEquals 1 ${numConflicts}
}

testMergeWithUTF8WithBOMEncoding() {
    local mergeResult=$(initBranchesAndMerge "${workDir}/otherencodings/UTF-8")
    local numConflicts=$(countWord "${mergeResult}" "CONFLICT")

    assertEquals 1 ${numConflicts}
}

testMergeWithUTF16Encoding() {
    local mergeResult=$(initBranchesAndMerge "${workDir}/otherencodings/UTF-16")
    local numConflicts=$(countWord "${mergeResult}" "CONFLICT")

    assertEquals 1 ${numConflicts}
}

testMergeWithFilesHavingDifferentEncodings() {
    local mergeResult=$(initBranchesAndMerge "${workDir}/otherencodings/different")
    local numConflicts=$(countWord "${mergeResult}" "CONFLICT")

    assertEquals 1 ${numConflicts}
}
##############################

countWord() {
    local text=${1}
    local word=${2}
    
    local numAppearances=$(echo ${text} | grep -o -w ${word} | wc -w)
    echo ${numAppearances}
}

countStat() {
    local regex1=${1}
    local regex2=${2}

    local numAppearances=$(cat $HOME/.jfstmerge/jfstmerge.summary | tr -d "[:blank:]" | grep -Eo ${regex1} | grep -Eo ${regex2})
    
    echo ${numAppearances}
}

initBranchesAndMerge() {
    local testFilesPath=${1}

    commitTestFile ${testFilesPath}/base "base"
    git checkout -b left
    commitTestFile ${testFilesPath}/left "left"
    git checkout master
    git checkout -b right
    commitTestFile ${testFilesPath}/right "right"
    git checkout master

    git merge left --no-edit
    git merge right --no-edit
}

commitTestFile() {
    local testFilePath=${1}
    local commitMessage=${2}

    cp -r ${testFilePath}/* .
    git add .
    git commit -m ${commitMessage}
}

# register the cleanup function to be called on the EXIT signal
trap cleanup EXIT

# load and run shunit2
. dependencies/shunit2-2.1.6/src/shunit2