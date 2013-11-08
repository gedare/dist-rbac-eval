package Generator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;


public class RBAConfiguration {
	public static int Number_of_Users;
	public static int Number_of_Roles;
	public static int Number_of_Permissions;
	public static int Roles_per_User;
	public static int Roles_per_Permission;
	public static int Depth_of_RH;
	public static int Shape_of_RH; //0: rectangle, 1: pyramid, 2: inverse pyramid, 3: diamond
	public static int Nature_of_RH; //0: Stanford, 1: Hybrid
	public static int R_Connectivity; //0: random, 1: uniform
	public static int U_Connectivity; //0: random, 1: uniform, 2: exactly 1
	public static int P_Connectivity; //0: random, 1: uniform, 2: exactly 1
	public static HashMap<String, HashSet<String>> users;
	public static HashMap<String, HashSet<String>>[] roles;
	public static HashSet<String> permissions;
	public static void main(String[] args) throws IOException {
		try {
			Number_of_Users = Integer.parseInt(args[0]);
			Number_of_Roles = Integer.parseInt(args[1]);
			Number_of_Permissions = Integer.parseInt(args[2]);
			Roles_per_User = Integer.parseInt(args[3]);
			Roles_per_Permission = Integer.parseInt(args[4]);
			Depth_of_RH = Integer.parseInt(args[5]);
			Shape_of_RH = Integer.parseInt(args[6]);
			Nature_of_RH = Integer.parseInt(args[7]);
			R_Connectivity = Integer.parseInt(args[8]);
			U_Connectivity = Integer.parseInt(args[9]);
			P_Connectivity = Integer.parseInt(args[10]);
			if(Nature_of_RH > 1 || Nature_of_RH < 0) {
				System.out.println("ERROR: Nature_of_RH= " + Nature_of_RH + " is not supported\n");
				errorMessage();
				System.exit(0);
			}
			else if(R_Connectivity > 1 || R_Connectivity < 0) {
				System.out.println("ERROR: R_Connectivity= " + R_Connectivity + " is not supported\n");
				errorMessage();
				System.exit(0);
			}
			else if(U_Connectivity > 2 || R_Connectivity < 0) {
				System.out.println("ERROR: U_Connectivity= " + U_Connectivity + " is not supported\n");
				errorMessage();
				System.exit(0);
			}
			else if(P_Connectivity > 2 || P_Connectivity < 0) {
				System.out.println("ERROR: P_Connectivity= " + P_Connectivity + " is not supported\n");
				errorMessage();
				System.exit(0);
			}
			else if(Depth_of_RH > Number_of_Roles) {
				System.out.println("ERROR: Depth of role hierarchy cannot be greater than the number of roles");
				errorMessage();
				System.exit(0);
			}
		} catch(Exception e) {
			errorMessage();
			System.exit(0);
		}
		users = new HashMap();
		roles = new HashMap[Depth_of_RH];
		for(int i = 0; i < Depth_of_RH; i++) {
			roles[i] = new HashMap();
		}
		switch(Shape_of_RH) {
		case 0:
			rectangle();
			break;
		case 1:
			pyramid(1);
			break;
		case 2:
			pyramid(2);
			break;
		case 3:
			diamond();
			break;
		}
		permissions = new HashSet();
		for(int i = 0; i < Number_of_Users; i++) {
			users.put("U" + i, new HashSet());
		}
		for(int i = 0; i < Number_of_Permissions; i++) {
			permissions.add("P" + i);
		}
		if(Depth_of_RH > 1) {
			if(R_Connectivity == 0) {
				if(Nature_of_RH == 0) {
					Random randomGenerator = new Random(new GregorianCalendar().getTimeInMillis());
					for(int i = 0; i < Depth_of_RH-1; i++) {
						for(int j = 0; j < roles[i].size(); j++) {
							for(int k = 0; k < 5; k++) {
								roles[i].get("R" + i + "_" + j).add("R" + (i+1) + "_" + randomGenerator.nextInt(roles[i+1].size()));
							}
						}
					}
					for(int i = 1; i < Depth_of_RH; i++) {
						for(int j = 0; j < roles[i].size(); j++) {
							for(int k = 0; k < 5; k++) {
								roles[i-1].get("R" + (i-1) + "_" + randomGenerator.nextInt(roles[i-1].size())).add("R" + i + "_" + j);
							}
						}
					}
				}
				else if(Nature_of_RH == 1) {
					Random randomGenerator = new Random(new GregorianCalendar().getTimeInMillis());
					Random randomGenerator2 = new Random(new GregorianCalendar().getTimeInMillis());
					for(int i = 0; i < Depth_of_RH-1; i++) {
						for(int j = 0; j < roles[i].size(); j++) {
							for(int k = 0; k < 5; k++) {
								long range = (long)(Depth_of_RH-1) - (long)(i+1) + 1;
							    // compute a fraction of the range, 0 <= frac < range
							    long fraction = (long)(range * randomGenerator2.nextDouble());
							    int randomNumber =  (int)(fraction + i+1);
								roles[i].get("R" + i + "_" + j).add("R" + randomNumber + "_" + randomGenerator.nextInt(roles[i+1].size()));
							}
						}
					}
					for(int i = 1; i < Depth_of_RH; i++) {
						for(int j = 0; j < roles[i].size(); j++) {
							for(int k = 0; k < 5; k++) {
								int rand2 = randomGenerator2.nextInt(i);
								int rand = randomGenerator.nextInt(roles[rand2].size());
								roles[rand2].get("R" + rand2 + "_" + rand).add("R" + i + "_" + j);
							}
						}
					}
				}
			}
			else if(R_Connectivity == 1) {
				if(Nature_of_RH == 0) {
					int reps = 5;
					for(int i = 0; i < Depth_of_RH-1; i++) {
						for(int j = 0; j < roles[i].size(); j++) {
							for(int k = 0; k < reps; k++) {
								roles[i].get("R" + i + "_" + j).add("R" + (i+1) + "_" + (j*reps+k)%roles[i+1].size());
							}
						}
					}
				}
				else if(Nature_of_RH == 1) {
					int reps = Depth_of_RH-1;
					for(int i = 0; i < Depth_of_RH-1; i++) {
						for(int j = 0; j < roles[i].size(); j++) {
							for(int k = 0; k < reps; k++) {
								roles[i].get("R" + i + "_" + j).add("R" + ((j*reps+k)%(Depth_of_RH-1-i)+i+1) + "_" + (j*reps+k)%roles[i+1].size());
							}
						}
					}
				}
			}
			if(U_Connectivity == 0) {
				if(Nature_of_RH == 0) {
					Random randomGenerator = new Random(new GregorianCalendar().getTimeInMillis());
					for(int j = 0; j < users.size(); j++) {
						for(int k = 0; k < 5; k++) {
							users.get("U" + j).add("R0_" + randomGenerator.nextInt(roles[0].size()));
						}
					}
					for(int j = 0; j < roles[0].size(); j++) {
						for(int k = 0; k < 5; k++) {
							users.get("U" + randomGenerator.nextInt(users.size())).add("R0_" + j);
						}
					}
				}
				else if(Nature_of_RH == 1) {
					Random randomGenerator = new Random(new GregorianCalendar().getTimeInMillis());
					Random randomGenerator2 = new Random(new GregorianCalendar().getTimeInMillis());
					for(int j = 0; j < users.size(); j++) {
						for(int k = 0; k < 5; k++) {
							int rand = randomGenerator2.nextInt(Depth_of_RH);
							users.get("U" + j).add("R" + rand +"_" + randomGenerator.nextInt(roles[rand].size()));
						}
					}
					for(int j = 0; j < roles[0].size(); j++) {
						for(int k = 0; k < 5; k++) {
							users.get("U" + randomGenerator.nextInt(users.size())).add("R0_" + j);
						}
					}
				}
			}
			else if(U_Connectivity == 1) {
				if(Nature_of_RH == 0) {
					for(int j = 0; j < users.size(); j++) {
						for(int k = 0; k < Roles_per_User; k++) {
							users.get("U" + j).add("R0_" + (j*Roles_per_User+k)%roles[0].size());
						}
					}
				}
				else if(Nature_of_RH == 1) {
					int k = 0;
					for(int j = 0; j < users.size(); j++) {
						for(int l = 0; l < Roles_per_User; l++) {
							users.get("U" + j).add("R" + (k%Depth_of_RH) + "_" + (k/Depth_of_RH)%roles[k%Depth_of_RH].size());
							k++;
						}
					}
				}
			}
			if(P_Connectivity == 0) {
				if(Nature_of_RH == 0) {
					Random randomGenerator = new Random(new GregorianCalendar().getTimeInMillis());
					for(int j = 0; j < roles[Depth_of_RH-1].size(); j++) {
						for(int k = 0; k < 5; k++) {
							roles[Depth_of_RH-1].get("R" + (Depth_of_RH-1) + "_" + j).add("P" + randomGenerator.nextInt(Number_of_Permissions));
						}
					}
					for(int i = 0; i < Number_of_Permissions; i++) {
						for(int k = 0; k < 5; k++) {
							roles[Depth_of_RH-1].get("R" + (Depth_of_RH-1) + "_" + randomGenerator.nextInt(roles[Depth_of_RH-1].size())).add("P" + i);
						}
					}
				}
				else if(Nature_of_RH == 1) {
					Random randomGenerator = new Random(new GregorianCalendar().getTimeInMillis());
					Random randomGenerator2 = new Random(new GregorianCalendar().getTimeInMillis());
					for(int j = 0; j < roles[Depth_of_RH-1].size(); j++) {
						for(int k = 0; k < 5; k++) {
							int rand = randomGenerator2.nextInt(Depth_of_RH);
							roles[Depth_of_RH-1].get("R" + (Depth_of_RH-1) + "_" + j).add("P" + randomGenerator.nextInt(Number_of_Permissions));
							roles[rand].get("R" + rand + "_" + j%roles[rand].size()).add("P" + randomGenerator.nextInt(Number_of_Permissions));
						}
					}
					for(int j = 0; j < Number_of_Permissions; j++) {
						for(int k = 0; k < 5; k++) {
							int rand = randomGenerator.nextInt(Depth_of_RH);
							int rand2 = randomGenerator2.nextInt(roles[rand].size());
							roles[rand].get("R" + rand + "_" + rand2).add("P" + j);
						}
					}
				}
			}
			else if(P_Connectivity == 1) {
				if(Nature_of_RH == 0) {
					int l = 0;
					for(int j = 0; j < permissions.size(); j++) {
						for(int k = 0; k < Roles_per_Permission; k++) {
							roles[Depth_of_RH-1].get("R" + (Depth_of_RH-1) + "_" + l%roles[Depth_of_RH-1].size()).add("P" + j);
							l++;
						}
					}
				}
				else if(Nature_of_RH == 1) {
					int k = 0;
					for(int j = 0; j < permissions.size(); j++) {
						for(int l = 0; l < Roles_per_Permission; l++) {
							roles[k%Depth_of_RH].get("R" + (k%Depth_of_RH) + "_" + (k/Depth_of_RH)%roles[k%Depth_of_RH].size()).add("P" + j);
							k++;
						}
					}
				}
			}
		}
		else if(Depth_of_RH == 1) {
			if(U_Connectivity == 0 && P_Connectivity == 0) {
				Random randomGenerator = new Random(new GregorianCalendar().getTimeInMillis());
				for(int j = 0; j < users.size(); j++) {
					for(int k = 0; k < 5; k++) {
						users.get("U" + j).add("R0_" + randomGenerator.nextInt(roles[0].size()));
					}
				}
				for(int j = 0; j < roles[0].size(); j++) {
					for(int k = 0; k < 5; k++) {
						users.get("U" + randomGenerator.nextInt(users.size())).add("R0_" + j);
					}
				}
				for(int j = 0; j < roles[Depth_of_RH-1].size(); j++) {
					for(int k = 0; k < 5; k++) {
						roles[Depth_of_RH-1].get("R" + (Depth_of_RH-1) + "_" + j).add("P" + randomGenerator.nextInt(Number_of_Permissions));
					}
				}
				for(int i = 0; i < Number_of_Permissions; i++) {
					for(int k = 0; k < 5; k++) {
						roles[Depth_of_RH-1].get("R" + (Depth_of_RH-1) + "_" + randomGenerator.nextInt(roles[Depth_of_RH-1].size())).add("P" + i);
					}
				}
			}
			else if(U_Connectivity == 1 && P_Connectivity == 1) {
				for(int j = 0; j < users.size(); j++) {
					for(int k = 0; k < Roles_per_User; k++) {
						users.get("U" + j).add("R0_" + (j*Roles_per_User+k)%roles[0].size());
					}
				}
				int l = 0;
				for(int j = 0; j < permissions.size(); j++) {
					for(int k = 0; k < Roles_per_Permission; k++) {
						roles[Depth_of_RH-1].get("R" + (Depth_of_RH-1) + "_" + l%roles[Depth_of_RH-1].size()).add("P" + j);
						l++;
					}
				}
			}
		}
	    FileWriter fstream1 = new FileWriter("Data/rbac-state");
        BufferedWriter out1 = new BufferedWriter(fstream1);
        out1.write("\n#UA\n");
        Iterator iterator = users.keySet().iterator();
        while( iterator.hasNext() ){
        	String user = (String)iterator.next();
        	Iterator iterator2 = users.get(user).iterator();
        	boolean b = false;
        	while(iterator2.hasNext()) {
        		String item = (String) iterator2.next();
        		if(item.substring(0,1).equals("R")) {
	        		if(!b) {
	        			out1.write(user + " ");
	        			b = true;
	        		}
        			out1.write(item + " ");
        		}
        	}
        	if(b) {
        		out1.write("\n");
        	}
        }
        out1.write("#PA\n");
        for(int i = 0; i < Depth_of_RH; i++) {
        	iterator = roles[i].keySet().iterator();
            while( iterator.hasNext() ){
            	String role = (String)iterator.next();
            	Iterator iterator2 = roles[i].get(role).iterator();
            	boolean b = false;
            	while(iterator2.hasNext()) {
            		String item = (String) iterator2.next();
            		if(item.substring(0,1).equals("P")) {
                		if(!b) {
                			out1.write(role + " ");
                			b = true;
                		}
            			out1.write(item + " ");
            		}
            	}
            	if(b) {
            		out1.write("\n");
            	}
            }
        }
        out1.write("#RH\n");
        for(int i = 0; i < Depth_of_RH; i++) {
        	iterator = roles[i].keySet().iterator();
            while( iterator.hasNext() ){
            	String role = (String)iterator.next();
            	Iterator iterator2 = roles[i].get(role).iterator();
            	boolean b = false;
            	while(iterator2.hasNext()) {
            		String item = (String) iterator2.next();
            		if(item.substring(0,1).equals("R")) {
            			if(!b) {
            				out1.write(role + " ");
            				b = true;
            			}
            			out1.write(item + " ");
            		}
            	}
            	if(b) {
            		out1.write("\n");
            	}
            }
        }
        out1.close();
        System.out.println("Done");
	}
	public static void rectangle() {
		int j = 0;
		out:while(j < Number_of_Roles) {
			for(int i = 0; i < Depth_of_RH; i++) {
				if(j == Number_of_Roles) {
					break out;
				}
				int size = roles[i].size();
				roles[i].put("R" + i + "_" + size, new HashSet<String>());
				j++;
			}
		}
	}
	public static void pyramid(int shape) {
		int size = 0;
		for(int i = 0; i < Depth_of_RH; i++) {
			int interval=0;
			if(shape==1) {
				interval = (int)(((double)((double)Number_of_Roles/Depth_of_RH-1)/Depth_of_RH)*i+1);
				
			}
			else if(shape==2) {
				interval = (int)(((double)(1-(double)Number_of_Roles/Depth_of_RH)/Depth_of_RH)*i+(double)Number_of_Roles/Depth_of_RH);
			}
			for(int j = 0; j < interval; j++) {
				roles[i].put("R" + i + "_" + j, new HashSet<String>());
				
			}
			size = size + roles[i].size();
		}
		while(size < Number_of_Roles) {
			int j = roles[size%Depth_of_RH].size();
			roles[size%Depth_of_RH].put("R" + size%Depth_of_RH + "_" + j, new HashSet<String>());
			size++;
		}
		if(size > Number_of_Roles) {
			System.out.println("ERROR: Bad Size");
		}
	}
	public static void diamond() {
		int size = 0;
		for(int i = 0; i < Depth_of_RH/2; i++) {
			int interval = (int)(((double)((double)Number_of_Roles/Depth_of_RH-1)/Depth_of_RH)*i+1);
			for(int j = 0; j < interval; j++) {
				roles[i].put("R" + i + "_" + j, new HashSet<String>());
			}
			size = size + roles[i].size();
		}
		for(int i = Depth_of_RH/2; i < Depth_of_RH; i++) {
			int interval = (int)(((double)(1-(double)Number_of_Roles/Depth_of_RH)/Depth_of_RH)*i+(double)Number_of_Roles/Depth_of_RH);
			for(int j = 0; j < interval; j++) {
				roles[i].put("R" + i + "_" + j, new HashSet<String>());
			}
			size = size + roles[i].size();
		}
		while(size < Number_of_Roles) {
			int j = roles[size%Depth_of_RH].size();
			roles[size%Depth_of_RH].put("R" + size%Depth_of_RH + "_" + j, new HashSet<String>());
			size++;
		}
		if(size > Number_of_Roles) {
			System.out.println("ERROR: Bad Size");
		}
	}
	public static void errorMessage() {
		System.out.println("Usage: java RBAConfiguration #U #R #P R/U R/P Depth_of_RH Shape_of_RH Nature_of_RH R_Connectivity U_Connectivity P_Connectivity");
		System.out.println("Shape_of_RH: 0: rectangle, 1: pyramid, 2: inverse pyramid, 3: diamond");
		System.out.println("Nature_of_RH: 0: Stanford, 1: Hybrid");
		System.out.println("R_Connectivity: 0: random, 1: uniform");
		System.out.println("U_Connectivity: 0: random, 1: uniform");
		System.out.println("P_Connectivity: 0: random, 1: uniform");
	}

}
