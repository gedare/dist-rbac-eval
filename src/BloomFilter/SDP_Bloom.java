package BloomFilter;

import PDP_SDP.PDP;
import PDP_SDP.SDP;
import PDP_SDP.Session;


public class SDP_Bloom extends SDP{

	BFDecision bfd;
	public SDP_Bloom(PDP pdp) {
		super(pdp);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean access_request(int session_id, int permission_id) {
		boolean decision = false;
		try {
		if(bfd.getCbftype() == 0) {
			if(bfd.getCbf().MemberCascadeBF(session_id + " " + permission_id)) {
				decision = true;
			}
			else {
				decision = false;
			}
		}
		else {
			if(bfd.getCbf().MemberCascadeBF(session_id + " " + permission_id)) {
				decision = false;
			}
			else {
				decision = true;
			}
		}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return decision;
	}

	@Override
	public int initiate_session_request(String user_id, String[] roles) {
		Session session = new Session(user_id);
		bfd = (BFDecision) new PDP_Bloom().request(session, roles);
		return session.id;
	}

	@Override
	public void destroy_session(int session_id) {
		bfd = (BFDecision) this.pdp.delete(session_id);	
	}

}
