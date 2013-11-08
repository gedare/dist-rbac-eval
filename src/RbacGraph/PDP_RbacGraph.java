package RbacGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import PDP_SDP.PDP;
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
	
	public SDP_Data_Structure request(Session s, String[] roles) {
		RbacGraph graph_response = new RbacGraph(this.requested);
		String ses_id = Integer.toString(s.id);
		UserVertex ses = new UserVertex(ses_id);//creates new UserVertex with session_id as ID which will be returned to SDP
		UserVertex current = this.g.FindUser(s.user_id);
		//check if there is such a user in PDP's graph
		if(current != null){
			//gets that user's sub graph
			ArrayList arraylist = new ArrayList();
			arraylist = this.g.getInducedGraph(arraylist, current);

			/*
			System.out.print("Request(), arraylist: ");
			for(Iterator j = arraylist.iterator();
				j.hasNext(); ) {

			    Vertex v = (Vertex)(j.next());
			    System.out.print(v.getStringID()+", ");
			}
			System.out.println("");
			/* */

			RbacGraph temp = new RbacGraph(arraylist);
			//go thru input roles and check them if they are assigned to specific user
			for(int i = 0; i < roles.length; i++){
				RoleVertex current_role = temp.FindRole(roles[i]);
				if(current_role != null){
					//if there is such role get its sub graph
					ArrayList a = new ArrayList();
					a = temp.getInducedGraph(a, current_role);

					/*
					System.out.print("Request(), a: ");
					for(Iterator j = a.iterator();
						j.hasNext(); ) {

					    Vertex v = (Vertex)(j.next());
					    System.out.print(v.getStringID()+", ");
					}
					System.out.println("");
					/* */

					//add connection between session vertex and current role in response graph
					graph_response.AddUA(ses_id, current_role);
					/* NOTE: serious abuse!
					 * current_role and its descendants
					 * end up in multiple graphs */

					//go thru list of vertices of role's induced graph
					//add each, if not contained, in response graph
					/* */
					Iterator iterator = a.iterator();
					while(iterator.hasNext()){
						Vertex v = (Vertex) iterator.next();
						if(!graph_response.vertices.contains(v)) {
						    graph_response.vertices.add(v);
						    /*
						    System.out.println("Request(): added "+v.getStringID());
						    /* */
						}
					}
					/* */
				}
				else return null;//not assigned to one of the roles?! return null
			}
			//update requested list
			requested = graph_response.vertices;
			//return updated graph to SDP
			return graph_response;
		}
		return null;
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
