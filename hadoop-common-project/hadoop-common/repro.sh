#!/bin/bash

FUZZ_OUTPUT_DIR=$1
FAILURE_FILE=$2
REPRODUCE_DIR=$3
./parse_failure.sh ${FUZZ_OUTPUT_DIR} ${FAILURE_FILE}
python3 repro.py ${FAILURE_FILE} ${REPRODUCE_DIR}

