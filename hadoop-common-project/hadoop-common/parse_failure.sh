#!/bin/bash

FUZZ_OUTPUT_DIR=$1
RESULT_OUTPUT_FILE=$2
find ${FUZZ_OUTPUT_DIR}/fuzz-results/ -regex ".*/id.*" | grep failures | sort > ${RESULT_OUTPUT_FILE}
