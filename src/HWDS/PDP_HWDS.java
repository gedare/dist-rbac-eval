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
	public SDP_Data_Structure request(Session s, String[] roles) {

    PDP_Response P = (PDP_Response)super.request(s, roles);
    if ( P == null )
      return null;
    ArrayList<RoleVertex> Roles = P.getRoles();
    ArrayList<Vertex> userSubgraph = P.getUserGraph();

    ArrayList<Integer> current_permissions = new ArrayList<Integer>();

    Iterator iterator = Roles.iterator();
    while (iterator.hasNext()) {
      Vertex currentvertex = (Vertex) iterator.next();
      ArrayList<Vertex> roleSubgraph =
        ((RbacGraph)g).getInducedGraph(new ArrayList<Vertex>(), currentvertex);

      // FIXME: the following could be computed directly on roleSubgraph,
      // however this follows how other PDPs compute permissions during
      // session creation.
      RbacGraph helpergraph = new RbacGraph(roleSubgraph);
      int[] permissions = helpergraph.get_permissions_IDs();
      // Add all permissions to the current session's set
      for ( int i = 0; i < permissions.length; i++ ) {
        if(!current_permissions.contains(permissions[i]))
          current_permissions.add(permissions[i]);
      }
    }
    Sessions.put(s.id, current_permissions);
		return new SDP_Response(current_permissions);
	}

	@Override
	public SDP_Data_Structure delete(int session_id) {
    Sessions.remove(session_id);
    return null; // What's the point of a return value?
  }

}
