#!/bin/sh
# file: s3mTests.sh

suite() {
	. ./s3mMultipleEncodingTests.sh
	. ./s3mMultipleLanguagesTests.sh
	. ./s3mGitTests.sh
	. ./s3mBigRepositoryTests.sh
}
