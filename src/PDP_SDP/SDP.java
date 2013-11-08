package PDP_SDP;

import java.util.ArrayList;

import Structures.RBAC;


public abstract class SDP {
	
	public ArrayList<Session> sessions;
	protected static int sdp_id;
	public int id;
	//instead of RBAC could be SDP_Data_Structure
	public SDP_Data_Structure g; //RBAC (graph or matrix) that is returned after request
	public PDP pdp;

	public SDP(PDP pdp) {
		this.setPdp(pdp);
		this.id = sdp_id;
		this.sdp_id++;
		this.sessions = new ArrayList();
	}
	
	public int getId() {
		return id;
	}

	public SDP_Data_Structure getG() {
		return g;
	}

	public void setG(SDP_Data_Structure g) {
		this.g = g;
	}

	public PDP getPdp() {
		return pdp;
	}

	public void setPdp(PDP pdp) {
		this.pdp = pdp;
	}

	public abstract void destroy_session(int session_id);
	public abstract int initiate_session_request(String user_id, String[] roles);
	public abstract boolean access_request(int session_id, int permission_id);
}
