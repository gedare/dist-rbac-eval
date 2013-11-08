package RbacGraph;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

import Helper.Parser;
import Structures.PermissionVertex;
import Structures.RBAC;
import Structures.RoleVertex;
import Structures.UserVertex;
import Structures.Vertex;

public class RbacGraph implements RBAC {
	public ArrayList<Vertex> vertices;
	public HashMap<String, UserVertex> users;

	//public Parser parser;

	public RbacGraph(){
		//parser = new Parser();
		vertices = new ArrayList();
		users = new HashMap<String, UserVertex>();
	}
	
	public RbacGraph(ArrayList vertices){
		//parser = new Parser();
		this.vertices = vertices;
		users = new HashMap<String, UserVertex>();

		for(Iterator i = vertices.iterator(); i.hasNext(); ) {
		    Vertex v = (Vertex)(i.next());
		    if(v instanceof UserVertex) {
			users.put(v.getStringID(), (UserVertex)v);
		    }
		}
	}
	
	public int size(){
		return vertices.size();
	}
	
	public void AddUser(UserVertex user){
		vertices.add(user);
		users.put((new String(user.getStringID())), user);
		/*
		System.out.println("AddUser(): put("+user.getStringID()+", "+user+")");
		/* */
	}
	
	public void AddRole(RoleVertex role){
		vertices.add(role);
	}
	
	public void AddPermission(PermissionVertex permission){
		vertices.add(permission);
	}
	
	public void AddUser(String user_id){
		UserVertex user = new UserVertex(user_id);
		vertices.add(user);
		users.put((new String(user_id)), user);
	}
	public void AddRole(String role_id){
		RoleVertex role = new RoleVertex(role_id);
		vertices.add(role);
	}
	public void AddPermission(String permission_id){
		PermissionVertex permission = new PermissionVertex(permission_id);
		vertices.add(permission);
	}
	
	public int count_users(){
		int count_users = 0;
		Iterator iterator = vertices.iterator(); 
		while(iterator.hasNext()) 
		{	
		    Vertex vertex = (Vertex) iterator.next();
		    if (vertex instanceof UserVertex) count_users++;
		}
		return count_users;
	}
	public int count_roles(){
		int count_roles = 0;
		Iterator iterator = vertices.iterator(); 
		while(iterator.hasNext()) 
		{	
		    Vertex vertex = (Vertex) iterator.next();
		    if (vertex instanceof RoleVertex) count_roles++;
		}
		return count_roles;
	}
	public int count_permissions(){
		int count_permissions = 0;
		Iterator iterator = vertices.iterator(); 
		while(iterator.hasNext()) 
		{	
		    Vertex vertex = (Vertex) iterator.next();
		    if (vertex instanceof PermissionVertex) count_permissions++;
		}
		return count_permissions;
	}
	//this method returns String[] of permissions id in the graph
	public String[] get_permissions_ids(){
		int num_of_permissions = this.count_permissions();
		String[] p_id = new String[num_of_permissions];
		int i = 0;
		
		Iterator iterator = vertices.iterator();
		while(iterator.hasNext()){
			Vertex vertex = (Vertex) iterator.next();
			if(vertex instanceof PermissionVertex) {
				p_id[i] = ((PermissionVertex)vertex).getPermissionID();
				i++;
			}
		}
		return p_id;
	}
	//this method returns int[] of permissions ID in the graph
	public int[] get_permissions_IDs(){
		int num_of_permissions = this.count_permissions();
		int[] p_id = new int[num_of_permissions];
		int i = 0;
		
		Iterator iterator = vertices.iterator();
		while(iterator.hasNext()){
			Vertex vertex = (Vertex) iterator.next();
			if(vertex instanceof PermissionVertex) {
				p_id[i] = ((PermissionVertex)vertex).getID();
				i++;
			}
		}
		return p_id;
	}
	
