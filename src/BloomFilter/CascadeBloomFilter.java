package BloomFilter;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class CascadeBloomFilter {
	public int MAX_DEPTH;
	ArrayList<CountingBloomFilter> filter;
	Set[] AA;
	Set explicitlist;
	int depth;
	
	
	public int getMAX_DEPTH() {
		return MAX_DEPTH;
	}
	public void setMAX_DEPTH(int max_depth) {
		MAX_DEPTH = max_depth;
	}
	public void setFilter(ArrayList<CountingBloomFilter> filter) {
		this.filter = filter;
	}
	public ArrayList<CountingBloomFilter> getFilter() {
		return filter;
	}
	
	
	public Set[] getAA() {
		return AA;
	}
	public void setAA(Set[] aa) {
		AA = aa;
	}
	
	public void setExplicitlist(Set explicitlist) {
		this.explicitlist = explicitlist;
	}
	public Set getExplicitlist() {
		return explicitlist;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public void ConstructCascadeBF(Set A, ArrayList U, int m, int Elen) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		//	Set newA = A;

			Set newU_A = new HashSet();
			boolean construct = false;
			for(depth = MAX_DEPTH; depth >= 1; depth--) {
				filter = new ArrayList();
				explicitlist = new HashSet();
				int Usize = ((int[])U.get(0)).length*((ArrayList)U.get(1)).size();
				construct = tryToConstruct(1, depth, A.size(), Usize, m, Elen);
				if(construct) {
					AA = new Set[depth+1];
					for(int i = 0; i <= depth; i++) {
						AA[i] = new HashSet();
					}
					int[] allpermissions = (int[]) U.get(0);
					ArrayList sessions = (ArrayList) U.get(1);
					for(int i = 0; i < sessions.size(); i++) {
						for(int j = 0; j < allpermissions.length; j++) {
							AA[0].add(sessions.get(i) + " " + allpermissions[j]);
						}
					}
					AA[1].addAll(A);
					Collections.reverse(filter);
					for(int level = 1; level <= depth; level++) {
						Iterator it = AA[level].iterator();
						while (it.hasNext()) {
						    // Get element
							String e = (String)it.next();
							filter.get(level-1).InsertElement(e);
						}
						newU_A = new HashSet();
						newU_A.addAll(AA[level-1]);
						newU_A.removeAll(AA[level]);
						it = newU_A.iterator();
						while (it.hasNext()) {
						    // Get element
							String e = (String)it.next();
							if(filter.get(level-1).containsElement(e)) {
								if(level == depth) {
									explicitlist.add(e);
								}
								else {
									filter.get(level).InsertElement(e);
									AA[level+1].add(e);
								}
							}
						}
					}
					break;
				}
			}
		}
		public boolean tryToConstruct(int level, int depth, int Asize, int Usize, int m, int Elen) {
			if(m <= 0) {
				return false;
			}
			int trials = m/Asize - 1;
			for(int i = trials-1; i >= 0; i--) {
				int mi = (m/Asize - i)*Asize;
				int ki = (int)(Math.round(Math.log((double)2) *
						((double)mi)/((double)Asize)));
				float ei = (float)(Math.pow((double)((double)1 - Math.pow(Math.E,
						(double)(-1) * (double)(ki) * ((double)Asize)/((double)(mi)))),
						(double)(ki)));
				
				int newAsize = Math.round((ei * (float)(Usize - Asize)) +
						(float)(0.5) - Float.MIN_VALUE);
				if(level == depth) {
					if(newAsize < Elen) {
						CountingBloomFilter cbf = new CountingBloomFilter();
						cbf.setCbf(mi);
						cbf.setK(ki);
						filter.add(cbf);
						return true;
					}
					else {
						continue;
					}
				}
				if(tryToConstruct(level+1, depth, newAsize, newAsize + Asize, m-mi, Elen)){
						CountingBloomFilter cbf = new CountingBloomFilter();
						cbf.setCbf(mi);
						cbf.setK(ki);
						filter.add(cbf);
						return true;
					}
				}
			System.out.println("failed");
			System.exit(1);
			return false;
		}
		public boolean InsertIntoCascadeBF(int level, HashSet I, ArrayList Up) throws NoSuchAlgorithmException, UnsupportedEncodingException {
			depth = filter.size();
			if(level%2 == 0 || level > depth+1) {
				return false;
			}
			if(level == depth+1) {
				Iterator it = I.iterator();
				while (it.hasNext()) {
				    // Get element
					String e = (String)it.next();
					if(!explicitlist.contains(e)) {
						explicitlist.add(e);
					}
				}
				return true;
			}
			Iterator it = I.iterator();
			while (it.hasNext()) {
			    // Get element
				String e = (String)it.next();
				if(!AA[level].contains(e)) {
					AA[level].add(e);
					filter.get(level-1).InsertElement(e);
				}
			}
			if(level == depth) {
				it = I.iterator();
				while (it.hasNext()) {
				    // Get element
					String e = (String)it.next();
					if(explicitlist.contains(e)) {
						explicitlist.remove(e);
					}
				}
				Set set = new HashSet();
				set.addAll(AA[level]);
				set.addAll(explicitlist);
				Set F = new HashSet();
				if(Up.size() == 2) {
					int[] allpermissions = (int[])Up.get(0);
					ArrayList sessions = (ArrayList)Up.get(1);
					for(int i = 0; i < sessions.size(); i++) {
						for(int j = 0; j < allpermissions.length; j++) {
							F.add(sessions.get(i) + " " + allpermissions[j]);
						}
					}
				}
				else {
					F.addAll((HashSet)Up.get(0));
				}
				F.removeAll(set);
				it = F.iterator();
				while (it.hasNext()) {
				    // Get element
					String e = (String)it.next();
					if(filter.get(level-1).containsElement(e)) {
						explicitlist.add(e);
					}
				}
				return true;
			}
			it = I.iterator();
			while (it.hasNext()) {
			    // Get element
				String e = (String)it.next();
				if(AA[level+1].contains(e)) {
					AA[level+1].remove(e);
					filter.get(level).RemoveElement(e);
				}
			}
			Set set = new HashSet();
			set.addAll(AA[level]);
			set.addAll(AA[level+1]);
			Set F = new HashSet();
			if(Up.size() == 2) {
				int[] allpermissions = (int[])Up.get(0);
				ArrayList sessions = (ArrayList)Up.get(1);
				for(int i = 0; i < sessions.size(); i++) {
					for(int j = 0; j < allpermissions.length; j++) {
						F.add(sessions.get(i) + " " + allpermissions[j]);
					}
				}
			}
			else {
				F.addAll((HashSet)(Up.get(0)));
			}
			F.removeAll(set);
			it = F.iterator();
			while (it.hasNext()) {
			    // Get element
				String e = (String)it.next();
				if(filter.get(level-1).containsElement(e)) {
					if(!AA[level+1].contains(e)) {
						AA[level+1].add(e);
						filter.get(level).InsertElement(e);
					}
				}
			}
			HashSet Ip = new HashSet();
			it = AA[level].iterator();
			while (it.hasNext()) {
			    // Get element
				String e = (String)it.next();
				if(filter.get(level).containsElement(e)) {
					Ip.add(e);
				}
			}
			HashSet Upp = new HashSet();
			Upp.addAll(Ip);
			Upp.addAll(AA[level+1]);
			if(Ip.size() != 0 || Upp.size() != 0) {
				ArrayList Uppp = new ArrayList();
				Uppp.add(Upp);
				InsertIntoCascadeBF(level+2,Ip,Uppp);
			}
			return false;
		}
		public boolean RemoveFromCascadeBF(HashSet Up) throws NoSuchAlgorithmException, UnsupportedEncodingException {
			depth = filter.size();
			int d;
			if((depth+1)%2 == 0) {
				d = depth-1;
				Iterator it = Up.iterator();
				while(it.hasNext()) {
					String e = (String)it.next();
					if(explicitlist.contains(e)) {
						explicitlist.remove(e);
					}
				}
				for(int i = 1; i < AA.length; i++) {
					Set set = new HashSet();
					set.addAll(AA[i]);
					it = Up.iterator();
					while(it.hasNext()) {
						String e = (String)it.next();
						if(AA[i].contains(e)) {
							AA[i].remove(e);
							filter.get(i-1).RemoveElement(e);
						}
					}
				}
			}
			else {
				d = depth;
				Iterator it = Up.iterator();
				while(it.hasNext()) {
					String e = (String)it.next();
					if(explicitlist.contains(e)) {
						explicitlist.remove(e);
					}
				}
				for(int i = 1; i < AA.length; i++) {
					Set set = new HashSet();
					set.addAll(AA[i]);
					it = Up.iterator();
					while(it.hasNext()) {
						String e = (String)it.next();
						if(AA[i].contains(e)) {
							AA[i].remove(e);
							filter.get(i-1).RemoveElement(e);
						}
					}
				}
			}
			for(int level = 1; level <= d; level = level+2 ) {
				if(level%2 == 0 || level > depth+1) {
					return false;
				}
				Set set = new HashSet();
				set.addAll(AA[level+1]);
				Iterator it = set.iterator();
				while(it.hasNext()) {
					String e = (String)it.next();
					if(!filter.get(level-1).containsElement(e)) {
						AA[level+1].remove(e);
						filter.get(level).RemoveElement(e);						
					}
				}
			}
			return true;
		}
		public boolean MemberCascadeBF(String e) throws NoSuchAlgorithmException, UnsupportedEncodingException {
			depth = filter.size();
			for(int level = 1; level <= depth; level++) {
				if(!filter.get(level-1).containsElement(e)) {
					if(level%2 == 0) {
						return true;
					}
					else {
						return false;
					}
				}
			}
			if(depth%2 == 0) {
				if(explicitlist.contains(e)) {
					return true;
				}
				else {
					return false;
				}
			}
			else {
				if(explicitlist.contains(e)) {
					return false;
				}
				else {
					return true;
				}
			}
		}
}
