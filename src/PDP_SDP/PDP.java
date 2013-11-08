package PDP_SDP;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import Helper.Parser;
import RbacGraph.RbacGraph;
import Structures.RBAC;


public abstract class PDP {
	/*public RBAC getG();//returns a RBAC data structure from PDP
	public void setG(RbacGraph g);//assign particular RbacGraph to PDP 
	public ArrayList getSDP();//returns list of all SPDs
	public void setSDP(SDP sdp);//assign particular SDP to PDP
	*/
	public static RbacGraph g;
	public ArrayList SDP;
	
	public PDP() {
		this.SDP = new ArrayList();
	}
	
	public RBAC getG() {
		return g;
	}

	public void setG(RbacGraph g) {
		this.g = g;
	}

	public ArrayList getSDP() {
		return SDP;
	}

	public void setSDP(SDP sdp) {
		SDP.add(sdp);
	}
	
	public void init(FileReader fReader) throws IOException{
		this.g = new RbacGraph();
		Parser p = new Parser();
		this.g.load(p, fReader);

	}
	
	public abstract SDP_Data_Structure request(Session s, String[] roles);
	public abstract SDP_Data_Structure delete(int session_id);
	
}
