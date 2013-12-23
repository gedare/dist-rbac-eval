package HWDS;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import PDP_SDP.PDP;
import PDP_SDP.PDP_Response;
import PDP_SDP.SDP_Data_Structure;
import PDP_SDP.Session;
import RbacGraph.RbacGraph;
import Structures.*;

public class PDP_HWDS extends PDP {

  // The Sessions is a map of session ids as keys and permission bits as values
  // TODO: Use a sparse representation when permissions are sparse/dense.
  public Map<Integer, ArrayList<Integer>> Sessions;
	
	public PDP_HWDS() {
    super();
    this.Sessions = new TreeMap<Integer, ArrayList<Integer>>();
	}

	@Override
	public SDP_Data_Structure request(Session s, String[] roles,
      SDP_Data_Structure prepared) {

    PDP_Response P = (PDP_Response) prepared;
    if ( P == null )
      return null;
    ArrayList<RoleVertex> Roles = P.getRoles();
    ArrayList<Vertex> userSubgraph = P.getUserGraph();

    ArrayList<Integer> current_permissions = P.getPermissions();
      new ArrayList<Integer>();

    Sessions.put(s.id, current_permissions);
		return new SDP_Response(current_permissions);
	}

	@Override
	public SDP_Data_Structure delete(int session_id) {
    Sessions.remove(session_id);
    return null; // What's the point of a return value?
  }

}