	//this method returns all users' string type ids
	//this method returns String[] of users id in the graph
	public String[] get_users_ids(){
		int num_of_users = this.count_users();
		String[] u_id = new String[num_of_users];
		int i = 0;
		
		Iterator iterator = vertices.iterator();
		while(iterator.hasNext()){
			Vertex vertex = (Vertex) iterator.next();
			if(vertex instanceof UserVertex) {
				u_id[i] = ((UserVertex)vertex).getUserID();
				i++;
			}
		}
		return u_id;
	}
	//this method returns String[] of roles id in the graph
	public String[] get_roles_ids(){
		int num_of_roles = this.count_roles();
		String[] r_id = new String[num_of_roles];
		int i = 0;
		
		Iterator iterator = vertices.iterator();
		while(iterator.hasNext()){
			Vertex vertex = (Vertex) iterator.next();
			if(vertex instanceof RoleVertex) {
				r_id[i] = ((RoleVertex)vertex).getRoleID();
				i++;
			}
		}
		return r_id;
	}
	
	//this method returns int[] with number of internal roles - only pointing to other roles, 
	//number of roles that has only pointers to permissions,
	//number of hybrid roles - pointing to both roles and permissions
	public int[] count_roles_type(){
		
		ArrayList internal = new ArrayList();//list of all role_id that only have roles as neighbours
		ArrayList end_point = new ArrayList();//list of all role_id that only have permissions as neighbours
		ArrayList hybrid = new ArrayList();//list of all role_id that have both roles and permissions as neighbours
		int[] all_count = new int[3];
		
		Iterator iterator = vertices.iterator(); 
		while(iterator.hasNext()) 
		{	
		    Vertex vertex = (Vertex) iterator.next();
		    
		    int permissions_neighbours = 0;//number of permissions that specific Role assigns to
		    int roles_neighbours = 0;//number of roles that specific Role assigns to
		    
		    if (vertex instanceof RoleVertex) {
		    	/*
		    	Iterator it = ((RoleVertex)vertex).neighbours.iterator();
		    	//note neighbour list is STRING
		    	while(it.hasNext()){
		    		String neighbour = (String) it.next();
		    		if(neighbour.startsWith("r")) roles_neighbours++;
		    		if(neighbour.startsWith("p")) permissions_neighbours++;
		    	}
		    	//check all neighbours increment number of role_neighbours or permission_neighbours
		    	//if their id does not exists already in the general all graph list which we make as we go thru roles
		    	if (permissions_neighbours == 0) internal.add(((RoleVertex)vertex).getRoleID());
		    		else if (roles_neighbours == 0) end_point.add(((RoleVertex)vertex).getRoleID());
		    			else hybrid.add(((RoleVertex)vertex).getRoleID());
		    	*/
		    	//other way
		    	String type = new String(((RoleVertex)vertex).GetRoleType());
		    	if(type.equalsIgnoreCase("hybrid")) hybrid.add(((RoleVertex)vertex).getRoleID());
		    	if(type.equalsIgnoreCase("end_point")) end_point.add(((RoleVertex)vertex).getRoleID());
		    	if(type.equalsIgnoreCase("internal")) internal.add(((RoleVertex)vertex).getRoleID());
		    	
		    }
		}
		all_count[0] = internal.size();
		all_count[1] = end_point.size();
		all_count[2] = hybrid.size();
		
		//System.out.println(all_count[2]);
		//System.out.println(internal.size());
		//System.out.println(end_point.size());
		//System.out.println(hybrid.size());
		
		return all_count;
	}
	
	//public int count_level_of_role_hierarchy(){}
	
	//this method returns true if vertex with id = user_id is UserVertex
	public boolean IsUser(String user_id){
		boolean cond = false;
		Iterator iterator = vertices.iterator(); 
		while(iterator.hasNext()) 
		{	
		    Vertex vertex = (Vertex) iterator.next();
		    if (vertex instanceof UserVertex){
		    	if (((UserVertex) vertex).getUserID().equals(user_id)) cond = true;
		    }
		}
		return cond;
		
	}
	
	//this method returns true if vertex with id = role_id is RoleVertex
	public boolean IsRole(String role_id){
		boolean cond = false;
		Iterator iterator = vertices.iterator(); 
		while(iterator.hasNext()) 
		{	
		    Vertex vertex = (Vertex) iterator.next();
		    if (vertex instanceof RoleVertex){
		    	if (((RoleVertex) vertex).getRoleID().equals(role_id)) cond = true;
		    }
		}
		return cond;
		
	}

	public Vertex FindVertex(String id) {
	    Vertex v;

	    v = FindUser(id);
	    if(v != null) return v;

	    v = FindRole(id);
	    if(v != null) return v;

	    return(FindPermission(id));
	}

