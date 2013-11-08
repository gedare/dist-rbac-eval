package Cpol;

public class CacheKey {
	
	public int session_id;

	public CacheKey(int key){
		this.setKey(key);
	}
	
	public int getKey() {
		return session_id;
	}

	public void setKey(int key) {
		session_id = key;
	}
	
	
}
