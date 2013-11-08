package Recycling;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.lang.reflect.Array;

import PDP_SDP.PDP;
import PDP_SDP.SDP;
import PDP_SDP.Session;


public class SDP_Recycling extends SDP{
	String[] allroles;
	HashMap<Integer,String[]> sessions;
	HashMap<String, Integer> counter;
	String permissionid;
	static HashMap<Integer, Set> cacheminus;
	static HashMap<Integer, ArrayList> cacheplus;
	public SDP_Recycling(PDP pdp) {
		super(pdp);
		// TODO Auto-generated constructor stub
		this.sessions = new HashMap();
		this.counter = new HashMap();
		this.cacheminus = new HashMap();
		this.cacheplus = new HashMap();
	}

	@Override
	public boolean access_request(int session_id, int permission_id) {
		allroles = sessions.get(session_id);
		boolean decision = false;
		List list = Arrays.asList(allroles);
		Set set = new HashSet(list);
		if(cacheminus.get(permission_id) != null && cacheminus.get(permission_id).containsAll(set)) {
			return false;
		}
		else if(cacheplus.get(permission_id) != null) {
			for(int i = 0; i < cacheplus.get(permission_id).size(); i++) {
				if(set.containsAll((Set)cacheplus.get(permission_id).get(i))) {
					return true;
				}
			}
		}
		String[] roles = new String[allroles.length+1];
	    for(int i = 0; i < allroles.length; i++) {
	    	roles[i] = allroles[i];
	    }
	    roles[roles.length-1] = "" + permission_id;
		decision = ((Decision)(new PDP_Recycling().request(null, roles))).isDecision();
		AddResponse(decision, permission_id);
		return decision;
	}

	@Override
	public int initiate_session_request(String user_id, String[] roles) {
		allroles = roles;
		Session session = new Session(user_id);
		sessions.put(session.id, roles);
		for(int i = 0; i < roles.length; i++) {
			if(counter.get(roles[i]) == null) {
				counter.put(roles[i], 1);
			}
			else {
				counter.put(roles[i], counter.get(roles[i]) + 1);
			}
		}
		return session.id;
	}

	public void destroy_session(int session_id) {
		String[] useroles = sessions.get(session_id);
		String[] removeroles;
		boolean removed = false;
		for(int i = 0; i < useroles.length; i++) {
			if(counter.get(useroles[i]) == 1) {
				Iterator it = cacheminus.keySet().iterator();
				while(it.hasNext()) {
					int key = (Integer)it.next();
					if(cacheminus.get(key).contains(useroles[i])) {
						HashSet set = new HashSet();
						set.addAll((HashSet) cacheminus.get(key));
						set.remove(useroles[i]);
						cacheminus.put(key, set);
						if(cacheminus.get(key).size() == 0) {
							it.remove();
						}
						removed = true;
						counter.remove(useroles[i]);
					}
				}
				if(!removed) {
					it = cacheplus.keySet().iterator();
					while(it.hasNext()) {
						int key = (Integer)it.next();
						ArrayList<HashSet> list = (ArrayList)cacheplus.get(key);
						for(int j = 0; j < list.size(); j++) {
							if(list.get(j).contains(useroles[i])) {
								list.get(j).remove(useroles[i]);
								if(list.get(j).size() == 0) {
									list.remove(j);
								}
								cacheplus.put(key, list);
								if(((ArrayList)cacheplus.get(key)).size() == 0) {
									it.remove();
								}
								counter.remove(useroles[i]);
							}
						}
					}
				}
				removed = false;
			}
		}
		sessions.remove(session_id);
	}
	public void AddResponse(boolean decision, int permission_id) {
		List list = Arrays.asList(allroles);
		Set set = new HashSet(list);
		if(!decision) {
			if(cacheplus.get(permission_id) != null) {
				for(int i = 0; i < cacheplus.get(permission_id).size(); i++){
					((Set)cacheplus.get(permission_id).get(i)).removeAll(set);
				}
			}
			if(cacheminus.get(permission_id) != null) {
				cacheminus.get(permission_id).addAll(set);
			}
			else {
				cacheminus.put(permission_id, set);
			}
		}
		else {
			if(cacheminus.get(permission_id) != null) {
				set.removeAll(cacheminus.get(permission_id));
				if(cacheplus.get(permission_id) != null) {
					cacheplus.get(permission_id).add(set);
				}
				else {
					ArrayList<Set> ls = new ArrayList();
					ls.add(set);
					cacheplus.put(permission_id, ls);
				}
			}
			else {
				if(cacheplus.get(permission_id) != null) {
					cacheplus.get(permission_id).add(set);
				}
				else {
					ArrayList<Set> ls = new ArrayList();
					ls.add(set);
					cacheplus.put(permission_id, ls);
				}
			}
		}
	}


}
