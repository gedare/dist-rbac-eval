package Structures;
import java.util.ArrayList;
import java.util.Iterator;


public class UserVertex extends Vertex{
	private String UserID;
	

	public String getUserID() {
		return UserID;
	}

	public String getStringID() {
	    return UserID;
	}
	
	/*public UserVertex(){
		int temp = ID;
		UserID = temp++;
		neighbours = new ArrayList();
		
	}*/
	public UserVertex(String UserID){
	    if(UserID != null)
		this.UserID = new String(UserID);
	    else 
		this.UserID = UserID;

		this.ID = staticID;
		staticID++;
		neighbours = new ArrayList();
		
	}
}
