#!/bin/bash
INPUT_DIR=$1
OUTPUT_DIR=$2
for file in $(find ${INPUT_DIR} -name "input*"); do python3 configGen.py $file ${OUTPUT_DIR}; done
