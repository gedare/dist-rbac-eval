package Cpol;

import PDP_SDP.SDP_Data_Structure;

//CacheEntry will be basically the structure we will return as reply from PDP
public class CacheEntry implements SDP_Data_Structure {
	
	public CacheKey key;
	public AccessToken A;
	public boolean condition;
	
	public CacheEntry(CacheKey key, AccessToken A){
		this.key = key;
		this.A = A;
	}
	
	public CacheKey getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key.setKey(key);
	}
	public AccessToken getA() {
		return A;
	}
	public void setA(AccessToken a) {
		A = a;
	}
	public boolean getCondition() {
		return condition;
	}
	public void setCondition(boolean condition) {
		this.condition = condition;
	}

}
