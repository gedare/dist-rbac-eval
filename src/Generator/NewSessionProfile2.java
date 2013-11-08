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

public class NewSessionProfile2 {

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
		
		
		int number_of_inst_files = 10;
		int[] roles = new int[10];
		roles[0] = 10;
		roles[1] = 50;
		roles[2] = 100;
		roles[3] = 500;
		roles[4] = 750;
		roles[5] = 2000;
		roles[6] = 3000;
		roles[7] = 6000;
		roles[8] = 8000;
		roles[9] = 10000;
		//roles[10] = 110;
		//roles[11] = 120;
		//roles[12] = 75;
		//roles[13] = 9;
		//roles[14] = 90;
		//roles[15] = 222;
		//roles[16] = 125;
		//roles[17] = 105;
		//roles[18] = 205;
		//roles[19] = 500;
		
		int constant_num_of_permissions = 10;
		int counter = 0;
		
		ArrayList list = new ArrayList();
		list = g.getInducedGraph(list, current_user);//get user's sub graph
		RbacGraph sub_g = new RbacGraph(list);//create a graph so that roles and permissions could be extracted
	
		//get list of permissions for current role
		ArrayList permission_list = new ArrayList();
		RoleVertex role = sub_g.FindRole(current_roles[1]);
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
				//add only so many permissions to list
				if(counter < constant_num_of_permissions){
					permission_list.add(new_vertex);
					counter++;
				}
			}
		}
		for(int i = 0; i < number_of_inst_files; i++){
			
			FileWriter file;
			file = new FileWriter("src\\Data\\instructions" + "4" + "_" + i);
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

					
			for(int k = 0; k <= roles[i]; k++){
				if(!current_roles[k].equals("XR")){
					buffWriter.write(" " + current_roles[k]);
				}
			}
			buffWriter.write(newline);//print out new line

				//part where we access constant # of the permissions for role(s)
				
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
			System.out.println("Done generating..." + "4" + "_" + i);
		}
			
	}

}

