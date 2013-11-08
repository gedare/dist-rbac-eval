MAIN_DIR=${DIST_RBAC_DIR}
CLASSPATH=.:${MAIN_DIR}
SOURCEPATH=${MAIN_DIR}

%.class:%.java
	javac -classpath ${CLASSPATH} -sourcepath ${SOURCEPATH} $<

all: AccessToken.class CacheEntry.class Cache.class CacheKey.class PDP_Cpol.class \
    Rule.class SDP_Cpol.class Session_Cpol.class

clean:
	rm -rf *.class a.out core
