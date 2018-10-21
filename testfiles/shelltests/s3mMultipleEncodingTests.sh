#! /bin/sh
# file: s3mMultipleEncodingTests.sh
	
. ./functions/s3mEncodingTestFunction.sh

# Test the tool with multiple encodings: ASCII, ISO, UTF-8 and UTF-16.
testASCIIEncoding() 
{
	multipleEncodingsFunction "$START_PATH/otherencodings/ASCII" "1"
}

testISOEncoding() 
{
	multipleEncodingsFunction "$START_PATH/otherencodings/ISO1" "1"
}

testUTF8Encoding() 
{
	multipleEncodingsFunction "$START_PATH/otherencodings/UTF-8" "1"
}

testUTF16Encoding() 
{
	multipleEncodingsFunction "$START_PATH/otherencodings/UTF-16" "1"
}

suite_addTest testASCIIEncoding
suite_addTest testISOEncoding
suite_addTest testUTF8Encoding
suite_addTest testUTF16Encoding
