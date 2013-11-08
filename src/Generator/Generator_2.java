package Generator;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import Helper.Parser;
import RbacGraph.RbacGraph;
import Structures.Vertex;


//**************************************************************//
//			this generator generates next profile				//
//1st it generates session initiation request					//
//2nd it generates all possible access requests for that session//
//3rd it generates session deletion								//
//three steps from above are repeated for every possible session//
//**************************************************************//

public class Generator_2 {

	public static void main(String[] args) throws IOException{
		
		String filename = args[0];//construct file name
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
		
		ArrayList<Item> Items = new ArrayList();
		int counter_of_sessions = 0;
		//initiate all possible session requests; for every user all combinations of roles
		for (int i = 0; i < num_of_users; i++){
			String current_id;
			//part when particular user is found
			//and its induced graph is returned
			//so we can extract all of it's roles and permissions
	
			buffWriter.flush();
			
			current_id = users_ids[i];
			
			Vertex current_user = g.FindUser(current_id);//find user
			ArrayList list = new ArrayList();
			list = g.getInducedGraph(list, current_user);//get user's sub graph
			RbacGraph sub_graph = new RbacGraph(list);//create a graph so that roles and permissions could be extracted
			
			String[] current_roles_ids = sub_graph.get_roles_ids();//all the roles' ids		
			//int[] current_permissions_ids = sub_graph.get_permissions_IDs();//all the permissions' ids
			int current_num_of_roles = current_roles_ids.length;//count of all the roles
			//int current_num_of_permissions = current_permissions_ids.length;//count of all the permissions
		
			SET set = new SET();//set of all roles for one user
			//filling the set with roles
			for(int j = 0; j < current_num_of_roles; j++){
				if(!set.contains(current_roles_ids[j])){
					set.add(current_roles_ids[j]);
				}
			}
			//set.print();//testing purposes only
			//defining number of subsets
			int num_of_subsets = (int) Math.pow(2, current_num_of_roles) - 1;//number of subsets; set contains all the roles for one user
			//num_of_subsets = num_of_subsets - 1;
			//System.out.println(num_of_subsets);
			// - 1 is because we don't count empty set
			//System.out.println(num_of_subsets);
			SET[] subsets = new SET[num_of_subsets];
			//go through all the subsets and fill them
			for (int j = 0; j < num_of_subsets; j++) {
				
				subsets[j] = set.SubSet(j+1);
				//subsets[j].print();
				Item new_item = new Item(counter_of_sessions, subsets[j].elements);//create new item with current session id and roles activated for that session which are roles in subset[j]
				Items.add(new_item);
				//write user id into file with current subset of roles
				buffWriter.flush();
				//print out "i"
				buffWriter.write("i");
				//print out item_id
				buffWriter.write(" " + counter_of_sessions);
				//current user's id should be written into file
				buffWriter.write(" " + current_id);
				//go through subset's elements and write them into file as a current initiation request instruction
				Iterator iterator = subsets[j].elements.iterator();
				while (iterator.hasNext()){
					String current_elem = (String) iterator.next();
					buffWriter.write(" " + current_elem);
				}
				buffWriter.write(newline);
				
				/*
				int[] permissions_ids = sub_graph.get_permissions_IDs();//all the permissions' ids of user
				int num_of_permissions = permissions_ids.length;//count of all the permissions
			
				//try all access requests; for every session all permissions to be accessed
				for(int k = 0; k < num_of_permissions; k++){
						//print out "a"
						buffWriter.flush();
						buffWriter.write("a");
						//writing item number in the file
						buffWriter.write(" " + counter_of_sessions);
						buffWriter.write(" " + permissions_ids[k]);
						buffWriter.write(newline);//print out new line
				}
				*/
				
				Iterator<Item> it = Items.iterator();
				//go through items and generate access requests for approved permissions ONLY
				while (it.hasNext()){
					ArrayList permissions_allowed = new ArrayList();
					Item elems = it.next();
					if(elems.getItem_id() == counter_of_sessions){
						//go through list of roles for current session and get all of theirs allowed permissions
						ArrayList roles = elems.getElems();
						Iterator iter = roles.iterator();
						while(iter.hasNext()){
							//get sub graph of a role
							Vertex r = g.FindRole((String)iter.next());
							ArrayList sub = new ArrayList();
							sub = g.getInducedGraph(sub, r);
							//get permissions assigned to
							RbacGraph sub_g = new RbacGraph(sub);
							int[] perm = sub_g.get_permissions_IDs();
							//adding permissions to a list; no duplicates!
							for(int index = 0; index < perm.length; index++){
								if(! permissions_allowed.contains(perm[index])) {
									permissions_allowed.add(perm[index]);
								}
							}
						}

						//go through allowed permissions and generate access request
						iter = permissions_allowed.iterator();
						while(iter.hasNext()){
							buffWriter.flush();
							buffWriter.write("a");
							//writing item number in the file
							buffWriter.write(" " + elems.getItem_id());
							//int permission = Integer.parseInt((String) iter.next());
							buffWriter.write(" " + iter.next());
							buffWriter.write(newline);//print out new line
						}
					}	
				}
				
				//delete the session
				//print out "d"
				buffWriter.flush();
				buffWriter.write("d");
				buffWriter.write(" " + 	counter_of_sessions);
				buffWriter.write(newline);//print out new line
				
				counter_of_sessions ++;
			}
		}
	buffWriter.close();	
	System.out.println("Done generating...");	
	}

}
