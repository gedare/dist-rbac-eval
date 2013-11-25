package HWDSBitSet;

import PDP_SDP.PDP;
import PDP_SDP.SDP;
import PDP_SDP.Session;
import java.util.*;

import jni.gem5Op;
import hwds.*;

public class SDP_HWDSBitSet extends SDP {

  // A subset of the PDP's Sessions map.
  private HWDSBitSet hwds;
  private HashMap<Integer,CountedBitSet> hashed_bitsets;

	public SDP_HWDSBitSet(PDP pdp) {
		super(pdp);
    hwds = new SimulatedHWDSBitSet();
    hashed_bitsets = new HashMap<Integer,CountedBitSet>();
	}

  @Override
	public boolean access_request(int session_id, int permission_id) {
    int tag = (int)hwds.search(session_id);
    CountedBitSet cb = hashed_bitsets.get(tag);
    if ( cb == null )
      return false;
    return cb.getBits().get(permission_id);
	}

	@Override
	public int initiate_session_request(String user_id, String[] roles) {
		Session session = new Session(user_id);
		SDP_Response response = (SDP_Response)pdp.request(session, roles);
    if ( response != null ) {
      BitSet b = response.getBitSet();
      if ( b != null ) {
        int tag = b.hashCode();
        CountedBitSet cb = hashed_bitsets.get(tag);
        if ( cb != null) {
          cb.obtain();
        } else {
          cb = new CountedBitSet(b);
          hashed_bitsets.put(tag, cb);
        }
        hwds.enqueue(session.id, tag);
		    return session.id;
      }
    }
    return -1; // not authorized
	}

	@Override
	public void destroy_session(int session_id) {
    int tag = (int)hwds.extract(session_id);
    CountedBitSet cb = hashed_bitsets.get(tag);
    if (cb != null) {
      cb.release();
      if (cb.getCount() == 0) {
        hashed_bitsets.remove(tag);
      }
    }
		this.pdp.delete(session_id);	
	}

}
