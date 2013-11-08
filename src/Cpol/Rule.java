package Cpol;

import PDP_SDP.Session;

//class Rule is basically mapping from a Session in other implementations
//in Cpol we have Rule in other approaches we have Session
//analogy is the same
public class Rule {
	//extends Session because pdp and spd in their methods have session
	//make Session EMPTY interface? and rename actual Session to something else and implement interface
	public static int rule_id;
	public int id;	
	
	public String owner; //owner will remain unused, since the system is the owner
	//in our implementation licence will be session, however since we represent 
	//users as sessions, licencee might as well be user_id, since we map Rule to Session
	public String licencee; 
	public AccessToken accesstoken;
	public boolean condition; //will be used to tell if the cache is valid or not
	
	public Rule(String s_id){
		this.setId(this.rule_id);
		this.rule_id ++;
	}
	public Rule(String s_id, AccessToken A){
		this.setLicencee(s_id);
		this.setAccesstoken(A);
		this.setCondition(true);
		this.setId(this.rule_id);		
		this.rule_id ++;
	}
	
	public Rule(int id, String owner, String licence, AccessToken A, boolean condition){
		this.setId(id);
		this.setLicencee(licence);
		this.setAccesstoken(A);
		this.setCondition(condition);
		this.setOwner(owner);
	}
	
	//these methods from original Cpol's implementation we will map to initiate_session and destory_session form our other approaches
	//so now they will actually be in SDP_Cpol and PDP_Cpol instead
	public void AddRule(int requester, String owner, String licence, AccessToken A, boolean condition){
		//this is basically in one way constructor
		//first check if this could be done
		if(this.id == requester){
			this.setAccesstoken(A);
			this.setLicencee(licence);
			this.setCondition(condition);
		}
	}
	public boolean RemoveRule(){
		//we simply set AccessToken to null and Condition to false
		this.setAccesstoken(null);
		this.setCondition(false);
		return true;
		}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLicencee() {
		return licencee;
	}
	//this method should be banned to use, since user could not change his own id
	public void setLicencee(String licencee) {
		this.licencee = licencee;
	}

	public AccessToken getAccesstoken() {
		return accesstoken;
	}

	public void setAccesstoken(AccessToken accesstoken) {
		this.accesstoken = accesstoken;
	}

	public boolean getCondition() {
		return condition;
	}

	public void setCondition(boolean condition) {
		this.condition = condition;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
}
