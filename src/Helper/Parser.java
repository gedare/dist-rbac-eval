package Helper;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import Structures.RBAC;


public class Parser {
	public RBAC g;
	
	public Parser(){}
	
	public Parser(RBAC g){
		this.g = g;
	}
	
	public void parse(RBAC g, FileReader fReader) throws IOException 
		{
			//this.g = g;
			try 
			{
				//FileReader fReader = new FileReader("D:\\Waterloo\\Thesis\\test cases\\tst.rbac");

				BufferedReader buffRead = new BufferedReader(fReader);
				String line;
				String first = new String();
				String second = new String();
				
				while ((line = buffRead.readLine())!=null ) {
					//System.out.println(line); 

					if((line.equals("#UA"))||(line.equals("# UA"))){
						//System.out.println(line); 

						while (((line = buffRead.readLine())!=null) && !(line.startsWith("#"))){ 
							//System.out.println(line); 
	
							String[] StringArray;
							
							/*first = ""; //with every new line we need first to be null so we can make a new first
							int i = 0;
							while (line.charAt(i)!= ' ') {
								first += line.charAt(i);
								i++;
							}
						
							line = line.substring(i+1); //takes first out so now only remain neighbour vertices, +1 stands for 1st space 
							*/
							StringArray = line.split(" "); // leaving only IDs in StringArray
							if(StringArray.length > 1){
								for(int i = 1; i < StringArray.length; i++){
									//g.AddUA(first, StringArray[i]);
									g.AddUA(StringArray[0], StringArray[i]);
									}
							}
							else if(!g.IsUser(StringArray[0])) g.AddUser(StringArray[0]);
						}
					}	

					if((line.equals("#PA"))||(line.equals("# PA"))){
						//System.out.println(line); 

						while (((line = buffRead.readLine())!=null) && !(line.startsWith("#"))){ 
							//System.out.println(line); 

							String[] StringArray;
							
							/*first = "";
							int i = 0;
							while (line.charAt(i)!= ' '){
								first += line.charAt(i);
								i++;
							}
							line = line.substring(i+1); //takes first out so now only remain neighbour vertices 
							*/
								
							StringArray = line.split(" "); // leaving only IDs in StringArray
							if(StringArray.length > 1){//if there is more than one ID in line
								// check if there exist such user and roles
								// if not create them in graph
								
								for(int i = 1; i < StringArray.length; i++){
									//g.AddPA(first, StringArray[i]);
									g.AddPA(StringArray[0], StringArray[i]);
									}
							}
							else if(!g.IsRole(StringArray[0])) g.AddRole(StringArray[0]);
						}
					}
					
					if((line.equals("#RH"))||(line.equals("# RH"))){
						//System.out.println(line); 

						while (((line = buffRead.readLine())!=null) && !(line.startsWith("#"))){ 
							//System.out.println(line); 

							String[] StringArray;
							
							/*first = "";
							int i = 0;
							while (line.charAt(i)!= ' ') {
								first += line.charAt(i);
								i++;
							}
							line = line.substring(i+1); //takes first out so now only remain neighbour vertices 
							*/
							StringArray = line.split(" "); // leaving only IDs in StringArray
							if(StringArray.length > 1){
								// check if there exist such user and roles
								// if not create them in graph
								
								for(int i = 1; i < StringArray.length; i++){
									//System.out.println(second);
									//g.AddRH(first, StringArray[i]);
									g.AddRH(StringArray[0], StringArray[i]);
									}
							}
							else if(!g.IsRole(StringArray[0])) g.AddRole(StringArray[0]);
						}
					}


				}
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
			
		}
	
	public ArrayList parser_count(FileReader fReader) throws IOException{
		//goes through input file 1st time
		//counts number of Users, Roles, Permissions
		//puts them in arrays

		ArrayList<String> users = new ArrayList();
		ArrayList<String> roles = new ArrayList();
		ArrayList<String> permissions = new ArrayList();

		try 
		{
			//FileReader fReader = new FileReader("D:\\Waterloo\\Thesis\\test cases\\tst.rbac");
			BufferedReader buffRead = new BufferedReader(fReader);
			String line = new String();
			String first = new String();
			boolean multiple_show_up;
			
			while ((line = buffRead.readLine())!=null ) {

				if(line.equals("#UA")){

					while (((line = buffRead.readLine())!=null) && !(line.startsWith("#"))){ 

						String[] StringArray;
						
						first = ""; //with every new line we need first to be null so we can make a new first
						int i = 0;
						while (line.charAt(i)!= ' ') {
							first += line.charAt(i);
							i++;
						}
					
						line = line.substring(i+1); //takes first out so now only remain neighbour vertices, +1 stands for 1st space 
						
						StringArray = line.split(" "); // leaving only IDs in StringArray
							
						multiple_show_up = false;
						for(i = 0; i<users.size(); i++)
							if (users.get(i).equals(first)) multiple_show_up = true;
						if(!multiple_show_up)users.add(first);//put first in users ArrayList if it's unique
						
						for(i = 0; i < StringArray.length; i++){
							multiple_show_up = false;
							for(int j = 0; j<roles.size(); j++)
								if (roles.get(j).equals(StringArray[i])) multiple_show_up = true;
							if(!multiple_show_up) roles.add(StringArray[i]);//put second in roles ArrayList if it's unique
							
							}
					}
				}

				if(line.equals("#PA")){

					while (((line = buffRead.readLine())!=null) && !(line.startsWith("#"))){ 

						String[] StringArray;
						
						first = "";
						int i = 0;
						while (line.charAt(i)!= ' ') {
							first += line.charAt(i);
							i++;
						}
						line = line.substring(i+1); //takes first out so now only remain neighbour vertices 
						
						StringArray = line.split(" "); // leaving only IDs in StringArray
							
						multiple_show_up = false;
						for(i = 0; i<roles.size(); i++)
							if (roles.get(i).equals(first)) multiple_show_up = true;
						if(!multiple_show_up)roles.add(first);//put first in roles ArrayList if it's unique
						
						for(i = 0; i < StringArray.length; i++){
							multiple_show_up = false;
							for(int j = 0; j<permissions.size(); j++)
								if (permissions.get(j).equals(StringArray[i])) multiple_show_up = true;
							if(!multiple_show_up)permissions.add(StringArray[i]);//put second in permissions ArrayList if it's unique
							}
					}
				}
				
				if(line.equals("#RH")){

					while (((line = buffRead.readLine())!=null) && !(line.startsWith("#"))){ 

						String[] StringArray;
						
						first = "";
						int i = 0;
						while (line.charAt(i)!= ' ') {
							first += line.charAt(i);
							i++;
						}
						line = line.substring(i+1); //takes first out so now only remain neighbour vertices 
						
						StringArray = line.split(" "); // leaving only IDs in StringArray
						
						multiple_show_up = false;
						for(i = 0; i<roles.size(); i++)
							if (roles.get(i).equals(first)) multiple_show_up = true;
						if(!multiple_show_up)roles.add(first);//put first in roles ArrayList if it's unique
						
						for(i = 0; i < StringArray.length; i++){
							multiple_show_up = false;
							for(int j = 0; j<roles.size(); j++)
								if (roles.get(j).equals(StringArray[i])) multiple_show_up = true;
							if(!multiple_show_up) roles.add(StringArray[i]);//put second in roles ArrayList if it's unique
							}
					}
				}


			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}

		int users_num = users.size();
		int roles_num = roles.size();
		int permissions_num = permissions.size();
		
		//System.out.println("Number of users: " + users_num);
		//System.out.println("Number of roles: " + roles_num);
		//System.out.println("Number of permissions: " + permissions_num);
		
		ArrayList<String> all = new ArrayList();
		roles.addAll(permissions);
		users.addAll(roles);
		all = users;
		return all;
		//this.g = new RbacGraphMatrix(all);
		//return g;
		//this.parse(g); 
	}
	
	public void parseMatrix(RBAC g, FileReader fReader) throws IOException 
	{
		//this.g = g;
		try 
		{
			//FileReader fReader = new FileReader("D:\\Waterloo\\Thesis\\test cases\\user_permission.txt");

			BufferedReader buffRead = new BufferedReader(fReader);
			String line;
			String first = new String();
			String second = new String();
			
			
			while ((line = buffRead.readLine())!=null ) {
				//System.out.println(line); 

				if(line.equals("#UA")){
					//System.out.println(line); 

					String tip = new String("U");
					while (((line = buffRead.readLine())!=null) && !(line.startsWith("#"))){ 
						//System.out.println(line); 

						String[] StringArray;
						
						first = ""; //with every new line we need first to be null so we can make a new first
						int i = 0;
						while (line.charAt(i)!= ' ') {
							first += line.charAt(i);
							i++;
						}
					
						line = line.substring(i+1); //takes first out so now only remain neighbour vertices, +1 stands for 1st space 
						StringArray = line.split("p_P"); // leaving only IDs in StringArray
						first = first.substring(2); // leaving only ID - example u_U12 leaves 12 only
						first = tip.concat(first);
						for(i = 0; i < StringArray.length; i++){
							second = new String("P");
							StringArray[i] = StringArray[i].trim();
							second = second.concat(StringArray[i]); //makes role id R15 for example
							//g.AddUA(Integer.parseInt(first), Integer.parseInt(StringArray[i]));
							g.AddUA(first, second);
							}
					
						/*Iterator iterator = g.vertices.iterator(); 
						while(iterator.hasNext()) 
						{	
						    Vertex v = (Vertex) iterator.next();
						    System.out.println(v.toString());
						}
						*/
					}
				}
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
	}

	
	public ArrayList[] count_users_permissions(FileReader fReader) throws IOException {
		
		ArrayList<String> permissions = new ArrayList();
		ArrayList<String> users = new ArrayList();
		
		try 
		{
			//FileReader fReader = new FileReader("D:\\Waterloo\\Thesis\\test cases\\user_permission.txt");
			BufferedReader buffRead = new BufferedReader(fReader);
			String line;
			String first = new String();
			String second = new String();
			boolean multiple_show_up;
			
			while ((line = buffRead.readLine())!=null ) {

				if(line.equals("#UA")){
					String tip = new String("U");
					while (((line = buffRead.readLine())!=null) && !(line.startsWith("#"))){ 

						String[] StringArray;
						
						first = ""; //with every new line we need first to be null so we can make a new first
						int i = 0;
						while (line.charAt(i)!= ' ') {
							first += line.charAt(i);
							i++;
						}
					
						line = line.substring(i+1); //takes first out so now only remain neighbour vertices, +1 stands for 1st space 
						
						
						StringArray = line.split("p_P"); // leaving only IDs in StringArray
						
						first = first.substring(2); // leaving only ID - example u_U12 leaves 12 only
						first = tip.concat(first);
						
						multiple_show_up = false;
						for(i = 0; i<users.size(); i++)
							if (users.get(i).equals(first)) multiple_show_up = true;
						if(!multiple_show_up)users.add(first);//put first in users ArrayList if it's unique
						
						for(i = 0; i < StringArray.length; i++){
							second = new String("P");
							StringArray[i] = StringArray[i].trim();
							second = second.concat(StringArray[i]); //makes role id R15 for example
							
							multiple_show_up = false;
							for(i = 0; i<permissions.size(); i++)
								if (permissions.get(i).equals(second)) multiple_show_up = true;
							if(!multiple_show_up) permissions.add(second);//put second in roles ArrayList if it's unique
							
							}
					}
				}
			}
		}
		catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
		ArrayList[] count = new ArrayList[2];
		count[0] = users;
		count[1] = permissions;
		return count;		
	}

	//*** ALL METHODS BELOW WITH 'NEW' IN A NAME ARE THE SAME AS ONES BEFORE***
	//*** ONLY DIFFERENCE IS THAT THEY PARSE DIFFERENT STRUCTURE INPUT FILE
	//*** THAT IS NewIput.txt
	
	//after execution of g.Output() we call this method to parse new input file
	public void parseNew(RBAC g, FileReader fReader) throws IOException 
	{
		//this.g = g;
		try 
		{
			//FileReader fReader = new FileReader("C:\\users\\komlen\\desktop\\NewInput.txt");

			BufferedReader buffRead = new BufferedReader(fReader);
			String line;
			String first = new String();
			String second = new String();
			
			
			while ((line = buffRead.readLine())!=null ) {
				//System.out.println(line); 

				if(line.equals("#UA")){
					//System.out.println(line); 

					while (((line = buffRead.readLine())!=null) && !(line.startsWith("#"))){ 
						//System.out.println(line); 

						String[] StringArray;
						
						first = ""; //with every new line we need first to be null so we can make a new first
						int i = 0;
						while (line.charAt(i)!= ' ') {
							first += line.charAt(i);
							i++;
						}
					
						line = line.substring(i+1); //takes first out so now only remain neighbour vertices, +1 stands for 1st space 
						StringArray = line.split(" "); // leaving only IDs in StringArray
						for(i = 0; i < StringArray.length; i++){
							g.AddUA(first, StringArray[i]);
							}
					}
				}

				if(line.equals("#RH")){
					//System.out.println(line); 

					while (((line = buffRead.readLine())!=null) && !(line.startsWith("#"))){ 
						//System.out.println(line); 

						String[] StringArray;
						
						first = "";
						int i = 0;
						while (line.charAt(i)!= ' ') {
							first += line.charAt(i);
							i++;
						}
						line = line.substring(i+1); //takes first out so now only remain neighbour vertices 
						
						StringArray = line.split(" "); // leaving only IDs in StringArray
						
						for(i = 0; i < StringArray.length; i++){
							g.AddRH(first, StringArray[i]);
							}
					}
				}

				if(line.equals("#PA")){
					//System.out.println(line); 

					while (((line = buffRead.readLine())!=null) && !(line.startsWith("#"))){ 
						//System.out.println(line); 

						String[] StringArray;
						
						first = "";
						int i = 0;
						while (line.charAt(i)!= ' ') {
							first += line.charAt(i);
							i++;
						}
						line = line.substring(i+1); //takes first out so now only remain neighbour vertices 
						
						StringArray = line.split(" "); // leaving only IDs in StringArray
						
						for(i = 0; i < StringArray.length; i++){
							g.AddPA(first, StringArray[i]);
							}
					}
				}

			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
	}

	public void parseMatrixNew(RBAC g, FileReader fReader) throws IOException 
	{
		//this.g = g;
		try 
		{
			//FileReader fReader = new FileReader("D:\\Waterloo\\Thesis\\test cases\\user_permission.txt");

			BufferedReader buffRead = new BufferedReader(fReader);
			String line;
			String first = new String();
			String second = new String();
			
			
			while ((line = buffRead.readLine())!=null ) {
				//System.out.println(line); 

				if(line.equals("#UA")){
					//System.out.println(line); 

					String tip = new String("U");
					while (((line = buffRead.readLine())!=null) && !(line.startsWith("#"))){ 
						//System.out.println(line); 

						String[] StringArray;
						
						first = ""; //with every new line we need first to be null so we can make a new first
						int i = 0;
						while (line.charAt(i)!= ' ') {
							first += line.charAt(i);
							i++;
						}
					
						line = line.substring(i+1); //takes first out so now only remain neighbour vertices, +1 stands for 1st space 
						StringArray = line.split(" "); // leaving only IDs in StringArray
						
						for(i = 0; i < StringArray.length; i++){
							g.AddUA(first, StringArray[i]);
							}
					
						/*Iterator iterator = g.vertices.iterator(); 
						while(iterator.hasNext()) 
						{	
						    Vertex v = (Vertex) iterator.next();
						    System.out.println(v.toString());
						}
						*/
					}
				}
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
	}
	public ArrayList[] count_users_permissions_New(FileReader fReader) throws IOException {
		
		ArrayList<String> permissions = new ArrayList();
		ArrayList<String> users = new ArrayList();
		
		try 
		{
			//FileReader fReader = new FileReader("D:\\Waterloo\\Thesis\\test cases\\user_permission.txt");
			BufferedReader buffRead = new BufferedReader(fReader);
			String line;
			String first = new String();
			String second = new String();
			boolean multiple_show_up;
			
			while ((line = buffRead.readLine())!=null ) {

				if(line.equals("#UA")){
					String tip = new String("U");
					while (((line = buffRead.readLine())!=null) && !(line.startsWith("#"))){ 

						String[] StringArray;
						
						first = ""; //with every new line we need first to be null so we can make a new first
						int i = 0;
						while (line.charAt(i)!= ' ') {
							first += line.charAt(i);
							i++;
						}
					
						line = line.substring(i+1); //takes first out so now only remain neighbour vertices, +1 stands for 1st space 
						
						StringArray = line.split(" ");

						multiple_show_up = false;
						for(i = 0; i<users.size(); i++)
							if (users.get(i).equals(first)) multiple_show_up = true;
						if(!multiple_show_up)users.add(first);//put first in users ArrayList if it's unique
						
						for(i = 0; i < StringArray.length; i++){
							
							multiple_show_up = false;
							for(int j = 0; j<permissions.size(); j++)
								if (permissions.get(j).equals(StringArray[i])) multiple_show_up = true;
							if(!multiple_show_up) permissions.add(StringArray[i]);//put second in roles ArrayList if it's unique
							
							}
					}
				}
			}
		}
		catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
		ArrayList[] count = new ArrayList[2];
		count[0] = users;
		count[1] = permissions;
		return count;		
	}

	public ArrayList parser_count_New(FileReader fReader) throws IOException{
		//goes through input file 1st time
		//counts number of Users, Roles, Permissions
		//puts them in arrays
	
		ArrayList<String> users = new ArrayList();
		ArrayList<String> roles = new ArrayList();
		ArrayList<String> permissions = new ArrayList();
	
		try 
		{
			BufferedReader buffRead = new BufferedReader(fReader);
			String line;
			String first = new String();
			String second = new String();
			boolean multiple_show_up;
			
			while ((line = buffRead.readLine())!=null ) {
	
				if(line.equals("#UA")){
					String tip = new String("U");
					while (((line = buffRead.readLine())!=null) && !(line.startsWith("#"))){ 
	
						String[] StringArray;
						
						first = ""; //with every new line we need first to be null so we can make a new first
						int i = 0;
						while (line.charAt(i)!= ' ') {
							first += line.charAt(i);
							i++;
						}
					
						line = line.substring(i+1); //takes first out so now only remain neighbour vertices, +1 stands for 1st space 
						
						StringArray = line.split(" ");					
						
						multiple_show_up = false;
						for(i = 0; i<users.size(); i++)
							if (users.get(i).equals(first)) multiple_show_up = true;
						if(!multiple_show_up)users.add(first);//put first in users ArrayList if it's unique
						
						for(i = 0; i < StringArray.length; i++){
							
							multiple_show_up = false;
							for(int j = 0; j<roles.size(); j++)
								if (roles.get(j).equals(StringArray[i])) multiple_show_up = true;
							if(!multiple_show_up) roles.add(StringArray[i]);//put second in roles ArrayList if it's unique
							
							}
					}
				}
	
				if(line.equals("#RH")){
	
					String tip = new String("R");						
					while (((line = buffRead.readLine())!=null) && !(line.startsWith("#"))){ 
	
						String[] StringArray;
						
						first = "";
						int i = 0;
						while (line.charAt(i)!= ' ') {
							first += line.charAt(i);
							i++;
						}
						line = line.substring(i+1); //takes first out so now only remain neighbour vertices 
						
						StringArray = line.split(" ");						
						
						multiple_show_up = false;
						for(i = 0; i<roles.size(); i++)
							if (roles.get(i).equals(first)) multiple_show_up = true;
						if(!multiple_show_up)roles.add(first);//put first in roles ArrayList if it's unique
						
						for(i = 0; i < StringArray.length; i++){
							
							multiple_show_up = false;
							for(int j = 0; j<roles.size(); j++)
								if (roles.get(j).equals(StringArray[i])) multiple_show_up = true;
							if(!multiple_show_up) roles.add(StringArray[i]);//put second in roles ArrayList if it's unique
							}
					}
				}
	
				if(line.equals("#PA")){
	
					String tip = new String("R");
					while (((line = buffRead.readLine())!=null) && !(line.startsWith("#"))){ 
	
						String[] StringArray;
						
						first = "";
						second = new String("P");
						int i = 0;
						while (line.charAt(i)!= ' ') {
							first += line.charAt(i);
							i++;
						}
						line = line.substring(i+1); //takes first out so now only remain neighbour vertices 
						
						StringArray = line.split(" "); // leaving only IDs in StringArray
													
						multiple_show_up = false;
						for(i = 0; i<roles.size(); i++)
							if (roles.get(i).equals(first)) multiple_show_up = true;
						if(!multiple_show_up)roles.add(first);//put first in roles ArrayList if it's unique
						
						for(i = 0; i < StringArray.length; i++){
							
							multiple_show_up = false;
							for(int j = 0; j<permissions.size(); j++)
								if (permissions.get(j).equals(StringArray[i])) multiple_show_up = true;
							if(!multiple_show_up)permissions.add(StringArray[i]);//put second in permissions ArrayList if it's unique
							}
					}
				}
	
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	
		int users_num = users.size();
		int roles_num = roles.size();
		int permissions_num = permissions.size();
		ArrayList<String> all = new ArrayList();
		roles.addAll(permissions);
		users.addAll(roles);
		all = users;
		return all;
		//this.g = new RbacGraphMatrix(all);
		//return g;
		//this.parse(g); 
	}

}
