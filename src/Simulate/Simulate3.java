package Simulate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.lang.management.*;

import AccessMatrix.PDP_ACM;
import AccessMatrix.SDP_ACM;
import BloomFilter.PDP_Bloom;
import BloomFilter.SDP_Bloom;
import Cpol.PDP_Cpol;
import Cpol.SDP_Cpol;
import Cpol.Session_Cpol;
import PDP_SDP.PDP;
import PDP_SDP.SDP;
import PDP_SDP.Session;
import RbacGraph.PDP_RbacGraph;
import RbacGraph.SDP_RbacGraph;
import Recycling.PDP_Recycling;
import Recycling.SDP_Recycling;
import Structures.PermissionVertex;
import Structures.RoleVertex;
import Structures.UserVertex;


// TODO: Make this a proper object already, enough with the static methods!
public class Simulate3 {
	private static final int MAX_ITERATIONS = 30;

  
  private static double compute_mean(ArrayList<Long> nums, int start, int stop)
  {
    double mean = 0;
    for(int j = start; j < stop; j++) {
      mean = mean + nums.get(j);
    }
    return mean/(stop-start);
  }

  private static double compute_cov(ArrayList<Long> nums, int start, int stop)
  {
    double mean = compute_mean(nums, start, stop);
    double s = 0;
    for(int j = start; j < stop; j++) {
      s = s + (nums.get(j) - mean)*(nums.get(j) - mean);
    }
    s = Math.sqrt(s/(stop-start-1));
    return s/mean;
  }

  private static boolean are_we_there_yet(int iteration)
      throws IOException {
    int iter = 4;
    if ( iteration < iter )
      return false;

    ArrayList<Long> nums = new ArrayList<Long>();
    for (int i = 0; i <= iteration; i++) {
      BufferedReader f = new BufferedReader(new FileReader("Data/timinginfo" + iteration));
      String line;
      while((line=f.readLine()) != null) {
        nums.add(Long.parseLong(line));
      }
      f.close();
    }
    Collections.reverse(nums);
    
    ArrayList list = new ArrayList();
    double covtmp = 0.02;
    double cov = compute_cov(nums, 0, iter);
    if(cov < covtmp) {
      double mm = compute_mean(nums, 0, iter);
      FileWriter fstream1 = new FileWriter("Data/means", true);
      BufferedWriter out1 = new BufferedWriter(fstream1);
      out1.write(mm + "\n");
      out1.close();
      return true;
    } else {
      //System.out.println("cov: " + cov);
    }

    if( iteration == MAX_ITERATIONS - 1 ) {
      double min_cov = cov;
      int start = 0;
      int stop = iter;
      for (int i = 0; i < (iteration - iter); i++) {
        cov = compute_cov(nums, i, i + iter);
        if ( cov < min_cov ) {
          min_cov = cov;
          start = i;
          stop = i + iter;
        }
      }
      double mm = compute_mean(nums, start, stop);
      FileWriter fstream1 = new FileWriter("Data/means", true);
      BufferedWriter out1 = new BufferedWriter(fstream1);
      out1.write(mm + "\n");
      out1.close();
   }
    return false;
  }

