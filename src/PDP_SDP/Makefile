MAIN_DIR=${DIST_RBAC_DIR}
CLASSPATH=.:${MAIN_DIR}
SOURCEPATH=${MAIN_DIR}

%.class:%.java
	javac -classpath ${CLASSPATH} -sourcepath ${SOURCEPATH} $<

all: PDP.class SDP_Data_Structure.class SDP.class Session.class

clean:
	rm -rf *.class a.out core
