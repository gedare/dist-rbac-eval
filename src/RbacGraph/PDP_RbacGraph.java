package RbacGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import PDP_SDP.PDP;
import PDP_SDP.PDP_Response;
import PDP_SDP.SDP_Data_Structure;
import PDP_SDP.Session;
import Structures.RoleVertex;
import Structures.UserVertex;
import Structures.Vertex;


public class PDP_RbacGraph extends PDP {

	public ArrayList<Vertex> requested;
	
	public PDP_RbacGraph(){
		super();
		this.requested = new ArrayList();
	}
	
	public SDP_Data_Structure request(Session s, String[] roles,
      SDP_Data_Structure prepared) {

    PDP_Response P = (PDP_Response) prepared;
    if ( P == null )
      return null;
    ArrayList<RoleVertex> Roles = P.getRoles();

		RbacGraph graph_response = new RbacGraph(this.requested);
		String ses_id = Integer.toString(s.id);

    /* FIXME: Why not just get the induced subgraph of the requested roles
     * and add a user vertex to connect to each of them? */
    Iterator iterator = Roles.iterator();
    while (iterator.hasNext()) {
      RoleVertex currentvertex = (RoleVertex) iterator.next();
      ArrayList<Vertex> a =
        this.g.getInducedGraph(new ArrayList(), currentvertex);

      // add connection between session and role in response graph
      /* NOTE: serious abuse!
       * current_role and its descendants
       * end up in multiple graphs */
      graph_response.AddUA(ses_id, currentvertex);

      //go thru list of vertices of role's induced graph
      //add each, if not contained, in response graph
      Iterator iterator2 = a.iterator();
      while(iterator2.hasNext()){
        Vertex v = (Vertex) iterator2.next();
        if(!graph_response.vertices.contains(v)) {
            graph_response.vertices.add(v);
        }
      }
    }

    //update requested list
    requested = graph_response.vertices;
    return graph_response;
	}

	@Override
	public SDP_Data_Structure delete(int session_id) {
		ArrayList<Vertex> helper = requested;
		RbacGraph helper1 = new RbacGraph();
		//go thru helper and create a new subgraph
		RbacGraph temp = new RbacGraph(helper);
		Iterator iterator = helper.iterator();
		while(iterator.hasNext()){
			Vertex v = (Vertex) iterator.next();
			if (v instanceof UserVertex) {
				if (((UserVertex)v).getID() != session_id){
					//put every user vertex but one with session_id in helper1 graph
					helper1.AddUser((UserVertex) v);
					ArrayList a = new ArrayList();
					//get v's inducted graph, v is current user vertex
					a = temp.getInducedGraph(a, v);
					Iterator it = a.iterator();
					//go thru induced graph's vertices and if not already in helper1, add them
					while(iterator.hasNext()){
						Vertex b = (Vertex) iterator.next();
						if(!helper1.vertices.contains(b)) helper1.vertices.add(b);
					}
				}
			}
			//now in graph helper1 we have merged all induced graphs of all 
			//sessions' but the one with session_id specified
		}
		requested = helper1.vertices;
		//if difference in number of vertices is only 1
		//this means that all we had to do is delete only session vertex with session_id
		if (helper1.size() + 1 == requested.size()) return null;
		//should I return helper1??? or SDP could remove it itself
		else return helper1;
	}

}
