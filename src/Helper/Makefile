MAIN_DIR=${DIST_RBAC_DIR}
CLASSPATH=.:${MAIN_DIR}
SOURCEPATH=${MAIN_DIR}

%.class:%.java
	javac -classpath ${CLASSPATH} -sourcepath ${SOURCEPATH} $<

all: Parser.class

clean:
	rm -rf *.class a.out core
