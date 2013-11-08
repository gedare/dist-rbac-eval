package Cpol;

import java.util.ArrayList;
import java.util.Iterator;

public class Cache {

	public ArrayList<CacheEntry> cache;
	
	public Cache() {
		this.cache = new ArrayList<CacheEntry>();
	}
	public Cache(ArrayList<CacheEntry> cache) {
		this.cache = cache;
	}
	public ArrayList<CacheEntry> getCache() {
		return cache;
	}
	public void setCache(ArrayList<CacheEntry> cache) {
		this.cache = cache;
	}
	public void addEntry(CacheEntry e) {
		this.cache.add(e);
	}
	public void removeEntry(CacheEntry e) {
		this.cache.remove(e);
	}
	public void invalidateEntry(int session_id) {
		Iterator<CacheEntry> iterator = this.cache.iterator();
		while(iterator.hasNext()){
			CacheEntry current_cache_entry = iterator.next();
			//first check if that entry is valid (condition check)
			if(current_cache_entry.condition){
				//check if the key == session_id
				if(current_cache_entry.getKey().getKey() == session_id){
					//remove it from a cache - invalidate it
					current_cache_entry.setCondition(false);
					//or just invalidate its condition? without removal?
					this.cache.remove(current_cache_entry);
					break;
				}
			}
		}
	}
	public boolean isEntry(int session_id, int permission_id) {

		Iterator<CacheEntry> iterator = this.cache.iterator();
		while(iterator.hasNext()){
			CacheEntry current_cache_entry = iterator.next();
			//first check if that entry is valid (condition check)
			if(current_cache_entry.condition){
				//check if the key == session_id
				if(current_cache_entry.getKey().getKey() == session_id){
					//get that particular cache entry's access token, 
					//and check if permission_id is in access token's permissions
					AccessToken A = current_cache_entry.getA();
					//if yes return true, otherwise return false
					if(A.checkPermission(permission_id)) return true;
				}
			}
		}
		return false;
	}
}
