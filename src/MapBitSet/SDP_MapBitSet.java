package MapBitSet;

import PDP_SDP.PDP;
import PDP_SDP.SDP;
import PDP_SDP.Session;
import java.util.*;

public class SDP_MapBitSet extends SDP {

  // A subset of the PDP's Sessions map.
  public Map<Integer, BitSet> Sessions;

	public SDP_MapBitSet(PDP pdp) {
		super(pdp);
    Sessions = new TreeMap<Integer, BitSet>();
	}

  @Override
	public boolean access_request(int session_id, int permission_id) {
    BitSet b = Sessions.get(session_id);
    if ( b == null )
      return false;
    return b.get(permission_id);
	}

	@Override
	public int initiate_session_request(String user_id, String[] roles) {
		Session session = new Session(user_id);
		SDP_Response response = (SDP_Response)pdp.request(session, roles);
    if ( response != null ) {
      BitSet b = response.getBitSet();
      if ( b != null ) {
        Sessions.put(session.id, b);
		    return session.id;
      }
    }
    return -1; // not authorized
	}

	@Override
	public void destroy_session(int session_id) {
    Sessions.remove(session_id);
		this.pdp.delete(session_id);	
	}

}
