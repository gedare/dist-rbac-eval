package PDP_SDP;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

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
	
  // request() can be written to check if prepared != null and call prepare
	public abstract SDP_Data_Structure request(Session s, String[] roles,
      SDP_Data_Structure prepared);
	public SDP_Data_Structure prepare(String user_id, String[] roles)
  {
    UserVertex user = ((RbacGraph)g).FindUser(user_id);
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

    // for each role from Roles, get all the available permissions
    ArrayList current_permissions = new ArrayList();
    Iterator iterator = Roles.iterator();
    while (iterator.hasNext()){
      Vertex currentvertex = (Vertex) iterator.next();
      ArrayList alist = new ArrayList();
      ArrayList<Vertex> helperlist =
        ((RbacGraph)g).getInducedGraph(alist, currentvertex);
      RbacGraph helpergraph = new RbacGraph(helperlist);
      int[] permissions = helpergraph.get_permissions_IDs();
      for(int i = 0; i < permissions.length; i++){
        if(!current_permissions.contains(permissions[i]))
          current_permissions.add(permissions[i]);
      }
    }
    Collections.sort(current_permissions);

    return new PDP_Response(Roles, userSubgraph, current_permissions);
  }
	public abstract SDP_Data_Structure delete(int session_id);
	
}