	//finds user vertex in graph, if there is one with user.id=id
	public UserVertex FindUser(String id){
	    /*
	    System.out.println("FindUser(): users.size() = "+users.size());
	    /* */
	    return(this.users.get(id));
	    /* 
		Iterator iterator = vertices.iterator(); 
		while(iterator.hasNext()) 
		{	
		    Vertex vertex = (Vertex) iterator.next();
		    if (vertex instanceof UserVertex){
		    	if (((UserVertex) vertex).getUserID().equals(id)) return (UserVertex) vertex;
		    }
		}
		return null;
	    /* */
	}
	
	//finds role vertex in graph, if there is one with role.id=id
	public RoleVertex FindRole(String id){
		Iterator iterator = vertices.iterator(); 
		while(iterator.hasNext()) 
		{	
		    Vertex vertex = (Vertex) iterator.next();
		    if (vertex instanceof RoleVertex){
		    	if (((RoleVertex) vertex).getRoleID().equals(id)) return (RoleVertex) vertex;
		    }
		}
		return null;
	}
	
	//finds permission vertex in graph, if there is one with role.id=id
	public PermissionVertex FindPermission(String id){
		Iterator iterator = vertices.iterator(); 
		while(iterator.hasNext()) 
		{	
		    Vertex vertex = (Vertex) iterator.next();
		    if (vertex instanceof PermissionVertex){
		    	if (((PermissionVertex) vertex).getPermissionID().equals(id)) return (PermissionVertex) vertex;
		    }
		}
		return null;
	}
	
	public UserVertex FindUserVertex(int id){
		Iterator iterator = vertices.iterator(); 
		while(iterator.hasNext()) 
		{	
		    Vertex vertex = (Vertex) iterator.next();
		    if (vertex instanceof UserVertex){
		    	if (((UserVertex) vertex).getID() == id) return (UserVertex) vertex;
		    }
		}
		return null;
	}

	/* An abusive method by which the RoleVertex role, and
	 * its descendent Vertex instances potentially end up
	 * in multiple RbacGraph instances */
	public void AddUA(String user_id, RoleVertex role) {
	    if(role == null) return;

	    UserVertex user = FindUser(user_id);
	    if(user == null) {
		user = new UserVertex(user_id);
		this.AddUser(user);
		/*
		System.out.println("AddUA(): adding user "+user.getStringID());
		/* */
	    }

	    user.AddNeighbour(role);
	}
	
	//adds edge between user and role
	public void AddUA(String user_id, String role_id) {
		
		UserVertex user = FindUser(user_id);
		RoleVertex role = FindRole(role_id);
		
		if (user == null) {
			user = new UserVertex(user_id);
			this.AddUser(user);

			/*
			System.out.println("AddUA: creating user "+user_id);
			/* */
		}
		if (role == null) {
			role = new RoleVertex(role_id);
			this.AddRole(role);
			/*
			System.out.println("AddUA: creating role "+role_id);
			/* */
		}
		
		if ((user!=null) && (role!=null)) {
		    /*
		    System.out.println(user.getStringID()+" --> "+role.getStringID());
		    (new Throwable()).printStackTrace();
		    /* */
		    user.AddNeighbour(role);
		}
	}
	
	//adds edge between role and role
	public void AddRH(String role_id1, String role_id2) {
		
		RoleVertex role1 = FindRole(role_id1);
		RoleVertex role2 = FindRole(role_id2);
		
		if (role1 == null) {
			role1 = new RoleVertex(role_id1);
			this.AddRole(role1);
			/*
			System.out.println("AddRH: creating role "+role_id1);
			/* */
		}
		if (role2 == null) {
			role2 = new RoleVertex(role_id2);
			this.AddRole(role2);
			/*
			System.out.println("AddRH: creating role "+role_id2);
			/* */
		}
		//check if there are such roles, and check role hierarchy level
		//don't want to have cycles in role subgraph
		if ((role1!=null) && (role2!=null)&&(role1.level >= role2.level )) {
		    /*
		    System.out.println(role1.getStringID()+" --> "+role2.getStringID());
		    (new Throwable()).printStackTrace();
		    /* */
		    role1.AddNeighbour(role2);
		}
	}
	
