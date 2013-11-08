MAIN_DIR=${DIST_RBAC_DIR}
CLASSPATH=.:${MAIN_DIR}
SOURCEPATH=${MAIN_DIR}

%.class:%.java
	javac -classpath ${CLASSPATH} -sourcepath ${SOURCEPATH} $<

all: PermissionVertex.class RbacGraphMatrix.class RBAC.class RoleVertex.class \
    UserVertex.class Vertex.class

clean:
	rm -rf *.class a.out core
