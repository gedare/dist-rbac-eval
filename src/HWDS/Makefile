MAIN_DIR:=${DIST_RBAC_DIR}
CLASSPATH:=.:${MAIN_DIR}
SOURCEPATH:=${MAIN_DIR}

M5OP=/home/gedare/work/gem5/gem5/util/m5
PSEUDO=/home/gedare/work/research/hwds/gem5/pseudo_insn

CLASSPATH:=$(CLASSPATH):$(M5OP):$(PSEUDO)

all: PDP_HWDS.class SDP_HWDS.class SDP_Response.class CountedBitSet.class

%.class:%.java $(M5OP)/gem5OpJni.jar $(PSEUDO)/hwds.jar
	javac -classpath $(CLASSPATH) -sourcepath $(SOURCEPATH) $<

clean:
	rm -rf *.class a.out core
