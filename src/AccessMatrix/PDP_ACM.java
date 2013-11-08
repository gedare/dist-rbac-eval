package AccessMatrix;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import PDP_SDP.PDP;
import PDP_SDP.SDP_Data_Structure;
import PDP_SDP.Session;
import RbacGraph.RbacGraph;
import RbacGraph.RecordData;
import Structures.RoleVertex;
import Structures.UserVertex;
import Structures.Vertex;


public class PDP_ACM extends PDP {
	
	public ArrayList<RecordData> record;//table of all session permissions
	public ArrayList Sessions; //sessions' id that have made a request
	public ArrayList Permissions; //permissions' id that have been requested to be accessed to
	
	

	public PDP_ACM() {
		super();
		this.Permissions = new ArrayList();
		this.Sessions = new ArrayList();
		this.record = new ArrayList();
	}
	
	public SDP_Data_Structure request(Session s, String[] roles){
		ArrayList current_permissions = new ArrayList();//returning arraylist of permission for particular user and his asked roles
		ArrayList<RoleVertex> Roles = new ArrayList();
		//adding roles to arraylist of RoleVertices
		for(int i = 0; i < roles.length; i++){
			Roles.add(((RbacGraph)g).FindRole(roles[i]));
		}
		
		UserVertex user = ((RbacGraph)g).FindUser(s.user_id);
		//check if there is such a user
		if(user != null){
			ArrayList arraylist = new ArrayList();
			arraylist = ((RbacGraph)g).getInducedGraph(arraylist, (Vertex)user);//get induced graph for particular user
						
			//check if every role requested, is contained in arraylist of users sub graph
			if(arraylist.containsAll(Roles)) {
				Iterator iterator = Roles.iterator();
				while (iterator.hasNext()){
					Vertex currentvertex = (Vertex) iterator.next();
					//for each role from Roles, get induced graph, so we can get all its permissions id
					ArrayList alist = new ArrayList();
					ArrayList<Vertex> helperlist = ((RbacGraph)g).getInducedGraph(alist, currentvertex);
					RbacGraph helpergraph = new RbacGraph(helperlist);
					int[] permissions = helpergraph.get_permissions_IDs();
					//go thru all permissions, if it is not contained in Permissions ADD it
					//just makes sure no double permissions are there
					for(int i = 0; i < permissions.length; i++){
						if(!Permissions.contains(permissions[i]))
							Permissions.add(permissions[i]);//adds to ALL permissions ever assigned by any session
						if(!current_permissions.contains(permissions[i]))
							current_permissions.add(permissions[i]);//adds to permissions assigned by this session
					}
				
				}
				
				record.add(new RecordData(s.id, current_permissions));
				Sessions.add(s.id);
				//now we have finalised the process of updating sessions and permissions and RecordData on PDP
				
				//part where RBAC is being created(updated) out of the RecordData
				
				/* REPLACED WITH matrix_create METHOD
				//find largest of all permissions and sessions that will be the matrix dimensions
				Object[] helper = Permissions.toArray();
				int top = 0;
				for(int i = 0; i < helper.length; i++){
					//int curr = Integer.parseInt((String) helper[i]);
					int curr = (Integer) helper[i];
					if (top < curr) top = curr;
				}
				int per_num = top + 1;//subtract top with 1, cause largest could be 2,
				//but actually there are permissions 0, 1, 2, and that's 3 of them
				
				top = 0;//reset top to 0 value again
				Object[] helper1 = Sessions.toArray();//reset helper to be now produced from Sessions
				for(int i = 0; i < helper1.length; i++){
					int curr = (Integer) helper1[i];
					if (top < curr) top = curr;
				}
				int ses_num = top + 1;
				//constructs empty RbacMatrix with dimensions of largest session id and permission id
				//lots of field might not be used - space trade of
				SDP_Data_Structure graph_response = new RbacMatrix(ses_num, per_num);
				
				//part where we go thru RecordData to pickup pairs of 
				//session and permission to fill out the matrix
				Iterator iterator1 = record.iterator();
				while(iterator1.hasNext()){
					RecordData r = (RecordData) iterator1.next();
					//go thru permissions of current record to collect permissions' id
					Iterator it = r.permissions.iterator();
					String ses_id = Integer.toString(r.session);
					while(it.hasNext()){
						int p_id = (Integer) it.next();
						String per_id = Integer.toString(p_id);
						//adds current pair session id, permission id
						((RbacMatrix) graph_response).AddUA(ses_id, per_id);
					}
					
				}
				
				return graph_response;
				*/
				SDP_Data_Structure graph_response = this.matrix_create();
				return graph_response;
			}
			else return null;//if he asked for roles that he is not assigned to
		}
		else return null;
	}

