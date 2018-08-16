#! /bin/sh
# file: s3mMultipleEncodingTests.sh
	
. ./functions/s3mEncodingTestFunction.sh

# Test the tool with multiple encodings.
testEncoding() {

	THISDIRECTORY=$PWD

	testMultipleEncodings "$THISDIRECTORY/otherencodings/ASCII" "1"
	testMultipleEncodings "$THISDIRECTORY/otherencodings/ISO1" "0"
	testMultipleEncodings "$THISDIRECTORY/otherencodings/UTF-8" "1"
	testMultipleEncodings "$THISDIRECTORY/otherencodings/UTF-16" "1"
}

suite_addTest testEncoding
