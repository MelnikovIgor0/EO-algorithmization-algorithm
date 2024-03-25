#!/bin/bash

START=$(date +%s%N)
/bin/bash go.sh
END=$(date +%s%N)

DIFF=$(($END - $START))
echo "It took $DIFF nanoseconds"
sleep 5