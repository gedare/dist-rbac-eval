package Structures;

import java.io.FileReader;
import java.io.IOException;

import Helper.Parser;
import PDP_SDP.SDP_Data_Structure;


public interface RBAC extends SDP_Data_Structure {
	
	public void AddUA(String user_id, String role_id);
	public void AddRH(String role_id1, String role_id2);
	public void AddPA(String role_id, String permission_id);
	public void load(Parser parser, FileReader fReader);
	public void generateOutput() throws IOException;
    public boolean IsUser(String user_id);
	public boolean IsRole(String role_id);
	public void AddUser(String user_id);
	public void AddRole(String role_id);
	public void AddPermission(String permission_id);

}
