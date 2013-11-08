package BloomFilter;

import java.util.ArrayList;
import java.util.Set;

import PDP_SDP.SDP_Data_Structure;
public class BFDecision implements SDP_Data_Structure{
	CascadeBloomFilter cbf;
	int cbftype;
	
	public CascadeBloomFilter getCbf() {
		return cbf;
	}
	public void setCbf(CascadeBloomFilter cbf) {
		this.cbf = cbf;
	}
	public int getCbftype() {
		return cbftype;
	}
	public void setCbftype(int cbftype) {
		this.cbftype = cbftype;
	}
	
}
