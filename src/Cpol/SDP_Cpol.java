package Cpol;

import java.util.ArrayList;
import java.util.Iterator;

import PDP_SDP.PDP;
import PDP_SDP.SDP;
import PDP_SDP.SDP_Data_Structure;

public class SDP_Cpol extends SDP {

	public Cache cache;
	//public ArrayList<CacheEntry> cache; //SDP will contain cache
	//public ArrayList<Rule> rules; //sessions map to rules, so in this approach instead of sessions we will have rules
	public ArrayList<Session_Cpol> sessions;
	
	//public int deleted_sessions;//for testing purposes only
	
	public SDP_Cpol(PDP pdp) {
		super(pdp);
		this.cache = new Cache();
	}

	//public int getNumberOfSessions() {
		/*try{
			return this.rules.size();
		}
		catch (Exception e) {}
		return 0;
		*/
	//	return deleted_sessions;
	//}
	/*
	public ArrayList<CacheEntry> getCache() {
		return cache;
	}

	public void setCache(ArrayList<CacheEntry> cache) {
		this.cache = cache;
	}
	*/
	//this method is mapped GetAccess method from Cpol's original implementation
	@Override
	public boolean access_request(int session_id, int permission_id) {
		return this.cache.isEntry(session_id, permission_id);
	}
	
	//this method as mentioned in Rule class is actually AddRule method from Cpol's original implementation
	@Override
	public int initiate_session_request(String user_id, String[] roles,
      SDP_Data_Structure P) {
		//create empty AccessToken, later we will fill it up with permissions
		AccessToken A = new AccessToken();
		Rule R = new Rule(user_id);
		//just setting all the parameters
		int requester = R.getId();
		String owner = "System";
		String licence = user_id;
		boolean condition = false;//condition is false nothing in cache so far
		//call AddRule method
		R.AddRule(requester, owner, licence, A, condition);
		//** all this could have been done on easier way, I just wanted to use AddRule method
		//other way simply call the Rule constructor
		//Rule R = new Rule(user_id, A);
		Session_Cpol S = new Session_Cpol(R);//creates new session
		//what should we do!
		//go to pdp with these []roles and check if that user is assigned to them
		SDP_Data_Structure checker = this.pdp.request(S, roles, P);
		if(checker != null){
			((CacheEntry)checker).setCondition(true);//set it is valid entry
			this.g = checker;//should return only cache entry
			//add it to cache
			try{
				//this.cache.add((CacheEntry) checker);
				this.cache.addEntry((CacheEntry) checker);
			}
			catch (Exception e) {}
			//add rule to arraylist as well
			R.setAccesstoken(((CacheEntry)checker).getA());//first set its access token
			R.setCondition(true);//set it is valid
			try{
				//this.rules.add(R);//add
				this.sessions.add(S);//add it to list
			}
			catch (Exception e) {}
			return ((CacheEntry)checker).getKey().getKey();//returns int value
		}
		//else System.out.println("Unauthorised");
		return -1;
		//get their permissions, and put them in AccessToken within CacheEntry which will be returned
		//get CacheEntry's AccessToken and assign it to Rule R
		//add Rule R to list of rules
		//finally store that CacheEntry on SDP's cache
		
	}

	@Override
	public void destroy_session(int session_id) {
		//find cache entry with session_id
		//remove it from a cache, or rather set its condition to false
		this.cache.invalidateEntry(session_id);
		//same thing for Rules' list
		try{
		Iterator<Session_Cpol> it = this.sessions.iterator();
		while(it.hasNext()){
			Session_Cpol current_session = it.next();
			if(current_session.getRule().getId() == session_id){
					//remove it from a list
					current_session.getRule().setCondition(false);
					//or just invalidate its condition? without removal?
					this.sessions.remove(current_session);
					break;
				}
		}
		} catch (Exception e) {}
		
		
		//inform pdp so it can delete it from its list
		this.pdp.delete(session_id);
		//deleted_sessions++;//for testing purposes only
	}
	
}
