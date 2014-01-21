package Generator;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import Helper.Parser;
import RbacGraph.RbacGraph;
import Structures.PermissionVertex;
import Structures.RoleVertex;
import Structures.Vertex;

public class SessionProfile {

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	//public SessionProfile(){}
	//public static void Profile(String[] args, int index1, int index2) throws IOException {
	public static void main(String[] args) throws IOException {
	
		//fetch a file and parse it to get actual RBAC
		String filename = args[0];//construct file name
		int number_of_sessions = Integer.parseInt(args[1]); 
		int number_of_sessions_per_access_check = Integer.parseInt(args[2]); 
		int number_of_roles_per_session = Integer.parseInt(args[3]); 
		int nature_of_roles = Integer.parseInt(args[4]); 
		int number_of_access_checks = Integer.parseInt(args[5]); 
		int nature_of_access_checks = Integer.parseInt(args[6]);
		
		FileReader fReader = new FileReader(filename);
	
		Parser parser = new Parser();
		RbacGraph g = new RbacGraph();
		g.load(parser, fReader);
		
		
		String[] users_ids = g.get_users_ids();
		int num_of_users = users_ids.length;
		
		FileWriter file;
		file = new FileWriter("Data/instructions");

		BufferedWriter buffWriter = new BufferedWriter(file);
		String newline = System.getProperty("line.separator");
		
		HashMap<Integer, ArrayList> hashmap = new HashMap();
		int counter = 0; //counter of sessions created
		int session_to_delete = 0; //session to be deleted in case there is too much of them
		
		int i = 0;
	  Random generator = new Random();
		while(i < number_of_sessions)
		//for (int i = 0; i < number_of_sessions; i++)
			{
			//generate random user
			int user_id = generator.nextInt(num_of_users);
			
			String current_id = users_ids[user_id]; //user that we want to initiate the session; as string type
			
			Vertex current_user = g.FindUser(current_id);//find user
			ArrayList list = new ArrayList();
			list = g.getInducedGraph(list, current_user);//get user's sub graph
			RbacGraph sub_graph = new RbacGraph(list);//create a graph so that roles and permissions could be extracted
			
			
			
			String[] current_roles = sub_graph.get_roles_ids();//all the roles;
			
			ArrayList list_of_roles = new ArrayList();//list of roles to be activated
		
			//activate number of roles matching number_of_roles_per_session
			boolean flag = false;//flag we use only in case nature_of_roles = 0
			switch(nature_of_roles){
				case 0:{
					ArrayList set_of_roles = new ArrayList();
					ArrayList permission_list = new ArrayList();
					//int set_counter = 0; //counter of the elements in set of roles that we want to activate
					for(int c = 0; c < current_roles.length; c++){
						//while(!flag){
							//instantiate empty list of roles "set_of_roles" so we can start adding roles WITH SAME PERMISISONS to it
							//set_of_roles = new ArrayList();
							set_of_roles.clear();

							//ArrayList permission_list = new ArrayList();
							permission_list.clear();

							RoleVertex role = sub_graph.FindRole(current_roles[c]);
							set_of_roles.add(role);
							
							//get a list of all role's sub vertices (role's subgraph)
							ArrayList vertices = new ArrayList();
							//get a new role's sub graph so we can get its permissions as well
							vertices = sub_graph.getInducedGraph(vertices, role);
							//go through the sub graph so we can compare permissions
							Iterator vertex = vertices.iterator();
							while(vertex.hasNext()){
								Vertex new_vertex = (Vertex) vertex.next();
								//get a neighbour
								//if it is a permission add it to a list
								if(new_vertex instanceof PermissionVertex){
									permission_list.add(new_vertex);
								}
							}
							
							//in this loop we will go through roles from subgraph starting from next index position
							//we will extract all the permissions that a new role activates and create a new list of permissions
							//at the end it the new list of permissions is matching with old one, that means that 2 roles activates the same permisisons
							//therefore we will add new role to a list of roles that we called "set_of_roles"
							for(int d = c+1; d < current_roles.length; d++){
								//repeat the process of getting a permission neighbours
								ArrayList permission_list_new = new ArrayList();
								RoleVertex role_new = sub_graph.FindRole(current_roles[d]);
								//get a list of all role's sub vertices (role's subgraph)
								ArrayList vertices_new = new ArrayList();
								//get a new role's sub graph so we can get its permissions as well
								vertices_new = sub_graph.getInducedGraph(vertices_new, role_new);
								//go through the sub graph so we can compare permissions
								vertex = vertices_new.iterator();
								while(vertex.hasNext()){
									Vertex new_vertex = (Vertex) vertex.next();
									//get a neighbour
									//if it is a permission add it to a list
									if(new_vertex instanceof PermissionVertex){
										permission_list_new.add(new_vertex);
									}
								}
								//when completed compare 2 lists
								int set_counter = 0;
								if(permission_list.size() == permission_list_new.size()){
									Iterator i1 = permission_list.iterator();
									while(i1.hasNext()){
										PermissionVertex p1 = (PermissionVertex) i1.next();
										Iterator i2 = permission_list_new.iterator();
										boolean fla = false;//this FLAG we use to brake the inner WHILE when we find matching permission, so we do not go any further
										while(i2.hasNext() && !fla){
											PermissionVertex p2 = (PermissionVertex) i2.next();
											if((p1.getPermissionID()).equals(p2.getPermissionID())){
												set_counter++;
												fla = true;
												//System.out.print(p1.getPermissionID());
												//System.out.print(p2.getPermissionID());
											}
										}
									}
								}
								//only if all the permissions are compared we can say roles are matching
								//System.out.print(set_counter);
								//System.out.print("**********");
								//System.out.print(permission_list.size());
								if(set_counter == permission_list.size()){
									//add matching role to a list
									set_of_roles.add(role_new);
								}
							}
							//if there are more than 2 elements in the set we do BRAKE
							//in the case where we did not find at least 2 roles that match permissions they activate we will continiue to interate the outher loop
							//if NOT we will set flag to TRUE so we do not get into loop again!!!
							//System.out.print("+++");
							//System.out.print(set_of_roles.size());
							if(set_of_roles.size() > 1){
								//flag = true;
								list_of_roles = set_of_roles;
							}
						//}
					}	
					//list_of_roles = set_of_roles;
					break;
				}
				case 1:{
/*
					//get a random role index
					int ind = generator.nextInt(current_roles.length);
					//add first role, and every other we compare with that one
					//we only compare hierarchy level
					RoleVertex role = sub_graph.FindRole(current_roles[ind]);
					list_of_roles.add(role);
					int level = role.level;
*/
					for(int k = 0; k < current_roles.length; k++){
						RoleVertex new_role = sub_graph.FindRole(current_roles[k]);
						//add first role, and every other we compare with that one
						//we only compare hierarchy level
/*						if (new_role.getLevel()!=level) {  */
							list_of_roles.add(new_role);
/*							} */
						}
					break;
					}
				case 2:{
					//algorithm for getting all the end point roles in the sub-graph
					Iterator iterate = list.iterator();
					while(iterate.hasNext()){
						Vertex vertex = (Vertex) iterate.next();
						if((vertex instanceof RoleVertex)&&(((RoleVertex)vertex).GetRoleType().equals("end_point"))){
							list_of_roles.add((RoleVertex)vertex);
						}
					}
					break;
				}
			}
			
			//this part of outer IF is to set number of how many roles we will actually activate in our access request
			//there are TWO cases wether we activate all of them from list_of_roles or just SOME depending on variable nature_of_roles
			int num = list_of_roles.size();//set the num to length of list_of_roles list
			if(nature_of_roles == 2){//if the nature of roles is 2, generate random number from 0 to (number of roles - 1), so we activate only SOME roles and not all of them!!!
				//this condition is to make sure we do not end up generating random of ZERO!!!
				if(list_of_roles.size() > 1){
					num = generator.nextInt(list_of_roles.size() - 1);
				}
			}
			//this part is to make sure we do not end up activating ZERO roles
			if (num == 0) {
				num = 1;
			}
				
			if(list_of_roles.size() > 0){
				//1.create session, increment counter
				buffWriter.flush();
				//print out "i"
				buffWriter.write("i");
				//print out item_id
				buffWriter.write(" " + counter);
				//current user's id should be written into file
				buffWriter.write(" " + current_id);
				//buffWriter.write(newline);//print out new line
					
				//printout roles id for initiation instruction
				Iterator r_it = list_of_roles.iterator();
				int number_roles = number_of_roles_per_session;//to keep track of number of roles that we want to activate per session
				while(r_it.hasNext()){
					RoleVertex r = (RoleVertex) r_it.next();
					if((number_roles > 0)&&(num > 0)){
						//first condition in IF statement is to make sure we do not exceed number of roles per session
						//second condition in IF statement has to cases:
							//1st if nature of roles is anything but 2 we will activate all the roles from the list we created before
							//2nd if nature of roles is 2 we will only activate so many roles and not ALL from the list, since
						buffWriter.write(" " + r.getRoleID());
						//decrease both variables for the next iteration of while loop
						number_roles --;
						num --;
					}
	
				}
				buffWriter.write(newline);
				//add to a hash map so we can know which session id/item id is maped to which permissions/set of roles
				hashmap.put(counter, list_of_roles);
				//increase counter so we know at wich item_id we are
				counter++;
				//increase i so we know we did not exceed number of sessions initiated
				i++;
			}
			if(counter > number_of_sessions_per_access_check){
				//delete one of the sessions
				buffWriter.flush();
				buffWriter.write("d");
				buffWriter.write(" " + session_to_delete);
				buffWriter.write(newline);//print out new line
				
				session_to_delete++;
			}	
			
			if(counter - session_to_delete == number_of_sessions_per_access_check) {
				//2.access check
				switch(nature_of_access_checks){
					case 0:{
						int number_of_access = number_of_access_checks;//to keep track when to stop generating access checks
						//perform all access checks until it reaches the number of access checks
						int[] list_of_all_permissions = sub_graph.get_permissions_IDs();
						int j = 0;
						while(number_of_access > 0){
							buffWriter.flush();
							buffWriter.write("a");
							//generate random item id
							int item = generator.nextInt(counter); 
							//check so we do not access already deleted session
							while(item < session_to_delete){
								item = generator.nextInt(counter);
							}
							//writing item number in the file
							buffWriter.write(" " + item);
							buffWriter.write(" " + list_of_all_permissions[j]);
							buffWriter.write(newline);//print out new line
							j = (j + 1)% list_of_all_permissions.length;//keep increasing j
							//decrease number_of_access for the next iteration of while loop
							number_of_access --;
						}
						/*for(int j = 0; j < list_of_all_permissions.length; j++){
							if(number_of_access > 0){
								buffWriter.flush();
								buffWriter.write("a");
								//generate random item id
								generator = new Random();
								int item = generator.nextInt(counter);
								//writing item number in the file
								buffWriter.write(" " + item);
								buffWriter.write(" " + list_of_all_permissions[j]);
								buffWriter.write(newline);//print out new line
								//decrease number_of_access for the next iteration of while loop
								number_of_access --;
							}
						}*/
						break;
					}
					case 1:{
						int number_of_access = number_of_access_checks;//to keep track when to stop generating access checks
						while(number_of_access > 0){
	
							//int number_of_access = number_of_access_checks;//to keep track when to stop generating access checks
							//generate random item id

              // FIXME: strategy for session to activate?
              // This is uniform random
							int item = generator.nextInt(counter);

							//check so we do not access already deleted session
							while(item < session_to_delete){
								item = generator.nextInt(counter);
							}
							list_of_roles = hashmap.get(item);
							//perform only allowed access checks
							Iterator iter = list_of_roles.iterator();
							//go through the list of roles activated, and get all the permissions
	
							ArrayList permissions_allowed = new ArrayList();
							//go through list of roles for current session and get all of theirs allowed permissions
							while(iter.hasNext()){
								//get sub graph of a role
								
								Vertex r = g.FindRole(((RoleVertex)iter.next()).getRoleID());
								//Vertex r = sub_graph.FindRole(((RoleVertex)iter.next()).getRoleID());
								ArrayList sub = new ArrayList();
								sub = g.getInducedGraph(sub, r);
								//sub = sub_graph.getInducedGraph(sub, r);
								//get permissions assigned to
								RbacGraph role_sub_graph = new RbacGraph(sub);
								int[] perm = role_sub_graph.get_permissions_IDs();
								//int[] perm = role_sub_graph.get_permissions_IDs();
								//adding permissions to a list; no duplicates!
								for(int index = 0; index < perm.length; index++){
									if(! permissions_allowed.contains(perm[index])) {
										permissions_allowed.add(perm[index]);
									}
								}
							}
							//go through allowed permissions and generate access request
							//while(number_of_access > 0){
							//iter = permissions_allowed.iterator();
							//while(iter.hasNext()){
								if(number_of_access > 0){//this condition prevents generating access request in the case we have a lot more permissions than actual requests we need to do
									buffWriter.flush();
									buffWriter.write("a");
										
									//writing item number in the file
									buffWriter.write(" " + item);
									//int permission = Integer.parseInt((String) iter.next());
									//buffWriter.write(" " + iter.next());
                  
                  // FIXME: Strategy for permissions to access? This is
                  // uniform random.
									buffWriter.write(" " + generator.nextInt(permissions_allowed.size()));
									buffWriter.write(newline);//print out new line
									//decrease number_of_access for the next iteration of while loop
									number_of_access --;
								//}
								}
								else{
									break;
								}
							//}
							//break;	
						}
					number_of_access --;
					break;
					}
					case 2: {
            double alpha = 1.0; // skewness FIXME: paremetrize
						int number_of_access = number_of_access_checks;
            // precompute skewed zipf distributions for every session's permissions
            ArrayList<ZipfGenerator> zipf = new ArrayList<ZipfGenerator>();
            ArrayList<ArrayList> permissions = new ArrayList<ArrayList>();
            for ( int item = 0; item < counter; item++ ) {
                list_of_roles = hashmap.get(item);
                //perform only allowed access checks
                //go through the list of roles activated,
                //and get all the permissions
                Iterator iter = list_of_roles.iterator();
                ArrayList permissions_allowed = new ArrayList();
                while(iter.hasNext()){
                  //get sub graph of a role

                  Vertex r = g.FindRole(((RoleVertex)iter.next()).getRoleID());
                  ArrayList sub = new ArrayList();
                  sub = g.getInducedGraph(sub, r);
                  RbacGraph role_sub_graph = new RbacGraph(sub);
                  int[] perm = role_sub_graph.get_permissions_IDs();
                  for(int index = 0; index < perm.length; index++){
                    if(! permissions_allowed.contains(perm[index])) {
                      permissions_allowed.add(perm[index]);
                    }
                  }
                }
                zipf.add(item, new ZipfGenerator(permissions_allowed.size(),
                    alpha));
                permissions.add(item, permissions_allowed);
            }
            while ( number_of_access > 0 ) {
              // activate a uniform random session
              int item = generator.nextInt(counter);

              //check so we do not access already deleted session
              while(item < session_to_delete){
                item = generator.nextInt(counter);
              }

              ArrayList<Integer> permissions_allowed = permissions.get(item);
              int permission = permissions_allowed.get(zipf.get(item).next());

							//go through allowed permissions and generate access request
							if(number_of_access > 0){//this condition prevents generating access request in the case we have a lot more permissions than actual requests we need to do
									buffWriter.flush();
									buffWriter.write("a");
										
									//writing item number in the file
									buffWriter.write(" " + item);
									//int permission = Integer.parseInt((String) iter.next());
									//buffWriter.write(" " + iter.next());
                  
                  // FIXME: Strategy for permissions to access? This is
                  // uniform random.
                  buffWriter.write(" " + permission);
									buffWriter.write(newline);//print out new line
									//decrease number_of_access for the next iteration of while loop
									number_of_access --;
								}
								else{
									break;
								}
						}
  					number_of_access --;
	  				break;
					}

				}			
			}		
			
		}
		//4.delete all remaining sessions
		for(int l = session_to_delete; l < counter; l++){
			buffWriter.flush();
			buffWriter.write("d");
			buffWriter.write(" " + l);
			buffWriter.write(newline);//print out new line
		}
		
		buffWriter.close();
		System.out.println("Done generating...");	
		//System.out.println("Done generating..." + index1 + "_" + index2);	
	}

}
