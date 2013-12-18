package HWDSBitSet;

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

public class PDP_HWDSBitSet extends PDP {

  // The Sessions is a map of session ids as keys and permission bits as values
  // TODO: Use a sparse representation when permissions are sparse/dense.
  public Map<Integer, BitSet> Sessions;
	
	public PDP_HWDSBitSet() {
    super();
    this.Sessions = new TreeMap<Integer, BitSet>();
	}

	@Override
	public SDP_Data_Structure request(Session s, String[] roles) {

    PDP_Response P = (PDP_Response)super.request(s, roles);
    if ( P == null )
      return null;
    ArrayList<RoleVertex> Roles = P.getRoles();
    ArrayList<Vertex> userSubgraph = P.getUserGraph();

    // Construct set of permissions for requested roles in this session
    /* FIXME:
     * 1) Should be able to know the maximum number of permissions globally
     * 2) The vertex IDs include roles and users so there are gaps. It would
     *    be better if the permission IDs are dense.
     */
    Iterator iterator;
    iterator = userSubgraph.iterator();
    int max_permission = 0;
    while (iterator.hasNext()) {
      Vertex v = (Vertex) iterator.next();
      if ( v instanceof PermissionVertex ) {
        int p = ((PermissionVertex)v).getID();
        if ( p > max_permission )
          max_permission = p;
      }
    }
    BitSet current_permissions = new BitSet(max_permission);
    iterator = Roles.iterator();
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
          current_permissions.set(permissions[i]);
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
