package Cpol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashSet;

import PDP_SDP.PDP;
import PDP_SDP.SDP_Data_Structure;
import PDP_SDP.Session;
import RbacGraph.RbacGraph;
import RbacGraph.RecordData;
import Structures.RoleVertex;
import Structures.UserVertex;
import Structures.Vertex;

public class PDP_Cpol extends PDP {

	public ArrayList Rules;//arraylist of rules' id
	
	
	//this method as mentioned in Rule class is actually RemoveRule method from Cpol's original implementation
	@Override
	public SDP_Data_Structure delete(int session_id) {
		try{
		Iterator iterator = Rules.iterator();
		while(iterator.hasNext()){
			int current_id = Integer.parseInt(iterator.next().toString());
			if(current_id == session_id) {
				//Rules.remove(current_id);
				iterator.remove();
				return null; //successfully removed
			}
		}
		}catch (Exception e) {}
		return null;
	}

	@Override
	public SDP_Data_Structure request(Session s, String[] roles) {
		
		HashSet current_permissions = new HashSet();//returning arraylist of permission for particular user and his asked roles
		//ArrayList current_permissions = new ArrayList();//returning arraylist of permission for particular user and his asked roles
		ArrayList<RoleVertex> Roles = new ArrayList();
		//adding roles to arraylist of RoleVertices
		for(int i = 0; i < roles.length; i++){
			Roles.add(((RbacGraph)g).FindRole(roles[i]));
		}
		
		UserVertex user = ((RbacGraph)g).FindUser(((Session_Cpol)s).getRule().getLicencee());
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
						if(!current_permissions.contains(permissions[i]))
							current_permissions.add(permissions[i]);//adds to permissions assigned by this session
					}
				
				}
				//System.out.println(((Rule)s).getLicencee());
				//System.out.println(id);
				try{
					Rules.add(((Session_Cpol)s).getRule().getId());//add id to array list of rules' id
				}
				catch (Exception e) {}
				
				AccessToken A = new AccessToken(current_permissions);
				//create new cache key
				int key = ((Session_Cpol)s).getRule().getId();
				CacheKey cache_key = new CacheKey(key);
				//create new cache entry to be sent
				CacheEntry return_value = new CacheEntry(cache_key, A);
				
				return return_value;
				
			}
			else return null;//if he asked for roles that he is not assigned to
		}
		else return null;//if there is no such user
	}
	
}
	
