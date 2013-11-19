package PDP_SDP;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import Helper.Parser;
import RbacGraph.RbacGraph;
import Structures.RBAC;
import Structures.Vertex;
import Structures.RoleVertex;
import Structures.UserVertex;

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
	
	public SDP_Data_Structure request(Session s, String[] roles)
  {
    UserVertex user = ((RbacGraph)g).FindUser(s.user_id);
    if (user == null ) {
      return null;
    }
    
    // Convert requested roles into RoleVertex types
    ArrayList<RoleVertex> Roles = new ArrayList();
    for ( int i = 0; i < roles.length; i++ ) {
      Roles.add(((RbacGraph)g).FindRole(roles[i]));
    }

    // Verify all requested roles are in the user's induced subgraph
    ArrayList<Vertex> userSubgraph =
      ((RbacGraph)g).getInducedGraph(new ArrayList<Vertex>(), (Vertex)user);
    if (!userSubgraph.containsAll(Roles)) {
      return null;
    }

    return new PDP_Response(Roles, userSubgraph);
  }
	public abstract SDP_Data_Structure delete(int session_id);
	
}
