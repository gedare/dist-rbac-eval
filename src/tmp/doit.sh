#!/bin/sh

java -classpath ${CLASSPATH}:.:* -Xms256M -Xmx256M -Djava.library.path=. Simulate.Simulate3 Data/rbac-state 1
java -classpath ${CLASSPATH}:.:* -Xms256M -Xmx256M -Djava.library.path=. Simulate.Simulate3 Data/rbac-state 3
java -classpath ${CLASSPATH}:.:* -Xms256M -Xmx256M -Djava.library.path=. Simulate.Simulate3 Data/rbac-state 6
#java -classpath ${CLASSPATH}:hwds.jar:gem5OpJni.jar:AccessMatrix.jar:Cpol.jar:Helper.jar:HWDSBitSet.jar:HWDS.jar:PDP_SDP.jar:RbacGraph.jar:Structures.jar -Xms256M -Xmx256M -Djava.library.path=. Simulate.Simulate3 Data/rbac-state 7
m5 exit
