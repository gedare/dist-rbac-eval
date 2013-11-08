package AccessMatrix;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import Helper.Parser;
import Structures.PermissionVertex;
import Structures.RBAC;
import Structures.UserVertex;


public class RbacMatrix implements RBAC {

	//public Parser parser;
	public String [][] matrix;
	int M, N;
	
	public RbacMatrix(){
		//parser = new Parser();
		//inicijalizacija matrice?
	}
	public RbacMatrix(int M, int N){
		//parser = new Parser();
		this.M = M;
		this.N = N;
		this.matrix = new String[M][N];
		for(int i=0; i<M; i++)
			for(int j=0; j<N; j++)
				matrix[i][j] = "0";
	}
	
	public RbacMatrix(int M){
		//parser = new Parser();
		this.M = this.N = M;
		this.matrix = new String[M][M];
	}
	
	public RbacMatrix(ArrayList s1, ArrayList s2){
		//parser = new Parser();
		this.M = s1.size()+1;
		this.N = s2.size()+1;
		this.matrix = new String[this.M][this.N];
		//put all 0s in matrix
		for(int i = 0; i < this.M; i++)
			for(int j = 0; j < this.N; j++)
				matrix[i][j] = "0";
		
		matrix[0][0] = "U\\P";
		int i;
		int j = 0;
		
		for(i = 1; i <= s1.size(); i++ ) {
			matrix[i][0]=(String) s1.get(j);
			//System.out.println(matrix[i][0]);
			j++;
		}
		i = 0;
		for(j = 1; j <= s2.size(); j++ ) {
			matrix[0][j]=(String) s2.get(i);
			//System.out.println(matrix[0][j]);
			i++;
		}
	}
	
	public boolean IsUser(String user_id){	
		boolean cond = false;
		for (int i = 0; i < this.M; i++)
			if (this.matrix[i][0] == user_id) cond = true;
		return cond;
	}
	//this method simply returns true or false
	//if there is such a pair in matrix - [a][b] should be set to 1
	public boolean IsPair(int a, int b){
		if((a < this.M)&&(b < this.N))
			if (matrix[a][b].equals("1")) return true;
		return false;
	}
	public void AddUser(UserVertex user){
		
	}
		
	//public void AddRole(RoleVertex role){}
		
	public void AddPermission(PermissionVertex permission){
	
	}

	public void AddUA(String user_id, String permission_id) {
		int i = Integer.parseInt(user_id);
		int j = Integer.parseInt(permission_id);
		matrix[i][j] = "1";
	}

	public void AddRH(String role_id1, String role_id2) {
		//does not do anything
		//in matrix rows are permissions and columns are users
	}

	public void AddPA(String role_id, String permission_id) {
		//does not do anything
		//in matrix rows are permissions and columns are users
	}
	
	public void load(Parser parser, FileReader fReader){
		if (this != null )
			try {
				parser.parseMatrix(this,fReader);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@Override
	public void generateOutput() throws IOException {

		FileWriter file;
		file = new FileWriter("C:\\Users\\komlen\\Desktop\\RbacMatrix.txt");
		
		BufferedWriter buffWriter = new BufferedWriter(file);
		String newline = System.getProperty("line.separator");
		
		for (int i = 0; i < this.M; i++)
			{
			for (int j = 0; j < this.N; j++){
				//System.out.println(matrix[i][j]);
				buffWriter.write(this.matrix[i][j]);
				buffWriter.write("	");
		    	buffWriter.flush();
				}
			buffWriter.write(newline);
			}
		buffWriter.close();
	}
	
	public void AddPermission(String permissionId) {
		// TODO Auto-generated method stub
		
	}
	
	public void AddRole(String roleId) {
		// TODO Auto-generated method stub
		
	}
	
	public void AddUser(String userId) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean IsRole(String roleId) {
		// TODO Auto-generated method stub
		return false;
	}
	
	


}
