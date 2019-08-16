#!/bin/bash
parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd $parent_path

cp .gitconfig $HOME
cp .gitattributes $HOME
cp ../../../../build/libs/jFSTMerge-all.jar $HOME/jFSTMerge.jar
