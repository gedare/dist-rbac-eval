package BloomFilter;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import PDP_SDP.PDP;
import PDP_SDP.PDP_Response;
import PDP_SDP.SDP_Data_Structure;
import PDP_SDP.Session;
import RbacGraph.RbacGraph;
import Structures.Vertex;
import Structures.RoleVertex;


public class PDP_Bloom extends PDP{
	public static int m;
	public static int Elen;
	public static int MAX_DEPTH;
	
	public static ArrayList<CountingBloomFilter> filter;
	public static Set A;
	public static Set U_A;
	public static ArrayList<Integer> sessions;
	public static Set[] AA;
	public static Set explicitlist;
	public static int cbftype;	//0=positive. 1=negative
	
	public PDP_Bloom(){}
	
	public PDP_Bloom(int m, int Elen, int MAX_DEPTH) {
		this.m = m;
		this.Elen = Elen;
		this.MAX_DEPTH = MAX_DEPTH;
		this.filter = new ArrayList();
		this.A = new HashSet();
		this.U_A = new HashSet();
		this.sessions = new ArrayList();
		this.explicitlist = new HashSet();
	}

	@Override
	public SDP_Data_Structure request(Session s, String[] roles) {

    PDP_Response P = (PDP_Response)super.request(s, roles);
    if ( P == null )
      return null;
    ArrayList<RoleVertex> Roles = P.getRoles();

		int[] allpermissions = ((RbacGraph)this.getG()).get_permissions_IDs();
		int sessionid = s.id;
		Set Up = new HashSet();
		sessions.add(sessionid);
		ArrayList U = new ArrayList();
		U.add(allpermissions);
		U.add(sessions);
		for(int i = 0; i < allpermissions.length; i++) {
			Up.add(sessionid + " " + allpermissions[i]);
		}
		Set I = new HashSet();
    Iterator iterator = Roles.iterator();
    while (iterator.hasNext()) {
      Vertex currentvertex = (Vertex) iterator.next();
      ArrayList<Vertex> roleSubgraph =
        ((RbacGraph)g).getInducedGraph(new ArrayList<Vertex>(), currentvertex);
      RbacGraph helpergraph = new RbacGraph(roleSubgraph);
      int[] permissions = helpergraph.get_permissions_IDs();
      for(int j = 0; j < permissions.length; j++) {
        I.add(sessionid + " " + permissions[j]);
      }
    }
		Set Up_I = new HashSet();
		Up_I.addAll(Up);
		Up_I.removeAll(I);
		if(A.size() == 0 && U_A.size() == 0) {
			if(I.size() == 0) {
				U_A.addAll(Up_I);
			}
			else if(Up_I.size() == 0) {
				A.addAll(I);
			}
			else if(I.size() <= Up_I.size()) {
				
				A.addAll(I);
			}
			else {
				U_A.addAll(Up_I);
			}
		}
		else if(A.size() > 0) {
			if(I.size() > 0) {
				A.addAll(I);
			}
			if(A.size() > ((int[])U.get(0)).length*((ArrayList)U.get(1)).size() - A.size() && ((int[])U.get(0)).length*((ArrayList)U.get(1)).size() != A.size()) {
				for(int i = 0; i < sessions.size(); i++) {
					for(int j = 0; j < allpermissions.length; j++) {
						U_A.add(sessions.get(i) + " " + allpermissions[j]);
					}
				}
				U_A.removeAll(A);
				A.clear();
			}
		}
		else if(U_A.size() > 0) {
			if(Up_I.size() > 0) {
				U_A.addAll(Up_I);
			}
			if(U_A.size() >= ((int[])U.get(0)).length*((ArrayList)U.get(1)).size() - U_A.size() && ((int[])U.get(0)).length*((ArrayList)U.get(1)).size() != U_A.size()) {
				for(int i = 0; i < sessions.size(); i++) {
					for(int j = 0; j < allpermissions.length; j++) {
						A.add(sessions.get(i) + " " + allpermissions[j]);
					}
				}
				
				A.removeAll(U_A);
				U_A.clear();
			}
		}
		if(filter.size() == 0 || explicitlist.size() > Elen) {
			CascadeBloomFilter cbf = new CascadeBloomFilter();
			cbf.setMAX_DEPTH(MAX_DEPTH);
			try {
				if(A.size() != 0) {
					cbf.ConstructCascadeBF(A, U, m, Elen);
					cbftype = 0;
				}
				else {
					cbf.ConstructCascadeBF(U_A, U, m, Elen);
					cbftype = 1;
				}
				AA = cbf.getAA();
				filter = cbf.getFilter();
				explicitlist = cbf.getExplicitlist();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		else if((A.size() != 0) && cbftype == 1) {
			CascadeBloomFilter cbf = new CascadeBloomFilter();
			cbf.setMAX_DEPTH(MAX_DEPTH);
			try {
				cbf.ConstructCascadeBF(A, U, m, Elen);
				AA = cbf.getAA();
				filter = cbf.getFilter();
				explicitlist = cbf.getExplicitlist();
				cbftype = 0;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		else if((A.size() == 0) && cbftype == 0) {
			CascadeBloomFilter cbf = new CascadeBloomFilter();
			cbf.setMAX_DEPTH(MAX_DEPTH);
			try {
				cbf.ConstructCascadeBF(U_A, U, m, Elen);
				AA = cbf.getAA();
				filter = cbf.getFilter();
				explicitlist = cbf.getExplicitlist();
				cbftype = 1;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		else {
			CascadeBloomFilter cbf = new CascadeBloomFilter();
			cbf.setMAX_DEPTH(MAX_DEPTH);
			try {
				cbf.setAA(AA);
				cbf.setFilter(filter);
				cbf.setExplicitlist(explicitlist);
				if(cbftype == 0) {
					cbf.InsertIntoCascadeBF(1, (HashSet)I, U);
				}
				else {
					cbf.InsertIntoCascadeBF(1, (HashSet)Up_I, U);
				}
				AA = cbf.getAA();
				filter = cbf.getFilter();
				explicitlist = cbf.getExplicitlist();
			/*	if(explicitlist.size() > Elen) {
					if(A.size() != 0) {
						cbf.ConstructCascadeBF(A, U, m, Elen);
						cbftype = 0;
					}
					else {
						cbf.ConstructCascadeBF(U_A, U, m, Elen);
						cbftype = 1;
					}
				}
				AA = cbf.getAA();
				filter = cbf.getFilter();
				explicitlist = cbf.getExplicitlist();	*/			
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		CascadeBloomFilter cbf = new CascadeBloomFilter();
		cbf.setMAX_DEPTH(MAX_DEPTH);
		try {
			cbf.setFilter(filter);
			cbf.setExplicitlist(explicitlist);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		byte count = 0;
	/*	for(int i = 0; i < filter.size(); i++) {
			for(int j = 0; j < filter.get(i).getCbf().length; j++) {
				byte[] b = filter.get(i).getCbf();
				count = (byte) (count + b[j]);
				System.out.print(b[j] + " ");
			}
			System.out.println();
		}
		System.out.println("filter size: " + count + " explicitset size: " + explicitlist.size());*/

		SDP_Data_Structure bfd = new BFDecision();
		((BFDecision)bfd).setCbf(cbf);
		((BFDecision)bfd).setCbftype(cbftype);
		return bfd;
	}

	@Override
	public SDP_Data_Structure delete(int session_id) {
		int[] allpermissions = ((RbacGraph)this.getG()).get_permissions_IDs();
		Set Up = new HashSet();
		for(int i = 0; i < sessions.size(); i++) {
			if(sessions.get(i) == session_id) {
				sessions.remove(i);
				break;
			}
		}
		ArrayList U = new ArrayList();
		U.add(allpermissions);
		U.add(sessions);
		for(int i = 0; i < allpermissions.length; i++) {
//			U.remove(session_id + " " + allpermissions[i]);
			Up.add(session_id + " " + allpermissions[i]);
		}
		if(A.size() != 0) {
			for(int j = 0; j < allpermissions.length; j++) {
				if(A.contains(session_id + " " + allpermissions[j])) {
					A.remove(session_id + " " + allpermissions[j]);
				}
			}
		}
		else {
			for(int j = 0; j < allpermissions.length; j++) {
				if(U_A.contains(session_id + " " + allpermissions[j])) {
					U_A.remove(session_id + " " + allpermissions[j]);
				}
			}
		}
		if(A.size() != 0 && A.size() >  ((int[])U.get(0)).length*((ArrayList)U.get(1)).size() - A.size()) {
			for(int i = 0; i < sessions.size(); i++) {
				for(int j = 0; j < allpermissions.length; j++) {
					U_A.add(sessions.get(i) + " " + allpermissions[j]);
				}
			}
			U_A.removeAll(A);
			A.clear();
		}
		else if(U_A.size() != 0 && U_A.size() >=  ((int[])U.get(0)).length*((ArrayList)U.get(1)).size() - U_A.size()) {
			for(int i = 0; i < sessions.size(); i++) {
				for(int j = 0; j < allpermissions.length; j++) {
					A.add(sessions.get(i) + " " + allpermissions[j]);
				}
			}
			A.removeAll(U_A);
			U_A.clear();
		}
		if((A.size() != 0) && cbftype == 1) {
			CascadeBloomFilter cbf = new CascadeBloomFilter();
			cbf.setMAX_DEPTH(MAX_DEPTH);
			try {
				cbf.ConstructCascadeBF(A, U, m, Elen);
				AA = cbf.getAA();
				filter = cbf.getFilter();
				explicitlist = cbf.getExplicitlist();
				cbftype = 0;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		else if((U_A.size() != 0) && cbftype == 0) {
			CascadeBloomFilter cbf = new CascadeBloomFilter();
			cbf.setMAX_DEPTH(MAX_DEPTH);
			try {
				cbf.ConstructCascadeBF(U_A, U, m, Elen);
				AA = cbf.getAA();
				filter = cbf.getFilter();
				explicitlist = cbf.getExplicitlist();
				cbftype = 1;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		else {
			try {
				CascadeBloomFilter cbf = new CascadeBloomFilter();
				cbf.setMAX_DEPTH(MAX_DEPTH);
				cbf.setAA(AA);
				cbf.setFilter(filter);
				cbf.setExplicitlist(explicitlist);
				if(cbftype == 0) {
					cbf.RemoveFromCascadeBF((HashSet)Up);
				}
				else {
					cbf.RemoveFromCascadeBF((HashSet)Up);
				}
				AA = cbf.getAA();
				filter = cbf.getFilter();
				explicitlist = cbf.getExplicitlist();	
				
			/*	if(explicitlist.size() > Elen) {
					if(A.size() != 0) {
						cbf.ConstructCascadeBF(A, U, m, Elen);
						cbftype = 0;
					}
					else {
						cbf.ConstructCascadeBF(U_A, U, m, Elen);
						cbftype = 1;
					}
				}
				AA = cbf.getAA();
				filter = cbf.getFilter();
				explicitlist = cbf.getExplicitlist();*/
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		CascadeBloomFilter cbf = new CascadeBloomFilter();
		cbf.setMAX_DEPTH(MAX_DEPTH);
		try {
			cbf.setFilter(filter);
			cbf.setExplicitlist(explicitlist);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		SDP_Data_Structure bfd = new BFDecision();
		((BFDecision)bfd).setCbf(cbf);
		((BFDecision)bfd).setCbftype(cbftype);
		return bfd;
	}

}