	//adds edge between role and permission
	public void AddPA(String role_id, String permission_id) {
		
		RoleVertex role = FindRole(role_id);
		PermissionVertex permission = FindPermission(permission_id);
		
		if (role == null) {
			role = new RoleVertex(role_id);
			this.AddRole(role);
			/*
			System.out.println("AddPA: creating role "+role_id);
			/* */
		}
		if (permission == null) {
			permission = new PermissionVertex(permission_id);
			this.AddPermission(permission);
			/*
			System.out.println("AddPA: creating permission "+permission_id);
			/* */
		}
		
		if ((role!=null) && (permission!=null)) {
		    /*
		    System.out.println(role.getStringID()+" --> "+permission.getStringID());
		    (new Throwable()).printStackTrace();
		    /* */
		    role.AddNeighbour(permission);
		}
	}
	
	//method calls previous 3 methods in order to add state to RbacGraph
	public void AddState(String u, String r, String r1, String r2, String r3, String p){
		AddPA(u, r);
		AddRH(r1, r2);
		AddPA(r3, p);
	}
	
	public void RemoveUA(int user_id, int role_id){}
	public void RemovePA(int role_id, int permission_id){}
	public void RemoveRH(int role_id1, int role_id2){}
	
	//this is just in case that our id (a) is the same in matters of int or string
	//b is specific permission's ID that we want to check if it is assigned to a
	public boolean IsPair(int a, int b) {
		String s = Integer.toString(a);
		UserVertex u = this.FindUser(s);
		if(u != null){
			//get induced sub graph of that particular user
			ArrayList arraylist = new ArrayList();
			arraylist = this.getInducedGraph(arraylist, u);
			RbacGraph temp = new RbacGraph(arraylist);
			//get all permissions' id from that sub graph
			int[] permissions = temp.get_permissions_IDs();
			//go thru array to compare and see if there is such (from input)permission that user is assigned to
			for(int i = 0; i < permissions.length; i++){
				if (permissions[i] == b) return true;
			}
		}
		//find vertex with specific int session_id
		//get its induced graph
		//check in that graph if there is such permission with same permission_id
		//use method get_permissions_IDs() and then check for specific permission_id
		return false;
	}
	
	public ArrayList<Vertex> getInducedGraph(ArrayList<Vertex> arraylist, Vertex v) {
		
		//breath first search by user to get induced graph
			if (v instanceof UserVertex){//check user and his neighbours
				if(!arraylist.contains(v)) arraylist.add(v);
				for (int i = 0; i < ((UserVertex)v).neighbours.size(); i++){
					Iterator iterator = vertices.iterator(); 
					while(iterator.hasNext()){
						Vertex current = (Vertex) iterator.next();
						if(current instanceof RoleVertex){
							String s1 = ((RoleVertex)current).getRoleID();
							String s2 = ((UserVertex)v).neighbours.get(i).getStringID();
							if(s1.equals(s2)){
								getInducedGraph(arraylist, current);
								}
							}
						}
					}
					
			}
			if (v instanceof RoleVertex){//check role and his neighbours
				if(!arraylist.contains(v)) arraylist.add(v);
				for (int i = 0; i < ((RoleVertex)v).neighbours.size(); i++){
					Iterator iterator = vertices.iterator(); 
					while(iterator.hasNext()){
						Vertex current = (Vertex) iterator.next();
						if(current instanceof RoleVertex){
							if ((((RoleVertex)current).getRoleID()).equals(v.getNeighbour(i).getStringID())) {
								//arraylist.add(current);//add current vertex to arraylist
								getInducedGraph(arraylist, current);
								}
							}
						if(current instanceof PermissionVertex){
							if ((((PermissionVertex)current).getPermissionID()).equals(v.getNeighbour(i).getStringID())) {
								//arraylist.add(current);//add current vertex to arraylist
								getInducedGraph(arraylist, current);
								}
							}
						}
					}
					
			}
			if (v instanceof PermissionVertex)//probably it is already added!?
				if(!arraylist.contains(v)) arraylist.add(v);//add current vertex to arraylist
				

		/*
	        System.out.print("getInducedGraph(): ");
		for(Iterator j = arraylist.iterator(); j.hasNext(); ) {
		    Vertex w = (Vertex)(j.next());
		    System.out.print(w.getStringID()+", ");
		}
		System.out.println("");

		(new Throwable()).printStackTrace();
		/* */

		return arraylist;
	}
	
