package HWDS;

import PDP_SDP.PDP;
import PDP_SDP.SDP;
import PDP_SDP.Session;

import java.util.ArrayList;
import java.util.Iterator;

import jni.gem5Op;
import hwds.*;

public class SDP_HWDS extends SDP {

  // A subset of the PDP's Sessions map.
  private HWDS hwds;

	public SDP_HWDS(PDP pdp) {
		super(pdp);
    hwds = new SimulatedHWDS();
	}

  @Override
	public boolean access_request(int session_id, int permission_id) {
    long value = hwds.search(session_id);
    if ( value == permission_id ) {
      return true;
    }
    return false;
	}

	@Override
	public int initiate_session_request(String user_id, String[] roles) {
		Session session = new Session(user_id);
		SDP_Response response = (SDP_Response)pdp.request(session, roles);
    if ( response != null ) {
      ArrayList<Integer> v = response.getValues();
      Iterator iterator = v.iterator();
      while (iterator.hasNext()) {
        Integer Value = (Integer) iterator.next();
        hwds.enqueue(session.id, Value.longValue());
      }
		  return session.id;
    }
    return -1; // not authorized
	}

	@Override
	public void destroy_session(int session_id) {
    // TODO: extract_all(key)?
    long value = 0;
    while ( value != -1 )
      value = hwds.extract(session_id);
		this.pdp.delete(session_id);	
	}

}
