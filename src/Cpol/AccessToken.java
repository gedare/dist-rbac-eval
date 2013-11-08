package Cpol;

import java.util.HashSet;
import java.util.Set;

public class AccessToken {

	public Set permissions;//permissions could be either integer or string, in our case they will most certainly be integer
	
	public AccessToken(){
		this.permissions = new HashSet();
	}
	
	public AccessToken(Set permissions){
		this.permissions = permissions;
	}
	
	
	public Set getPermissions() {
		return permissions;
	}

	public void setPermissions(Set permissions) {
		this.permissions = permissions;
	}

	public boolean checkPermission(int permission_id){
		if (permissions.contains(permission_id)) return true;
		return false;
	}
	//do we need Add? authors said, combining two access tokens in Add, by linking them together to form a list ? 
	public void Add(AccessToken A){
	}
	
}