	public void Output() throws IOException{
		
		FileWriter file;
		String s = new String();
		file = new FileWriter("C:\\Users\\komlen\\Desktop\\NewInput.txt");
		
		BufferedWriter buffWriter = new BufferedWriter(file);
		String newline = System.getProperty("line.separator");
		
		buffWriter.write(newline);	//go to new line
		Iterator iterator = vertices.iterator(); 
		while(iterator.hasNext()) 
		{	
		    Vertex vertex = (Vertex) iterator.next();
		    if (vertex instanceof UserVertex){
		    	StringBuffer string = new StringBuffer();
		    	String id = ((UserVertex)vertex).getUserID();
		    	
		    	for(int i = 0; i < vertex.getNumNeighbours(); i++) {
		    		buffWriter.flush();
		    		buffWriter.write("#UA"); //prints user role pair
	    			buffWriter.write(newline);
		    		buffWriter.write(id);
		    		buffWriter.write(" ");
		    		buffWriter.write(vertex.getNeighbour(i).getStringID());
		    		buffWriter.write(newline);
		    	}
		    }
		    else if (vertex instanceof RoleVertex){
		    	StringBuffer string = new StringBuffer();
		    	String id = ((RoleVertex)vertex).getRoleID();
		    	
		    	for(int i = 0; i < vertex.getNumNeighbours(); i++) {
		    		buffWriter.flush();
		    		String helper = vertex.getNeighbour(i).getStringID().substring(0, 1);
		    		if (helper.equalsIgnoreCase("R")){ //prints role role pair
		    			buffWriter.write("#RH");
		    			buffWriter.write(newline);
		    			buffWriter.write(id);
		    			buffWriter.write(" ");
		    			buffWriter.write(vertex.getNeighbour(i).getStringID());
		    			buffWriter.write(newline);
		    		}
		    		if (helper.equalsIgnoreCase("P")){ //prints role permission pair
		    			buffWriter.write("#PA");
		    			buffWriter.write(newline);
		    			buffWriter.write(id);
		    			buffWriter.write(" ");
		    			buffWriter.write(vertex.getNeighbour(i).getStringID());
		    			buffWriter.write(newline);
		    		}
		    	}
		    }
		}
		buffWriter.close();
	}
	//this method generates output file for visualisation software input
	public void generateOutput() throws IOException{
		
		FileWriter file;
		String s = new String();
		file = new FileWriter("RbacGraph.dot");
		
		BufferedWriter buffWriter = new BufferedWriter(file);
		String newline = System.getProperty("line.separator");
		
		buffWriter.write("digraph RbacGraph {");
		buffWriter.write(newline);	//go to new line
		Iterator iterator = vertices.iterator(); 
		while(iterator.hasNext()) 
		{	
		    Vertex vertex = (Vertex) iterator.next();
		    //write users and list of roles
		    if (vertex instanceof UserVertex){
		    	StringBuffer string = new StringBuffer();
		    	String id = ((UserVertex)vertex).getUserID();
		    	
		    	for(int i = 0; i < vertex.getNumNeighbours(); i++) {
		    		buffWriter.flush();
		    		buffWriter.write(id);
		    		buffWriter.write(" -> ");
		    		buffWriter.write(vertex.getNeighbour(i).getStringID());
		    		buffWriter.write(";");
		    		buffWriter.write(newline);
		    	}
		    }
		    //write roles and list of permissions
		    else if (vertex instanceof RoleVertex){
		    	StringBuffer string = new StringBuffer();
		    	String id = ((RoleVertex)vertex).getRoleID();
		    	
		    	for(int i = 0; i < vertex.getNumNeighbours(); i++) {
		    		buffWriter.flush();
		    		buffWriter.write(id);
		    		buffWriter.write(" -> ");
		    		buffWriter.write(vertex.getNeighbour(i).getStringID());
		    		buffWriter.write(";");
		    		buffWriter.write(newline);
		    	}
		    }
		}
		buffWriter.write("}");
		buffWriter.close();
	}
	
	public void load(Parser parser, FileReader fReader){
		if (this != null )
			try {
				parser.parse(this, fReader);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
