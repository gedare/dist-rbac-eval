package AccessMatrix;



import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import PDP_SDP.PDP;
import PDP_SDP.SDP;
import PDP_SDP.SDP_Data_Structure;
import PDP_SDP.Session;
import Structures.RBAC;


public class SDP_ACM extends SDP {

	//public String[] users; //users that login and requests information
	
	public SDP_ACM(PDP pdp) {
		super(pdp);
	}
	//new request,
	public int initiate_session_request(String user_id, String[] roles,
      SDP_Data_Structure P) {
	
		//assume login is approved
		//create new empty session
		Session s = new Session(user_id);
		//sends user_id, ses_id, roles to PDP
		SDP_Data_Structure checker = this.pdp.request(s, roles, P);
		if(checker != null) {
			this.g = (RBAC) checker;//to make sure if we have denied session request it does not mess up our previous graph on SDP
			/* only for testing purposes
			try {
				if(this.g !=null)((RBAC)this.g).generateOutput();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			return s.id;//just return session id so far so the access request could be made
		} else System.out.println("Unauthorised");
		//PDP finds permissions and stores everything in a table as a ses_id, permissions
		//PDP replies RBAC(matrix[ses_ids][permissions])
		return -1;//if not authorised return -1
		//else request was DENIED, since the list of permissions is empty, null
	}
	//new access request
	public boolean access_request(int session_id, int permission_id){
		return (((RbacMatrix)this.g).IsPair(session_id, permission_id));
	}
	@Override
	public void destroy_session(int session_id) {
		this.pdp.delete(session_id);
	}
	
	/*
	//this method could be instead in PDP, that way we will not be having RbacGraph in SDP's cache
	public RBAC reply() throws IOException {
		int choice; //choice either we want graph or matrix as a replay
		RBAC[] graph; //in a case we need a graph representation as a replay
		graph = new RBAC[users.size()];
		choice = 2; //1 - leave it as a graph, 2 - convert it into adj.matrix, 3 - convert it into access matrix
		
		int i = 0;
		Iterator iterator = users.iterator(); //go thru the ArrayList of users
		while(iterator.hasNext()) 
		{	
		    String user = (String) iterator.next();
		   // Boolean u = this.g.IsUser(user); //check if specific user is in current SDP cache
		   // if (!u) {	
		    	Vertex current = ((RbacGraph)pdp.g).FindUser(user);
		    	ArrayList<Vertex> a = new ArrayList();
		    	a = ((RbacGraph)pdp.g).getInducedGraph(a, current);
		    	graph[i] = new RbacGraph(a);//if not in cache get its representation from PDP 
		    	i++;
		    	//((RbacGraph)graph[i]).Output();
		   // }
		}
		
				
		//algorithm to merge graphs to graph_helper
		RbacGraph graph_helper = new RbacGraph();
		
		for(int j = 0; j < users.size(); j++){
			iterator = ((RbacGraph)graph[j]).vertices.iterator();
			while(iterator.hasNext()){
				Vertex vertex = (Vertex) iterator.next();
				if (vertex instanceof UserVertex){ //check if vertex is user type and if that user already exists in graph_helper
					//String s1 = ((UserVertex)vertex).getUserID();
					//UserVertex ss = graph_helper.FindUser(((UserVertex)vertex).getUserID());
					if(graph_helper.FindUser(((UserVertex)vertex).getUserID())== null)
					//if (!((graph_helper.FindUser(((UserVertex) vertex).getUserID())).equals(((UserVertex)vertex).getUserID())))
						graph_helper.AddUser((UserVertex)vertex);
					//System.out.println((graph_helper.FindUser(((UserVertex)vertex).getUserID())).getUserID());
			    }
				if (vertex instanceof RoleVertex){
					if(graph_helper.FindRole(((RoleVertex)vertex).getRoleID())== null)
					//if (!((graph_helper.FindRole(((RoleVertex) vertex).getRoleID())).equals(((RoleVertex)vertex).getRoleID())))
						graph_helper.AddRole((RoleVertex)vertex);
			    }
				if (vertex instanceof PermissionVertex){
					if(graph_helper.FindPermission(((PermissionVertex)vertex).getPermissionID())== null)
					//if (!((graph_helper.FindPermission(((PermissionVertex) vertex).getPermissionID())).equals(((PermissionVertex)vertex).getPermissionID())))
					graph_helper.AddPermission((PermissionVertex)vertex);
			    }
			}
		}
		graph_helper.Output();
		//now we have additional graph_helper containing new fresh data
		//graph helper needs to be merged with graph g of SDP
		
		switch(choice) {
			case 1:{
				//instead of using graph_helper, just add users, roles and permissions to this.g, returns g;
				for(int j = 0; j < users.size(); j++){
					iterator = ((RbacGraph)graph[j]).vertices.iterator();
					while(iterator.hasNext()){
						Vertex vertex = (Vertex) iterator.next();
						if (vertex instanceof UserVertex){
							//check if vertex is user type and if that user already exists in graph_helper
							if(((RbacGraph)this.g).FindUser(((UserVertex)vertex).getUserID())== null)
							//if (!((((RbacGraph)this.g).FindUser(((UserVertex) vertex).getUserID())).equals(((UserVertex)vertex).getUserID())))
								((RbacGraph)this.g).AddUser((UserVertex)vertex);
					    }
						if (vertex instanceof RoleVertex){
							if(((RbacGraph)this.g).FindRole(((RoleVertex)vertex).getRoleID())== null)
							//if (!((((RbacGraph)this.g).FindRole(((RoleVertex) vertex).getRoleID())).equals(((RoleVertex)vertex).getRoleID())))
								((RbacGraph)this.g).AddRole((RoleVertex)vertex);
					    }
						if (vertex instanceof PermissionVertex){
							if(((RbacGraph)this.g).FindPermission(((PermissionVertex)vertex).getPermissionID())== null)
							//if (!((((RbacGraph)this.g).FindRole(((PermissionVertex) vertex).getPermissionID())).equals(((PermissionVertex)vertex).getPermissionID())))
								((RbacGraph)this.g).AddPermission((PermissionVertex)vertex);
					    }
					}
				}
				((RbacGraph)this.g).Output();
				break;
			}	
				
				
			//one option for cases 2&3: 
			//call method  on graph_helper to create output file, go thru output file now as an input to add new elements to Matrix 
			//return it as result this.g
			case 2: {
				graph_helper.Output();
				Parser p = new Parser();
				FileReader fReader = new FileReader("C:\\users\\komlen\\desktop\\NewInput.txt");
				g = new RbacGraphMatrix(p.parser_count_New(fReader));
				
				fReader = new FileReader("C:\\users\\komlen\\desktop\\NewInput.txt");
				p.parseNew(this.g, fReader);
				g.generateOutput();
				break;
				//this will require new implementation of ADDs methods, since we are adding new columns and rows into Matrix
			}
			case 3:{

				//getting only user and permissions vertices into output file
				//that file will be read to get access matrix as a result
				FileWriter file;
				file = new FileWriter("C:\\Users\\komlen\\Desktop\\NewAccessMatrix.txt");
				
				BufferedWriter buffWriter = new BufferedWriter(file);
				String newline = System.getProperty("line.separator");
				String user_id = "";
				buffWriter.write("#UA");
				buffWriter.write(newline);

				for(i = 0; i< users.size(); i++){
					iterator = ((RbacGraph)graph[i]).vertices.iterator();
					while(iterator.hasNext()){
						Vertex vertex = (Vertex) iterator.next();
						if(vertex instanceof UserVertex) {
							user_id = ((UserVertex) vertex).getUserID();
						}
						if(vertex instanceof PermissionVertex) {
							buffWriter.write(user_id);
							buffWriter.write(" ");
							buffWriter.write(((PermissionVertex)vertex).getPermissionID());
							buffWriter.write(newline);
							buffWriter.flush();
						}
					}
				}

				
				Parser p = new Parser();
				FileReader fReader = new FileReader("C:\\users\\komlen\\desktop\\NewAccessMatrix.txt");
				ArrayList[] count = new ArrayList[2];		
				
				//algorithm that goes thru graph_helper and counts users and permissions.
				count = p.count_users_permissions_New(fReader);
				this.g = new RbacMatrix(count[0], count[1]);		
				//p.parseNew(graph_helper); //now graph_helper will be rewritten as a AdjMatrix or as a Matrix 
				//merge this.g and graph_helper into a single updated Matrix
				
				//second approach to update this.g from input file that graph_helper created
				FileReader fR = new FileReader("C:\\users\\komlen\\desktop\\NewAccessMatrix.txt");

				p.parseMatrixNew(this.g, fR);
				((RbacMatrix)this.g).generateOutput();
				break;
			}
		}
		
		return g;
		
	}
	*/
}
