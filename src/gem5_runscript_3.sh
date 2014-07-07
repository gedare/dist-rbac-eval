#!/bin/sh

export PATH=/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.45.x86_64/bin:$PATH
export LD_LIBRARY_PATH=/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.45.x86_64/lib/amd64/jli
java -classpath ${CLASSPATH}:.:* -Xms256M -Xmx256M -Djava.library.path=. Simulate.Simulate3 Data/rbac-state 3

m5 exit

