MAIN_DIR=${DIST_RBAC_DIR}
CLASSPATH=.:${MAIN_DIR}
SOURCEPATH=${MAIN_DIR}

all: PDP_MapBitSet.class SDP_MapBitSet.class SDP_Response.class

%.class:%.java
	javac -classpath ${CLASSPATH} -sourcepath ${SOURCEPATH} $<

clean:
	rm -rf *.class a.out core
