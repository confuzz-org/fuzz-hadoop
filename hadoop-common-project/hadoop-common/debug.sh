#!/bin/bash

for file in $(find . -name "input*"); do python3 configGen.py $file outputs/; done