MAIN_DIR=${DIST_RBAC_DIR}
CLASSPATH=.:${MAIN_DIR}
SOURCEPATH=${MAIN_DIR}

%.class:%.java
	javac -classpath ${CLASSPATH} -sourcepath ${SOURCEPATH} $<

all: Decision.class PDP_Recycling.class SDP_Recycling.class

clean:
	rm -rf *.class a.out core