	public static void main(String[] args) throws IOException {
	//	java.lang.Compiler.disable();
		long[] times = new long[MAX_ITERATIONS];
		for(int i = 0; i < MAX_ITERATIONS; i++) {
			times[i] = 0;
      FileWriter fstream1 = new FileWriter("Data/timinginfo" + i);
      BufferedWriter out1 = new BufferedWriter(fstream1);
      out1.write("");
      out1.close();
		}
		int iterations;
    for(iterations = 0; iterations < MAX_ITERATIONS; iterations++) {
      FileWriter fstream1 = new FileWriter("Data/timinginfo" + iterations, true);
      BufferedWriter out1 = new BufferedWriter(fstream1);
      int numAccessChecks = 0;
     	HashMap<Integer,Integer> sessions = new HashMap();

			long initiation_time = System.nanoTime();

			int algorithm = 0;

			try {
				algorithm = Integer.parseInt(args[1]);
				if(algorithm > 4 || algorithm < 0) {
					System.out.println(errorMessage());
					System.exit(0);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				System.out.println(errorMessage());
				System.exit(0);
			}
			PDP pdp = null;
			SDP sdp = null;
			switch(algorithm) {
			case 0:
				pdp = new PDP_RbacGraph();
				sdp = new SDP_RbacGraph(pdp);
				break;
			case 1:
				pdp = new PDP_ACM();
				sdp = new SDP_ACM(pdp);
				break;
			case 2:
				pdp = new PDP_Recycling();
				sdp = new SDP_Recycling(pdp);
				break;
			case 3:
				pdp = new PDP_Cpol();
				sdp = new SDP_Cpol(pdp);
				break;
			case 4:
				try {
					int m = Integer.parseInt(args[2]);
					int Elen = Integer.parseInt(args[3]);
					int max_depth = Integer.parseInt(args[4]);
					pdp = new PDP_Bloom(m, Elen, max_depth);
					sdp = new SDP_Bloom(pdp);
				}
				catch(Exception e) {
					System.out.println(errorMessage());
					System.exit(0);
				}
				break;
			}

			String filename = args[0];//construct file name
			FileReader fReader = new FileReader(filename);

			pdp.setSDP(sdp);
			pdp.init(fReader);
			//RBAC g = ((PDP_RbacGraph)pdp).getG();
			//RBAC g = ((PDP_ACM)pdp).getG();

			long initiation_request_time = 0;
			long access_request_time = 0;
			long destroy_time = 0;
			BufferedReader f = new BufferedReader(new FileReader("Data/instructions"));
			String line;
			while((line = f.readLine()) != null) {
				StringTokenizer token = new StringTokenizer(line, " ");
				String instruction = token.nextToken();
				if(instruction.equals("i")) {
					int itemID = Integer.parseInt(token.nextToken());
					String user = token.nextToken();
					ArrayList temp = new ArrayList();
					while(token.hasMoreTokens()) {
						temp.add(token.nextToken());
					}
					String [] roles = new String[temp.size()];
					for(int i = 0; i < roles.length; i++) {
						roles[i] = (String)temp.get(i);
					}
					long time1 = System.nanoTime();

					int session = sdp.initiate_session_request(user, roles);

					long time2 = System.nanoTime();
					initiation_request_time = initiation_request_time + time2 - time1;

					sessions.put(itemID, session);
				}
				else if(instruction.equals("a")) {
					int itemID = Integer.parseInt(token.nextToken());
					if(sessions.get(itemID) == null) {
						System.out.println("ERROR: The session you are trying to access does not exist");
					}
					else {
						while(token.hasMoreTokens()) {
							final int sessionid = sessions.get(itemID);
							final int permissionid = Integer.parseInt(token.nextToken());
						//	long startSystemTimeNano = getSystemTime( );
						//	long startUserTimeNano   = getUserTime( );
							
							//for(int i = 0; i < MAX_ITERATIONS; i++) {
								long time1 = System.nanoTime();
								sdp.access_request(sessionid, permissionid);
														//		long taskUserTimeNano    = getUserTime( ) - startUserTimeNano;
						//		long taskSystemTimeNano  = getSystemTime( ) - startSystemTimeNano;
							 numAccessChecks++;
							//	out1.write("\n\n" + "new result: " + new Benchmark(task).toStringFull());
								long time2 = System.nanoTime();
						/*		if(time2-time1>500000000*10) {
									System.out.println("Threshold for " + algorithm + " is: " + (time2-time1));
									System.exit(0);
								}*/
								
								access_request_time = time2 - time1;
								times[iterations] = times[iterations] + access_request_time;
							//}
					//		System.out.print(decision);	
				//		System.out.println(time2-time1);
						/*	
							if(decision) {
								System.out.println("APPROVED " + sessionid + " " + permissionid);
							}
							else {
								System.out.println("DENIED " + sessionid + " " + permissionid);
							}
							/* */
						}
					}
				}
				else if(instruction.equals("d")) {
					while(token.hasMoreTokens()) {
						int itemID = Integer.parseInt(token.nextToken());
						if(sessions.get(itemID) == null) {
							System.out.println("ERROR: The session you are trying to destroy does not exist");
						}
						else {
							int sessionid = sessions.get(itemID);

							long time1 = System.nanoTime();

							sdp.destroy_session(sessionid);

							long time2 = System.nanoTime();
							destroy_time = destroy_time + time2 - time1;

							sessions.remove(itemID);
						}
					}
				}
			}	//end of while loop

      times[iterations] = times[iterations] / numAccessChecks;
			out1.write(times[iterations] + "\n");
			f.close();
			fReader.close();
      out1.close();
      if (are_we_there_yet(iterations)) {
        break;
      }
    }
    
    //long end_time = System.nanoTime();
		//long difference = end_time - initiation_time;

		/*	System.out.println("Access Request Time:\t\t" + access_request_time);//time from access requests

			System.out.println("Initiation Request Time:\t" + initiation_request_time);//time from requests initiation

			System.out.println("Destroy Time:\t\t\t" + destroy_time);

			System.out.println("Total Running Time:\t\t" + difference);*/


			/*
			if(iterations == 0) {
			    System.out.println("Num Access Checks: "+numAccessChecks);
			}
			*/
	}

	public static String errorMessage() {
		String error = "Usage: java Simulate3 <RBAC Configuration file> <Algorithm ID>\n" +
		"<Algorithm ID>:\n" +
		"\t0: Rbac Graph\n" +
		"\t1: Access Matrix\n" +
		"\t2: Recycling\n" +
		"\t3: Cpol\n" +
		"\t4: Cascade Bloom Filter\n\n" +
		"In case 4:\n" +
		"Usage: java Simulate2 4 <m> <Elen> <MAX_DEPTH>";
		return error;
	}

	public static void reset() {
		(new Session_Cpol(null)).session_id = (new Session()).session_id = 0;
		(new PermissionVertex(null)).staticID = 0;
		(new RoleVertex(null)).staticID = 0;
		(new UserVertex(null)).staticID = 0;
		(new Cpol.Rule(null)).rule_id = 0;
		System.gc();
	}

}

