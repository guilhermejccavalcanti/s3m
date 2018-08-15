#! /bin/sh
# file: s3mMultipleEncodingTests.sh

# Test the tool with multiple encodings.
testEncoding() {

	THISDIRECTORY=$PWD

	. ./functions/s3mEncodingTestFunction.sh
	testMultipleEncodings "$THISDIRECTORY/otherencodings/ASCII"
	testMultipleEncodings "$THISDIRECTORY/otherencodings/ISO1"
	testMultipleEncodings "$THISDIRECTORY/otherencodings/UTF-8"
	testMultipleEncodings "$THISDIRECTORY/otherencodings/UTF-16"
}

suite_addTest testEncoding
