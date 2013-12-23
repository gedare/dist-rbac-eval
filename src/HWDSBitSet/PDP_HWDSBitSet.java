package HWDSBitSet;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import PDP_SDP.PDP;
import PDP_SDP.PDP_Response;
import PDP_SDP.SDP_Data_Structure;
import PDP_SDP.Session;
import RbacGraph.RbacGraph;
import Structures.*;

public class PDP_HWDSBitSet extends PDP {

  // The Sessions is a map of session ids as keys and permission bits as values
  // TODO: Use a sparse representation when permissions are sparse/dense.
  public Map<Integer, BitSet> Sessions;
	
	public PDP_HWDSBitSet() {
    super();
    this.Sessions = new TreeMap<Integer, BitSet>();
	}

	@Override
	public SDP_Data_Structure request(Session s, String[] roles,
      SDP_Data_Structure prepared) {

    PDP_Response P = (PDP_Response) prepared;
    if ( P == null )
      return null;
    ArrayList<RoleVertex> Roles = P.getRoles();
    ArrayList<Vertex> userSubgraph = P.getUserGraph();

    ArrayList permissions = P.getPermissions();

    // Construct set of permissions for requested roles in this session
    Iterator iterator;
    iterator = permissions.iterator();
    int max_permission = (Integer)permissions.get(permissions.size()-1);
    BitSet current_permissions = new BitSet(max_permission);

    Sessions.put(s.id, current_permissions);
		return new SDP_Response(current_permissions);
	}

	@Override
	public SDP_Data_Structure delete(int session_id) {
    Sessions.remove(session_id);
    return null; // What's the point of a return value?
  }

}