	@Override
	public SDP_Data_Structure delete(int session_id) {
		//compare with last session added to list
		//if it is the last, meaning it is the last in matrix, we can just delete last row
		if(this.Sessions.get(Sessions.size()-1).equals(session_id))
			{
				//remove it from lists
				//Sessions.remove(session_id);
				Iterator it = Sessions.iterator();
				while(it.hasNext()){
					Integer ses = (Integer) it.next();
					if (ses.equals(session_id)){
						it.remove();
					}
				}
				//go thru record and find particular session_id and delete it from record
				Iterator iterator = record.iterator();
				while(iterator.hasNext()){
					RecordData r = (RecordData) iterator.next();
					if (r.session == session_id) //record.remove(r);
						iterator.remove();
				}
				//return new matrix
				//part same as in response method *** copied
				SDP_Data_Structure graph_response = this.matrix_create();
				return graph_response;
			}
			//if not the last just return nothing - null and delete from PDP's lists
			else 
			{
				//remove it from lists
				//Sessions.remove(session_id);
				Iterator it = Sessions.iterator();
				while(it.hasNext()){
					Integer ses = (Integer) it.next();
					if (ses.equals(session_id)){
						it.remove();
					}
				}
				//go thru record and find particular session_id and delete it from record
				Iterator iterator = record.iterator();
				while(iterator.hasNext()){
					RecordData r = (RecordData) iterator.next();
					if (r.session == session_id) {
						iterator.remove();
						//record.remove(r);
						break;
					}
				}
				return null;

			}
		
	}
	
	public SDP_Data_Structure matrix_create() {
		//find largest of all permissions and sessions that will be the matrix dimensions
		Object[] helper = Permissions.toArray();
		int top = 0;
		for(int i = 0; i < helper.length; i++){
			//int curr = Integer.parseInt((String) helper[i]);
			int curr = (Integer) helper[i];
			if (top < curr) top = curr;
		}
		int per_num = top + 1;//subtract top with 1, cause largest could be 2,
		//but actually there are permissions 0, 1, 2, and that's 3 of them
		
		top = 0;//reset top to 0 value again
		Object[] helper1 = Sessions.toArray();//reset helper to be now produced from Sessions
		for(int i = 0; i < helper1.length; i++){
			int curr = (Integer) helper1[i];
			if (top < curr) top = curr;
		}
		int ses_num = top + 1;
		//constructs empty RbacMatrix with dimensions of largest session id and permission id
		//lots of field might not be used - space trade of
		SDP_Data_Structure graph_response = new RbacMatrix(ses_num, per_num);
		/* only for testing purposes
		try {
			((RbacMatrix) graph_response).generateOutput();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		//part where we go thru RecordData to pickup pairs of 
		//session and permission to fill out the matrix
		Iterator iterator1 = record.iterator();
		while(iterator1.hasNext()){
			RecordData r = (RecordData) iterator1.next();
			//go thru permissions of current record to collect permissions' id
			Iterator it = r.permissions.iterator();
			String ses_id = Integer.toString(r.session);
			while(it.hasNext()){
				int p_id = (Integer) it.next();
				String per_id = Integer.toString(p_id);
				//adds current pair session id, permission id
				((RbacMatrix) graph_response).AddUA(ses_id, per_id);
			}
			
		}
		
		return graph_response;
	}
	
	
}
