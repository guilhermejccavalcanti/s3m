#! /bin/sh
# file: s3mMultipleFileTypesTests.sh

. ./s3mMultipleLanguagesFunctions.sh
THISDIRECTORY=$PWD

# Test the tool with C++ files.
testCPP() {

	multipleLanguages "$THISDIRECTORY/cppsupport" ".cpp"
}

# Test the tool with Typescript files.
testTS() {

	multipleLanguages "$THISDIRECTORY/typescriptsupport" ".ts"
}
