#! /bin/sh
# file: s3mMultipleFileTypesTests.sh

. ./functions/s3mMultipleLanguagesFunctions.sh

# Test the tool with C++ files.
testCPP() 
{
	multipleLanguages "$START_PATH/otherlanguages" "cpp"
}

# Test the tool with Typescript files.
testTS() 
{
	multipleLanguages "$START_PATH/otherlanguages" "ts"
}

suite_addTest testCPP
suite_addTest testTS
