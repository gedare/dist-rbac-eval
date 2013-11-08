package Structures;
import java.util.ArrayList;
import java.util.Iterator;


// role can have edges to other roles and to permissions
//import org.jgrapht.graph;

public class RoleVertex extends Vertex{
	public int level; // level of role in hierarchy
	private String RoleID;//should be string!!!

	/*public RoleVertex(){
		level = 0;
		int temp = ID;
		RoleID = temp++;
		neighbours = new ArrayList();
		
	}*/
	
	public RoleVertex(String RoleID){
		level = 0;
		if(RoleID != null)
		    this.RoleID = new String(RoleID);
		else
		    this.RoleID = RoleID;

		this.ID = staticID;
		staticID++;
		neighbours = new ArrayList();
		
	}
	/*public RoleVertex(int level, boolean b){
		if(b){
			this.level = level;
			int temp = ID;
			RoleID = temp++;
			neighbours = new ArrayList();
		}
	}*/
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getRoleID() {
		return RoleID;
	}

	public String getStringID() {
	    return RoleID;
	}

	//returns String of role type: end_point, internal, hybrid role
	public String GetRoleType(){
		String result = new String();
		int role_neighbours = 0;
		int permission_neighbours = 0;
		Iterator iterator = neighbours.iterator(); 
		while(iterator.hasNext()) 
		{	
		    Vertex n = (Vertex)(iterator.next());
		    String id = n.getStringID();

		    if((((String)id).startsWith("r"))||(((String)id).startsWith("R"))) role_neighbours++;
		    if((((String)id).startsWith("r"))||(((String)id).startsWith("R"))) permission_neighbours++;
		}
		if(role_neighbours == 0) result = "end_point";
			else if(permission_neighbours == 0) result = "internal";
				else result = "hybrid";
		return result;
	}
}
