#!/bin/bash

OUTPUT_FILE=$1
find fuzz_output/fuzz-results/ -regex ".*/id.*" | grep failures | sort > ${OUTPUT_FILE}
