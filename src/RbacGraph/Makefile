MAIN_DIR=${DIST_RBAC_DIR}
CLASSPATH=.:${MAIN_DIR}
SOURCEPATH=${MAIN_DIR}

%.class:%.java
	javac -classpath ${CLASSPATH} -sourcepath ${SOURCEPATH} $<

all: PDP_RbacGraph.class RbacGraph.class RecordData.class SDP_RbacGraph.class

clean:
	rm -rf *.class a.out core
