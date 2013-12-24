package Cpol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashSet;

import PDP_SDP.PDP;
import PDP_SDP.PDP_Response;
import PDP_SDP.SDP_Data_Structure;
import PDP_SDP.Session;
import RbacGraph.RbacGraph;
import RbacGraph.RecordData;
import Structures.RoleVertex;
import Structures.UserVertex;
import Structures.Vertex;

public class PDP_Cpol extends PDP {

	public ArrayList Rules;//arraylist of rules' id
	
	
	//this method as mentioned in Rule class is actually RemoveRule method from Cpol's original implementation
	@Override
	public SDP_Data_Structure delete(int session_id) {
    try{
      Iterator iterator = Rules.iterator();
      while(iterator.hasNext()){
        int current_id = Integer.parseInt(iterator.next().toString());
        if(current_id == session_id) {
          //Rules.remove(current_id);
          iterator.remove();
          return null; //successfully removed
        }
      }
    } catch (Exception e) {}
    return null;
	}

	@Override
	public SDP_Data_Structure request(Session s, String[] roles,
      SDP_Data_Structure prepared) {
		PDP_Response P = (PDP_Response) prepared;
    if ( P == null )
      return null;
    ArrayList<RoleVertex> Roles = P.getRoles();

		HashSet current_permissions = new HashSet(P.getPermissions());

    //System.out.println(((Rule)s).getLicencee());
    //System.out.println(id);
    try{
      Rules.add(((Session_Cpol)s).getRule().getId());//add id to array list of rules' id
    }
    catch (Exception e) {}

    AccessToken A = new AccessToken(current_permissions);
    //create new cache key
    int key = ((Session_Cpol)s).getRule().getId();
    CacheKey cache_key = new CacheKey(key);
    //create new cache entry to be sent
    CacheEntry return_value = new CacheEntry(cache_key, A);

    return return_value;
  }
	
}
	
