package Cpol;

import PDP_SDP.Session;

public class Session_Cpol extends Session {
	
	public Rule rule;

	public Session_Cpol(Rule rule){
		super();
		this.rule = rule;
	}
	
	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}
	
}
