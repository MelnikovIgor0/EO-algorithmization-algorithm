#!/bin/bash

START=$(date +%s%N)
/bin/bash go_python.sh $1
END=$(date +%s%N)

DIFF=$(($END - $START))
echo "Python:      $DIFF nanoseconds"

START=$(date +%s%N)
/bin/bash go_transformed.sh $1
END=$(date +%s%N)

DIFF=$(($END - $START))
echo "Transformed: $DIFF nanoseconds"
sleep 5