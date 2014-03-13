MAIN_DIR=${DIST_RBAC_DIR}
CLASSPATH=.:${MAIN_DIR}
SOURCEPATH=${MAIN_DIR}

M5OP=/home/gedare/work/gem5/gem5/util/m5
CLASSPATH:=$(CLASSPATH):$(M5OP)

%.class:%.java
	javac -classpath ${CLASSPATH} -sourcepath ${SOURCEPATH} $<

all: Measurements.class Simulate3.class

clean:
	rm -rf *.class a.out core
