#!/bin/bash

./parse_failure.sh failure.out
python3 repro.py failure.out repro_fuzz_failures

