package Recycling;

import java.io.IOException;
import java.util.ArrayList;

import PDP_SDP.PDP;
import PDP_SDP.SDP_Data_Structure;
import PDP_SDP.Session;
import RbacGraph.RbacGraph;
import Structures.RoleVertex;


public class PDP_Recycling extends PDP{

	public SDP_Data_Structure request(Session s, String[] roles) {
		int permissionid = Integer.parseInt(roles[roles.length-1]);
		boolean avail = false;
		for(int i = 0; i < roles.length-1; i++) {
			if(Pair(roles[i], permissionid)) {
				avail = true;
				break;
			}
		}
		SDP_Data_Structure sd = new Decision();
		((Decision)sd).setDecision(avail);
		return sd;
	}
	public boolean Pair(String a, int b) {
		
		RoleVertex r = this.g.FindRole(a);
		
		if(r != null){
			//get induced sub graph of that particular user
			ArrayList arraylist = new ArrayList();
			arraylist = this.g.getInducedGraph(arraylist, r);
			RbacGraph temp = new RbacGraph(arraylist);
			//get all permissions' id from that sub graph
			int[] permissions = temp.get_permissions_IDs();
			//go thru array to compare and see if there is such (from input)permission that user is assigned to
			for(int i = 0; i < permissions.length; i++){
				if (permissions[i] == b) return true;
			}
		}
		//find vertex with specific int session_id
		//get its induced graph
		//check in that graph if there is such permission with same permission_id
		//use method get_permissions_IDs() and then check for specific permission_id
		return false;
	}
	@Override
	public SDP_Data_Structure delete(int session_id) {
		// TODO Auto-generated method stub
		return null;
	}

}
