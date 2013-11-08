package Generator;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import Helper.Parser;
import RbacGraph.RbacGraph;
import Structures.Vertex;

//******************************************************//
//		this generator generates next profile			//
//1st it generates one initiation session request		//
//after that all "i" "a" or "d" are generated randomly  //
//******************************************************//

public class Generator {

	public static void main(String[] args) throws IOException {

		String filename = args[0];//construct file name
		FileReader fReader = new FileReader(filename);
	
		Parser parser = new Parser();
		RbacGraph g = new RbacGraph();
		g.load(parser, fReader);
		
		int num_of_permissions = g.count_permissions();
		int[] permission_ids = g.get_permissions_IDs();
		String[] users_ids = g.get_users_ids();
		int num_of_users = users_ids.length;
		String[] roles_ids = g.get_roles_ids();
		int num_of_roles = roles_ids.length;
		
		FileWriter file;
		file = new FileWriter("Data\\instuctions");
		
		BufferedWriter buffWriter = new BufferedWriter(file);
		String newline = System.getProperty("line.separator");
		
		int choice;
		int counter_of_sessions = 0;
		ArrayList checker = new ArrayList();//will be filled with roles and used to check that no duplicates will be printed out
		ArrayList checker_1 = new ArrayList();//same purpose as ArrayList checker

		//outer loop, no limit in general
		int parameter = Integer.parseInt(args[1]);
		for(int w = 0; w < parameter; w++){
			if(counter_of_sessions == 0){
				String current_id;
				int i;//index i will be changed as we go through generation of simulation file
				//part when particular user is found
				//and its induced graph is returned
				//so we can extract all of it's roles and permissions

				//print out "i" and some int
				buffWriter.flush();
				buffWriter.write("i");
				//buffWriter.write(" " + SOME INTEGER?);
				buffWriter.write(" " + counter_of_sessions);
				counter_of_sessions ++;

				//index i should be generated as a RANDOM number from 0 to num_of_users
				i = (new Random()).nextInt(num_of_users);
				
				current_id = users_ids[i];
				//curent_id should be written into file
				buffWriter.write(" " + current_id);
				
				Vertex current_user = g.FindUser(current_id);//find user
				ArrayList list = new ArrayList();
				list = g.getInducedGraph(list, current_user);//get user's sub graph
				RbacGraph sub_graph = new RbacGraph(list);//create a graph so that roles and permissions could be extracted
				
				String[] current_roles_ids = sub_graph.get_roles_ids();//all the roles' ids
				int[] current_permissions_ids = sub_graph.get_permissions_IDs();//all the permissions' ids
				int current_num_of_roles = current_roles_ids.length;//count of all the roles
				int current_num_of_permissions = current_permissions_ids.length;//count of all the permissions
			
				//int num_of_members = (int) Math.pow(2, current_num_of_roles);//number of subsets	
				//int rand_num_of_roles = (new Random()).nextInt(num_of_members);//produce random to get number of set members
				int rand_num_of_roles = (new Random()).nextInt(current_num_of_roles);
				//for how many #of randomly generated roles there are, that many times we go in a for loop
				for(int n = 0; n < rand_num_of_roles + 1; n++){
					//we should randomly choose a role and write it into a file
					int j = (new Random()).nextInt(current_num_of_roles);//get a random role
					String current_role = current_roles_ids[j];
					if(!checker.contains(current_role)) {
						checker.add(current_role);//if it is not contained add it and print it
						//current_role should be written into file
						buffWriter.write(" " + current_role);
					}				
				}
				buffWriter.write(newline);//print out new line
			}
			//randomly choose one choice
			//0: initalization_session_request
			//1: access_request
			//2: session_destroy
			choice = (new Random()).nextInt(3);
			switch (choice){
				case 0:{
					String current_id;
					int i;//index i will be changed as we go through generation of simulation file
					//part when particular user is found
					//and its induced graph is returned
					//so we can extract all of it's roles and permissions

					//print out "i" and some int
					buffWriter.flush();
					buffWriter.write("i");
					//buffWriter.write(" " + SOME INTEGER?);
					buffWriter.write(" " + counter_of_sessions);
					counter_of_sessions ++;

					//index i should be generated as a RANDOM number from 0 to num_of_users
					i = (new Random()).nextInt(num_of_users);
					
					checker = new ArrayList();//reset the checker
					current_id = users_ids[i];
					//curent_id should be written into file
					buffWriter.write(" " + current_id);
					
					Vertex current_user = g.FindUser(current_id);//find user
					ArrayList list = new ArrayList();
					list = g.getInducedGraph(list, current_user);//get user's sub graph
					RbacGraph sub_graph = new RbacGraph(list);//create a graph so that roles and permissions could be extracted
					
					String[] current_roles_ids = sub_graph.get_roles_ids();//all the roles' ids
					int[] current_permissions_ids = sub_graph.get_permissions_IDs();//all the permissions' ids
					int current_num_of_roles = current_roles_ids.length;//count of all the roles
					int current_num_of_permissions = current_permissions_ids.length;//count of all the permissions
				
					//int num_of_members = (int) Math.pow(2, current_num_of_roles);//number of subsets	
					//int rand_num_of_roles = (new Random()).nextInt(num_of_members);//produce random to get number of set members
					int rand_num_of_roles = (new Random()).nextInt(current_num_of_roles);
					//for how many #of randomly generated roles there are, that many times we go in a for loop
					for(int n = 0; n < rand_num_of_roles + 1; n++){
						//we should randomly choose a role and write it into a file
						int j = (new Random()).nextInt(current_num_of_roles);//get a random role
						String current_role = current_roles_ids[j];
						if(!checker.contains(current_role)) {
							checker.add(current_role);//if it is not contained add it and print it
							//current_role should be written into file
							buffWriter.write(" " + current_role);
						}				
					}
					buffWriter.write(newline);//print out new line
					break;
				}
				case 1:{
					//print out "a"
					buffWriter.flush();
					buffWriter.write("a");
					//writing item number in the file
					int item_number =  (new Random()).nextInt(counter_of_sessions);
					String it_num = Integer.toString(item_number);
					buffWriter.write(" " + it_num);
					
					int rand_num_of_permissions = (new Random()).nextInt(num_of_permissions);//generate random number to be requested access by user
					for (int m = 0; m < rand_num_of_permissions + 1; m++){ //+ 1 is because random could generate ZERO value
						//generate random permission id
						int rand_1 = (new Random()).nextInt(num_of_permissions);
						//print out permission_ids[rand_1]
						buffWriter.write(" " + permission_ids[rand_1]);
					}
					buffWriter.write(newline);//print out new line
					break;
				}
				case 2:{
					if((counter_of_sessions - checker_1.size()) > 0){//additional check if there is any session to be deleted or not
						//print out "d"
						buffWriter.flush();
						
						ArrayList helper = new ArrayList();
						int rand_num_of_sessions = (new Random()).nextInt(counter_of_sessions);
						for (int m = 0; m < rand_num_of_sessions + 1; m++){
							int rand_1 = (new Random()).nextInt(counter_of_sessions);//generate random session number to be printed out
							if(!checker_1.contains(rand_1)){
								checker_1.add(rand_1);
								//print out rand_1
								helper.add(rand_1);//add it to a list which we will print later
								//buffWriter.write(" " + rand_1);
							}
						}
						//if any session is to be destroyed, any numbers are generated and not duplicates
						if(!helper.isEmpty()){
							buffWriter.write("d");
							Iterator iterator = helper.iterator();
							while(iterator.hasNext()){
								Object number = iterator.next();
								buffWriter.write(" " + number);
							}
							buffWriter.write(newline);//print out new line
						}
						break;
					}
				}
			}
		}
		buffWriter.close();
	}
}
