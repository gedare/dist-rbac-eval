package RbacGraph;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import PDP_SDP.PDP;
import PDP_SDP.SDP;
import PDP_SDP.SDP_Data_Structure;
import PDP_SDP.Session;
import Structures.RBAC;
import Structures.Vertex;
import Structures.UserVertex;
import Structures.PermissionVertex;


public class SDP_RbacGraph extends SDP {

	public SDP_RbacGraph(PDP pdp) {
		super(pdp);
	}

	public int initiate_session_request(String user_id, String[] roles) {
		//assume login is approved
		//create new empty session
		Session s = new Session(user_id);
		//sends user_id, ses_id, roles to PDP
		SDP_Data_Structure checker = this.pdp.request(s, roles);
		if(checker != null) {
			this.g = (RBAC) checker;//to make sure if we have denied session request it does not mess up our previous graph on SDP
			/* only for testing purposes 
			try {
				if(this.g !=null)((RBAC)this.g).generateOutput();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/* */
			return s.id;//just return session id so far so the access request could be made
		} else System.out.println("Unauthorised");
		//PDP finds permissions and stores everything in a table as a ses_id, permissions
		//PDP replies RBAC(matrix[ses_ids][permissions])
		return -1;//if not authorised return -1
		//else request was DENIED, since the list of permissions is empty, null

	}
	
	public boolean access_request(int session_id, int permission_id) {
		// return (((RbacGraph)this.g).IsPair(session_id, permission_id));
	    
/* */
	    /*
	    try {
		((RBAC)this.g).generateOutput();
	    }
	    catch (Exception e) {
	    }
	    /* */

	    UserVertex v = ((RbacGraph)this.g).FindUser(Integer.toString(session_id));
	    if((v == null) || (v.getNumNeighbours() == 0)) {
		/* */
		System.out.println("Ret 1, v = "+v);
		/* */
		return false;
	    }

	    /*
	    System.out.println(session_id+" has "+v.getNumNeighbours()+" neighbours.");
	    /* */

	    for(List nList1 = v.neighbours, nList2 = null;
		    nList1 != null; nList1 = nList2) {

		nList2 = new ArrayList<Vertex>();
		for(Iterator i = nList1.iterator(); i.hasNext(); ) {
		    Vertex w = (Vertex)(i.next());

		    /*
		    System.out.println(w.getStringID()+" has "+w.getNumNeighbours()+" neighbours.");
		    /* */
		    if((w instanceof PermissionVertex) &&
		       (w.getID() == permission_id)) {
			    return true;
		    }

		    if(w.getNumNeighbours() > 0) {
			nList2.addAll(0, w.getNeighbours());
		    }
		}

		if(nList2.isEmpty()) nList2 = null;
	    }

	    /* 
	    System.out.println("Ret 2");
	    /* */
	    return false;
/* */
	}

	@Override
	public void destroy_session(int session_id) {
		this.pdp.delete(session_id);		
	}


}
