package RbacGraph;
import java.util.ArrayList;


public class RecordData {
	public int session;//session id
	public ArrayList permissions;//permissions' id requested by the particular session
	
	public RecordData(int session, ArrayList permissions){
		this.session = session;
		this.permissions = permissions;
	}
	public void add_permission(int permission){
		this.permissions.add(permission);
	}
}
