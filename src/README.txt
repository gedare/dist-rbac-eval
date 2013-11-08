Explanation of directories:
---------------------------

AccessMatrix/ : implementation of Access Matrix data structure

Data/ : example of rbac policies (configs) and session profiles that can be used an input
        against the algorithms. Simulate.Simulate3 is an example of a program that takes
	these as input. NOTE: Simulate.Simulate3 expects the session profile to be in
	Data/instructions. But the rbac file is provided as a parameter. See run_1.expts
	for an example of invoking Simulate.Simulate3. 

	It is customary to change the number of iterations in Simulate.Simulate3 to whatever
	values one wants (e.g., 10). So many entries are generated in Data/timinginfo. Then,
	we run Simulate.Measurements . This expects an empty Data/means files to exist
	in Data/ (see run_1.expts). 

	Simulate.Measurements measures the steady-state values. We choose k = 4. (See the
	discussion in the companion paper on our methodology.)

PDP_SDP/ : java code for the PDP and SDP

Simulate/ : Files to run simulations of access checks. See discussion under Data/ above, and
            run_1.expts for an example of using programs in Simulate.

BloomFilter/ : Implementation of the SDP and PDP for the (Cascade) Bloom filter

Generator/ : Programs to generate RBAC configs (policies) and session profiles. See the pdf
             in that directory for the parameters.

RbacGraph/ : Implementation of a directed graph to represent an RBAC state

Structures/ : Basic data structures (e.g., Vertex) to support more complex data structures.

Cpol/ : Our reimplementation of CPOL.

Helper/ : Some helper programs.

Recycling/ : Our reimplementation of authorization recycling
