package Generator;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


import Helper.Parser;
import RbacGraph.RbacGraph;
import Structures.PermissionVertex;
import Structures.RoleVertex;
import Structures.Vertex;

public class NewSessionProfile {

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	public static void main(String[] args) throws IOException {
	//public static void main(String[] args) throws IOException {
	
		//fetch a file and parse it to get actual RBAC
		String filename = args[0];//construct file name
				
		FileReader fReader = new FileReader(filename);
	
		Parser parser = new Parser();
		RbacGraph g = new RbacGraph();
		g.load(parser, fReader);
		
		
		String[] users_ids = g.get_users_ids();
		int num_of_users = users_ids.length;
		
		//file = new FileWriter("D:\\workspace\\RBAC\\src\\Data\\instructions");

		int user_id = 0;
		
		String current_id = users_ids[user_id]; //user that we want to initiate the session; as string type
		
		Vertex current_user = g.FindUser(current_id);//find user
		//ArrayList list = new ArrayList();
		//list = g.getInducedGraph(list, current_user);//get user's sub graph
		//RbacGraph sub_graph = new RbacGraph(list);//create a graph so that roles and permissions could be extracted
		
		
		
		//String[] current_roles = sub_graph.get_roles_ids();//all the roles;
		
		List list_of_roles = new ArrayList();//list of roles to be activated
		list_of_roles = current_user.getNeighbours();
		RbacGraph sub_graph = new RbacGraph((ArrayList) list_of_roles);
		String[] current_roles = sub_graph.get_roles_ids();//all the roles that user is directly assigned to;
		
		for(int i = 0; i < current_roles.length; i++){
			
			ArrayList list = new ArrayList();
			list = g.getInducedGraph(list, current_user);//get user's sub graph
			RbacGraph sub_g = new RbacGraph(list);//create a graph so that roles and permissions could be extracted
			
				if(!current_roles[i].equals("XR")){
				FileWriter file;
				file = new FileWriter("src\\Data\\instructions" + "3" + "_" + i);
				BufferedWriter buffWriter = new BufferedWriter(file);
				String newline = System.getProperty("line.separator");
				
				//create session, increment counter
				buffWriter.flush();
				//print out "i"
				buffWriter.write("i");
				//print out item_id
				buffWriter.write(" " + "0");
				//current user's id should be written into file
				buffWriter.write(" " + "0");
				//buffWriter.write(newline);//print out new line
				buffWriter.write(" " + "XR");
				buffWriter.write(newline);//print out new line

				
				//create session, increment counter
				buffWriter.flush();
				//print out "i"
				buffWriter.write("i");
				//print out item_id
				buffWriter.write(" " + "1");
				//current user's id should be written into file
				buffWriter.write(" " + "0");
				//buffWriter.write(newline);//print out new line
				buffWriter.write(" " + current_roles[i]);
				//part where we access all the permissions for current role
				buffWriter.write(newline);//print out new line

				
				//get list of permissions for current role
				ArrayList permission_list = new ArrayList();
				RoleVertex role = sub_g.FindRole(current_roles[i]);
				
				//ArrayList l = (ArrayList) role.getNeighbours();
				//Iterator ver = l.iterator();
				//while(ver.hasNext()){
				//	Vertex new_ver = (Vertex) ver.next();
				//	System.out.println(new_ver.getID());
				//}
				
				//get a list of all role's sub vertices (role's subgraph)
				ArrayList vertices_new = new ArrayList();
				//get a new role's sub graph so we can get its permissions as well
				vertices_new = sub_g.getInducedGraph(vertices_new, role);
				//go through the sub graph so we can compare permissions
				Iterator vertex = vertices_new.iterator();
				while(vertex.hasNext()){
					Vertex new_vertex = (Vertex) vertex.next();
					//get a neighbour
					//if it is a permission add it to a list
					if(new_vertex instanceof PermissionVertex){
						permission_list.add(new_vertex);
					}
				}
				Iterator iter = permission_list.iterator();
				while(iter.hasNext()){
						buffWriter.flush();
						buffWriter.write("a");
						PermissionVertex p = (PermissionVertex) iter.next();
						
						//writing item number in the file
						buffWriter.write(" " + "1");
						//int permission = Integer.parseInt((String) iter.next());
						buffWriter.write(" " + p.getID());
						buffWriter.write(newline);//print out new line
				}
				
				//delete session
				buffWriter.flush();
				buffWriter.write("d");
				buffWriter.write(" " + "0");
				buffWriter.write(newline);//print out new line
				buffWriter.flush();
				buffWriter.write("d");
				buffWriter.write(" " + "1");
				buffWriter.write(newline);//print out new line
				
				
				buffWriter.close();
				//System.out.println("Done generating...");	
				System.out.println("Done generating..." + "3" + "_" + i);
			}
		}	
	}

}

