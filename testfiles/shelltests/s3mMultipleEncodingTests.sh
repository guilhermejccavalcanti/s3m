#! /bin/sh
# file: s3mMultipleEncodingTests.sh
	
. ./functions/s3mEncodingTestFunction.sh

# Test the tool with multiple encodings.
testEncoding() {

	testMultipleEncodings "$START_PATH/otherencodings/ASCII" "1"
	testMultipleEncodings "$START_PATH/otherencodings/ISO1" "0"
	testMultipleEncodings "$START_PATH/otherencodings/UTF-8" "1"
	testMultipleEncodings "$START_PATH/otherencodings/UTF-16" "1"
}

suite_addTest testEncoding
