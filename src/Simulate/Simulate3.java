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
import java.util.Iterator;
import java.util.StringTokenizer;
import java.lang.management.*;

import AccessMatrix.PDP_ACM;
import AccessMatrix.SDP_ACM;
//import BloomFilter.PDP_Bloom;
//import BloomFilter.SDP_Bloom;
import Cpol.PDP_Cpol;
import Cpol.SDP_Cpol;
import Cpol.Session_Cpol;
import HWDS.SDP_HWDS;
import HWDS.PDP_HWDS;
import HWDSBitSet.SDP_HWDSBitSet;
import HWDSBitSet.PDP_HWDSBitSet;
//import MapBitSet.SDP_MapBitSet;
//import MapBitSet.PDP_MapBitSet;
import PDP_SDP.PDP;
import PDP_SDP.SDP;
import PDP_SDP.SDP_Data_Structure;
import PDP_SDP.Session;
import RbacGraph.PDP_RbacGraph;
import RbacGraph.SDP_RbacGraph;
//import Recycling.PDP_Recycling;
//import Recycling.SDP_Recycling;
import Structures.PermissionVertex;
import Structures.RoleVertex;
import Structures.UserVertex;

//import jni.gem5Op;

// TODO: Make this a proper object already, enough with the static methods!
public class Simulate3 {
	private static final int MAX_ITERATIONS = 30;
  private static long[] times = new long[MAX_ITERATIONS];

  
  private static double compute_mean(int start, int stop)
  {
    double mean = 0;
    for(int j = start; j < stop; j++) {
      mean = mean + times[j];
    }
    return mean/(stop-start);
  }

  private static double compute_cov(int start, int stop)
  {
    double mean = compute_mean(start, stop);
    double s = 0;
    for(int j = start; j < stop; j++) {
      s = s + (times[j] - mean)*(times[j] - mean);
    }
    s = Math.sqrt(s/(stop-start-1));
    return s/mean;
  }

  private static boolean are_we_there_yet(int iteration, double covtmp)
      throws IOException {
    int iter = 4;
    if ( iteration < iter*5 ) // FIXME: hack to skip more of startup
      return false;
    
    ArrayList list = new ArrayList();
    int start = iteration - iter;
    int stop = iteration+1;
    double cov = compute_cov(start, stop);
    if(cov < covtmp ) {
      double mm = compute_mean(start, stop);
      System.out.println("cov: " + cov + "\tmean: " + mm + "\titeration: " + iteration);
      //System.out.println(java.util.Arrays.toString(times));
      FileWriter fstream1 = new FileWriter("Data/means", true);
      BufferedWriter out1 = new BufferedWriter(fstream1);
      out1.write(mm + " " + cov + "\n");
      out1.close();
      return true;
    } else {
    }
    
    if (iteration == MAX_ITERATIONS - 1) {
      for (int i = iter*3 /*FIXME as above so below*/; i <= iteration; i++) {
        if (are_we_there_yet(i, covtmp + 0.01))
          return true;
      }
    }

    return false;
  }

	public static void main(String[] args) throws IOException {
	//	java.lang.Compiler.disable();
    //System.loadLibrary("gem5OpJni");
		for(int i = 0; i < MAX_ITERATIONS; i++) {
			times[i] = 0;
		}
		int iterations;
    //gem5Op gem5 = new gem5Op();

			long initiation_time = System.nanoTime();

			int algorithm = 0;

			try {
				algorithm = Integer.parseInt(args[1]);
				if(algorithm > 7 || algorithm < 0) {
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
      System.out.println("algorithm " + algorithm);
			switch(algorithm) {
			case 0:
				pdp = new PDP_RbacGraph();
				break;
			case 1:
				pdp = new PDP_ACM();
				break;
			case 2:
//				pdp = new PDP_Recycling();
				break;
			case 3:
				pdp = new PDP_Cpol();
				break;
        /*
			case 4:
				try {
					int m = Integer.parseInt(args[2]);
					int Elen = Integer.parseInt(args[3]);
					int max_depth = Integer.parseInt(args[4]);
					pdp = new PDP_Bloom(m, Elen, max_depth);
				}
				catch(Exception e) {
					System.out.println(errorMessage());
					System.exit(0);
				}
				break;
      case 5:
        pdp = new PDP_MapBitSet();
        break;*/
      case 6:
        pdp = new PDP_HWDSBitSet();
        break;
      case 7:
        pdp = new PDP_HWDS();
        break;
			}

			String filename = args[0];//construct file name
			FileReader fReader = new FileReader(filename);

			pdp.init(fReader);
			//RBAC g = ((PDP_RbacGraph)pdp).getG();
			//RBAC g = ((PDP_ACM)pdp).getG();
			fReader.close();

      // go through the instructions file one time to ... 
      HashMap<ArrayList<String>, SDP_Data_Structure> Prepared_Sessions;
      Prepared_Sessions = new HashMap();
			BufferedReader f;
      f = new BufferedReader(new FileReader("Data/instructions"));
			String line;
      ArrayList<String> Instructions = new ArrayList<String>();
			while((line = f.readLine()) != null) {
        Instructions.add(line);
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
          temp.add(user);

					SDP_Data_Structure prepared = pdp.prepare(user, roles);
          Prepared_Sessions.put(temp, prepared);
				}
      }
			f.close();


    for(iterations = 0; iterations < MAX_ITERATIONS; iterations++) {
      int numAccessChecks = 0;
     	HashMap<Integer,Integer> sessions = new HashMap();

      switch(algorithm) {
			case 0:
				sdp = new SDP_RbacGraph(pdp);
				break;
			case 1:
				sdp = new SDP_ACM(pdp);
				break;
			case 2:
//				sdp = new SDP_Recycling(pdp);
				break;
			case 3:
				sdp = new SDP_Cpol(pdp);
				break;
			case 4:
//				sdp = new SDP_Bloom(pdp);
				break;
      case 5:
//        sdp = new SDP_MapBitSet(pdp);
        break;
      case 6:
        sdp = new SDP_HWDSBitSet(pdp);
        break;
      case 7:
        sdp = new SDP_HWDS(pdp);
        break;
			}
			pdp.setSDP(sdp);

			long initiation_request_time = 0;
			long access_request_time = 0;
			long destroy_time = 0;
      Iterator iterator = Instructions.iterator();
			while(iterator.hasNext()) {
				line = (String) iterator.next();
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
          temp.add(user);

          SDP_Data_Structure prepared = Prepared_Sessions.get(temp);

					long time1 = System.nanoTime();
					int session = sdp.initiate_session_request(user, roles, prepared);
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
								long time2 = System.nanoTime();
														//		long taskUserTimeNano    = getUserTime( ) - startUserTimeNano;
						//		long taskSystemTimeNano  = getSystemTime( ) - startSystemTimeNano;
							 numAccessChecks++;
							//	out1.write("\n\n" + "new result: " + new Benchmark(task).toStringFull());
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

      System.out.println("Times\t" +
          initiation_request_time + "\t" +
          times[iterations] + "\t" +
          destroy_time);
      times[iterations] = times[iterations] / numAccessChecks;

      if (are_we_there_yet(iterations, 0.02)) {
        break;
      }
      reset();
    }
    
    //long end_time = System.nanoTime();
		//long difference = end_time - initiation_time;

		/*	System.out.println("Access Request Time:\t\t" + access_request_time);//time from access requests


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
		"\t4: Cascade Bloom Filter\n" +
    "\t5: Map with BitSet\n" +
		"\n\t\tIn case 4:\n" +
		"\t\tUsage: java Simulate2 4 <m> <Elen> <MAX_DEPTH>";
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

