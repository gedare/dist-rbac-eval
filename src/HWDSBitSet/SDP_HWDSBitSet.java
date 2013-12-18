package HWDSBitSet;

import PDP_SDP.PDP;
import PDP_SDP.SDP;
import PDP_SDP.Session;
import java.util.*;

import jni.gem5Op;
import hwds.*;

public class SDP_HWDSBitSet extends SDP {

  // A subset of the PDP's Sessions map.
  private HWDS hwds;
  private int max_offset;
  private static final int value_bits = 64;

	public SDP_HWDSBitSet(PDP pdp) {
		super(pdp);
    hwds = new SimulatedHWDS();
    max_offset = 0;
	}

  @Override
	public boolean access_request(int session_id, int permission_id) {
    int offset = permission_id >> 6; // permission_id / 64
    long value = hwds.search( (session_id << 16) | offset );
    // FIXME: check for 'miss'
    int bit_position = permission_id & (value_bits - 1);
    return 0 != (value & (1 << bit_position));
	}

	@Override
	public int initiate_session_request(String user_id, String[] roles) {
		Session session = new Session(user_id);
		SDP_Response response = (SDP_Response)pdp.request(session, roles);
    if ( response != null ) {
      BitSet b = response.getBitSet();
      if ( b != null ) {
        long values[] = b.toLongArray();
        int offset = (b.length() + (value_bits-1)) / value_bits;
        if ( offset > max_offset )
          max_offset = offset;
        for ( int i = 0; i < offset; i++ ) {
          int key = (session.id << 16) | i;
          hwds.enqueue(key, values[i]);
        }
		    return session.id;
      }
    }
    return -1; // not authorized
	}

	@Override
	public void destroy_session(int session_id) {

    // slightly wasteful, but correct
    for ( int i = 0; i < max_offset; i++ )
      hwds.extract((session_id<<32) | i);

		this.pdp.delete(session_id);	
	}

}
