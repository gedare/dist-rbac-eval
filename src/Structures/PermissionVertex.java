package Structures;
import java.util.ArrayList;
import java.util.Iterator;


public class PermissionVertex extends Vertex{
	private String PermissionID;

	public String getPermissionID() {
		return PermissionID;
	}

	public String getStringID() {
	    return PermissionID;
	}

	/*public PermissionVertex(){
		int temp = ID;
		PermissionID = temp++;
		neighbours = new ArrayList();
		
	}*/
	
	public PermissionVertex(String PermissionID){
	    if(PermissionID != null)
		this.PermissionID = new String(PermissionID);
	    else
		this.PermissionID = PermissionID;

		this.ID = staticID;
		staticID++;
		neighbours = new ArrayList();
		
	}

	public void AddNeighbour(Vertex v) {
	    /* Should really throw an exception */
	    System.out.println("WARNING: attempt to AddNeighbour() to a permission.");
	}

	public void RemoveNeighbour(Vertex v) {
	    /* Should really throw an exception */
	    System.out.println("WARNING: attempt to RemoveNeighbour() from a permission.");
	}
}
