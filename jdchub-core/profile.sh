#!/bin/bash

#This script from http://habrahabr.ru/post/153135/

PID=$(top -n1 | grep -m1 java | perl -pe 's/\e\[?.*?[\@-~] ?//g' | cut -f1 -d' ')
NID=$(printf '%x' $(top -n1 -H | grep -m1 java | perl -pe 's/\e\[?.*?[\@-~] ?//g' | cut -f1 -d' '))
jstack $PID | grep -A500 $NID | grep -m1 '^$' -B 500

