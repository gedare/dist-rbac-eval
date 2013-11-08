package PDP_SDP;
import java.util.ArrayList;


public class Session {
	public static int session_id;
	public int id;
	public String user_id;
	public ArrayList<String> objects;
	
	public Session(){
		id = session_id;
		session_id++;
	}
	public Session(String user_id){
		id = session_id;
		session_id++;
		this.user_id = user_id;
		this.objects = new ArrayList();
	}
	public Session(String user_id, ArrayList objects){
		id = session_id;
		session_id++;
		this.user_id = user_id;
		this.objects = objects;
	}
	public void setPermissions(ArrayList objects){
		this.objects = objects;
	}
	
}
