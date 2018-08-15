#! /bin/sh
# file: s3mMultipleFileTypesTests.sh

. ./functions/s3mMultipleLanguagesFunctions.sh
THISDIRECTORY=$PWD

# Test the tool with C++ files.
testCPP() {

	multipleLanguages "$THISDIRECTORY/otherlanguages" ".cpp"
}

# Test the tool with Typescript files.
testTS() {

	multipleLanguages "$THISDIRECTORY/otherlanguages" ".ts"
}

suite_addTest testCPP
suite_addTest testTS
