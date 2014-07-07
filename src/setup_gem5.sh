#!/bin/sh
. setenv
m5 checkpoint
m5 readfile > Data.tar
tar -xf Data.tar
./doit.sh
m5 exit

