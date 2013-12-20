# Requires env variable DIST_RBAC_DIR

MAIN_DIR=${DIST_RBAC_DIR}
CLASSPATH=.:${MAIN_DIR}
SOURCEPATH=${MAIN_DIR}

all:
	$(MAKE) all -C AccessMatrix
	$(MAKE) all -C BloomFilter
	$(MAKE) all -C Cpol
	$(MAKE) all -C Generator
	$(MAKE) all -C Helper
	$(MAKE) all -C HWDS
	$(MAKE) all -C HWDSBitSet
	$(MAKE) all -C MapBitSet
	$(MAKE) all -C PDP_SDP
	$(MAKE) all -C RbacGraph
	$(MAKE) all -C Recycling
	$(MAKE) all -C Simulate
	$(MAKE) all -C Structures

jar: all
	jar cvf AccessMatrix.jar AccessMatrix/*.class
	jar cvf BloomFilter.jar BloomFilter/*.class
	jar cvf Cpol.jar Cpol/*.class
	jar cvf Helper.jar Helper/*.class
	jar cvf HWDS.jar HWDS/*.class
	jar cvf HWDSBitSet.jar HWDSBitSet/*.class
	jar cvf MapBitSet.jar MapBitSet/*.class
	jar cvf PDP_SDP.jar PDP_SDP/*.class
	jar cvf RbacGraph.jar RbacGraph/*.class
	jar cvf Recycling.jar Recycling/*.class
	jar cvf Simulate.jar Simulate/*.class
	jar cvf Structures.jar Structures/*.class

rungen:
	java -cp ${CLASSPATH} Generator/Generator

runsim:
	java -cp ${CLASSPATH} Simulate/Simulate

clean:
	$(MAKE) clean -C AccessMatrix
	$(MAKE) clean -C BloomFilter
	$(MAKE) clean -C Cpol
	$(MAKE) clean -C Generator
	$(MAKE) clean -C Helper
	$(MAKE) clean -C HWDS
	$(MAKE) clean -C HWDSBitSet
	$(MAKE) clean -C MapBitSet
	$(MAKE) clean -C PDP_SDP
	$(MAKE) clean -C RbacGraph
	$(MAKE) clean -C Recycling
	$(MAKE) clean -C Simulate
	$(MAKE) clean -C Structures
