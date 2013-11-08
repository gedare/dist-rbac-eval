package Simulate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import AccessMatrix.PDP_ACM;
import AccessMatrix.SDP_ACM;
import BloomFilter.PDP_Bloom;
import BloomFilter.SDP_Bloom;
import Cpol.PDP_Cpol;
import Cpol.SDP_Cpol;
import PDP_SDP.PDP;
import PDP_SDP.SDP;
import RbacGraph.PDP_RbacGraph;
import RbacGraph.SDP_RbacGraph;
import Recycling.PDP_Recycling;
import Recycling.SDP_Recycling;

public class Simulate {

	public static void main(String[] args) throws IOException{
		
		long initiation_time = System.nanoTime();

		//PDP pdp = new PDP_RbacGraph();
		//PDP pdp = new PDP_ACM();
		//PDP pdp = new PDP_Recycling();
		//PDP pdp = new PDP_Cpol();
		PDP pdp = new PDP_Bloom();

			
		//SDP sdp = new SDP_RbacGraph(pdp);
		//SDP sdp = new SDP_ACM(pdp);
		//SDP sdp = new SDP_Recycling(pdp);
		//SDP sdp = new SDP_Cpol(pdp);
		SDP sdp = new SDP_Bloom(pdp);
		
		String filename = args[0];//construct file name
		FileReader fReader = new FileReader(filename);
		
		pdp.setSDP(sdp);
		pdp.init(fReader);
		//RBAC g = ((PDP_RbacGraph)pdp).getG();
		//RBAC g = ((PDP_ACM)pdp).getG();

		//reads users' and roles' ids from 2 files
		FileReader users_file = new FileReader("users_samples.txt");
		FileReader roles_file = new FileReader("roles_samples.txt");

		BufferedReader users_reader = new BufferedReader(users_file);
		BufferedReader roles_reader = new BufferedReader(roles_file);
		
		String line = users_reader.readLine();
		String[] users = line.split(" ");
		
		class Structure{
			String[] roles;
			public Structure(String[] roles){
				this.roles = roles;
			}
			public void SetRoles(String[] roles){
				this.roles = roles;
			}
			public String[] GetRoles(){
				return this.roles;
			}
		}
		Structure[] Roles = new Structure[users.length];
		int i = 0;
		while ((line = roles_reader.readLine())!= null){
			String[] current = line.split(" ");
			Roles[i] = new Structure(current);
			//Roles[i].SetRoles(current);
			i++;
		}
		
		//initiation phase
		int[] s = new int[users.length];//sessions ids
		//each user initiate session with all his roles
		
		long initiation_request_time = System.currentTimeMillis();
		
		//System.out.println(users.length);
		//System.out.println(Roles.length);
		
		for(i = 0; i < users.length; i++){
			s[i] = sdp.initiate_session_request(users[i], Roles[i].GetRoles());
		}
		//System.out.print("Starting number of sessions - ");//for testing purposes only
		//System.out.println(((SDP_Cpol)sdp).getNumberOfSessions());//for testing purposes only
		//sdp.g.generateOutput();//for testing purposes only
		long access_request_time = System.currentTimeMillis();
		int k=0;int l=0;int m=0;
		for(i = 0; i < users.length; i++) {
			for(int j = 0; j < 5; j++){//j goes up to total number of permissions for that particular case
				if(s[i]!=-1){
					sdp.access_request(i, j);
					//k++;
					if(sdp.access_request(i, j)) 
						//l++;
						System.out.println("APPROVED " + i + " "+ j);
					else //m++;
						System.out.println("DENIED " + i + " "+ j);
				}
				else System.out.println(i + "was unothorized");
			}
		}
		for(i = 0; i < users.length/10; i++){
			sdp.destroy_session(i);
		}
		//System.out.print("Number of sessions after deletions - ");//for testing purposes only
		//System.out.println(((SDP_Cpol)sdp).getNumberOfSessions());//for testing purposes only
		
		//System.out.println(k);
		//System.out.println(l);
		//System.out.println(m);

		/*
		//((RbacGraph)g).Output();
		String[] roles = new String[3];
		roles[0] = "r_R1";
		roles[1] = "r_R2";
		roles[2] = "r_R3";
		
		String[] roles1 = new String[2];
		//roles1[0] = "r_3";
		roles1[0] = "r_R2";
		roles1[1] = "r_R7";
		
		String[] roles2 = new String[1];
		roles2[0] = "r_R3";
		
		//long pocetak = System.currentTimeMillis();

		int[] s = new int[3];
		
		s[0] = sdp.initiate_session_request("u_U1", roles);

		s[1] = sdp.initiate_session_request("u_U45", roles1);
		
		s[2] = sdp.initiate_session_request("u_U1000", roles2);

		long pocetak = System.currentTimeMillis();

		for(int i=0; i<3; i++){
			if(s[i]!=-1){
				if(sdp.access_request(i, 0)) System.out.println("APPROVED");
				else System.out.println("DENIED");
				if(sdp.access_request(i, 1)) System.out.println("APPROVED");
				else System.out.println("DENIED");
				if(sdp.access_request(i, 2)) System.out.println("APPROVED");
				else System.out.println("DENIED");
				if(sdp.access_request(i, 3)) System.out.println("APPROVED");
				else System.out.println("DENIED");
				if(sdp.access_request(i, 4)) System.out.println("APPROVED");
				else System.out.println("DENIED");
				if(sdp.access_request(i, 5)) System.out.println("APPROVED");
				else System.out.println("DENIED");
			}
		}
		//System.out.println(sdp.users.size());
		 
		*/
		long end_time = System.currentTimeMillis();

		System.out.println(end_time - access_request_time);//time from access requests

		System.out.println(end_time - initiation_request_time);//time from requests initiation

		System.out.println(end_time - initiation_time);//time from the beginning
		
	}

}
