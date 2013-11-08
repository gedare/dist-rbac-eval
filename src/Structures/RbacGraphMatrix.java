package Structures;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import Helper.Parser;


public class RbacGraphMatrix implements RBAC {

	//public Parser parser;
	public String [][] matrix;
	int M, N;
	
	public RbacGraphMatrix(){
		//parser = new Parser();
		//this(parser.parser_count());
	}
	
	public RbacGraphMatrix(int M, int N){
		//parser = new Parser();
		this.matrix = new String[M][N];
	}
	
	public RbacGraphMatrix(int M){
		//parser = new Parser();
		this.matrix = new String[M][M];
	}
	public RbacGraphMatrix(ArrayList s){
		//parser = new Parser();
		this.M = this.N = s.size()+1;
		this.matrix = new String[this.M][this.N];
		//put all 0s in matrix
		for(int i = 0; i < this.M; i++)
			for(int j = 0; j < this.N; j++)
				matrix[i][j] = "0";
		
		matrix[0][0] = "---";
		int i;
		int j = 0;
		
		for(i = 1; i <= s.size(); i++ ) {
			matrix[i][0]=(String) s.get(j);
			//System.out.println(matrix[i][0]);
			j++;
		}
		i = 0;
		for(j = 1; j <= s.size(); j++ ) {
			matrix[0][j]=(String) s.get(i);
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
	
	public void AddUA(String user_id, String role_id) {
		for(int i = 1; i < this.M; i++ )
			if (matrix[i][0].equals(user_id))//finds specific user, in column with index 0
				for(int j = 1; j < this.N; j++)
					if (matrix[0][j].equals(role_id))//finds specific role, in row with index 0
					//{	System.out.println(matrix[0][j]);
						matrix[i][j] = "1";
						//System.out.println(matrix[i][j]);}//matches the pair by putting an edge, value 1
					//else System.out.println(matrix[0][j]);
	}

	public void AddRH(String role_id1, String role_id2) {
		for(int i = 1; i < this.M; i++ )
			if (matrix[i][0].equals(role_id1))//finds specific role, in column with index 0
				for(int j = 1; j < this.N; j++)
					if (matrix[0][j].equals(role_id2))//finds specific role, in row with index 0
						matrix[i][j] = "1";	//matches the pair by putting an edge, value 1
	}
	public void AddPA(String role_id, String permission_id) {
		for(int i = 1; i < this.M; i++ )
			if (matrix[i][0].equals(role_id))//finds specific user, in column with index 0
				for(int j = 1; j < this.N; j++)
					if (matrix[0][j].equals(permission_id))//finds specific role, in row with index 0
						matrix[i][j] = "1";	//matches the pair by putting an edge, value 1
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


	@Override
	public void generateOutput() throws IOException {

		FileWriter file;
		file = new FileWriter("C:\\Users\\komlen\\Desktop\\RbacGraphMatrix.txt");
		
		BufferedWriter buffWriter = new BufferedWriter(file);
		String newline = System.getProperty("line.separator");
		
		for (int i = 0; i < this.M; i++)
			{
			for (int j = 0; j < this.N; j++){
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
