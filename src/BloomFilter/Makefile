MAIN_DIR=${DIST_RBAC_DIR}
CLASSPATH=.:${MAIN_DIR}
SOURCEPATH=${MAIN_DIR}

%.class:%.java
	javac -classpath ${CLASSPATH} -sourcepath ${SOURCEPATH} $<

all: BFDecision.class CascadeBloomFilter.class CountingBloomFilter.class PDP_Bloom.class \
    SDP_Bloom.class

clean:
	rm -rf *.class a.out core
