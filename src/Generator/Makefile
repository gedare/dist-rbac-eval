MAIN_DIR=${DIST_RBAC_DIR}
CLASSPATH=.:${MAIN_DIR}
SOURCEPATH=${MAIN_DIR}

%.class:%.java
	javac -classpath ${CLASSPATH} -sourcepath ${SOURCEPATH} $<

all: Generator_1.class Generator_2.class Generator_3.class Generator.class Item.class RBAConfiguration.class SessionProfile.class SET.class NewSessionProfile.class NewSessionProfile2.class ZipfGenerator.class

clean:
	rm -rf *.class a.out core
