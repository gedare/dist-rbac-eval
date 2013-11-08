package BloomFilter;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class CountingBloomFilter {
	public byte[] cbf;
	public int k;
	public int m;

	public byte[] getCbf() {
		return cbf;
	}

	public void setCbf(int m) {
		this.m = m;
		cbf = new byte[m];
		for(int i = 0; i < cbf.length; i++) {
			cbf[i] = 0;
		}
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}
	
	public void InsertElement(String v) throws NoSuchAlgorithmException, UnsupportedEncodingException {
	      BigInteger bfSize=new BigInteger(Long.toString(m));
	      BigInteger bi;
	      for(int i=0;i<k;i++){
	         /*
	          * generate a position in the Bloom filter
	          * position < m
	          */
	         bi=null;
	         try{
	            bi=SHA1(i+v);
	         }catch(Exception e){
	            System.out.println("SHA-1 Exception");
	            System.exit(0);
	         }
	         if(bi!=null){
	            bi=bi.mod(bfSize);
	            Integer position=new Integer(bi.intValue());
	            int pos=position.intValue();
	            cbf[pos]=(byte)(cbf[pos]+1);
	         }
	      }
	}
	
	public void RemoveElement(String v) throws NoSuchAlgorithmException, UnsupportedEncodingException {
	      BigInteger bfSize=new BigInteger(Long.toString(m));
	      BigInteger bi;
	      for(int i=0;i<k;i++){
	         /*
	          * generate a position in the Bloom filter
	          * position < m
	          */
	         bi=null;
	         try{
	            bi=SHA1(i+v);
	         }catch(Exception e){
	            System.out.println("SHA-1 Exception");
	            System.exit(0);
	         }
	         if(bi!=null){
	            bi=bi.mod(bfSize);
	            Integer position=new Integer(bi.intValue());
	            int pos=position.intValue();
	            cbf[pos]=(byte)(cbf[pos]-1);
	         }
	      }
	}
	public boolean containsElement(String v) throws NoSuchAlgorithmException, UnsupportedEncodingException {
	      BigInteger bi;
	      int i;

	      //System.out.println("test presence of element="+element);
	      BigInteger bfSize=new BigInteger(Long.toString(m));
	      for(i=0;i<k;i++){
	         /*
	          * generate a position in the Bloom filter
	          * position < m
	          */
	         bi=null;
	         try{
	            bi=SHA1(i+v);
	         }catch(Exception e){
	            System.out.println("SHA-1 Exception");
	            System.exit(0);
	         }
	         if(bi!=null){
	            bi=bi.mod(bfSize);
	            Integer position=new Integer(bi.intValue());
	            int pos=position.intValue();
	            //System.out.println("position="+position);
	            if(cbf[pos]==0){
	               return false;
	            }
	         }
	      }
	      return true;
	}
	   public BigInteger SHA1(String v) throws NoSuchAlgorithmException,UnsupportedEncodingException{
		      MessageDigest md;
		      md=MessageDigest.getInstance("SHA-1");
		      byte[] hash=new byte[40];
		      md.update(v.getBytes("iso-8859-1"),0,v.length());
		      hash=md.digest();
		      return new BigInteger(hash);
		    }

		   public byte getMaxCounter(){
		      int i;

		      byte maxCounter=0;
		      for(i=0;i<m;i++){
		         if(cbf[i]>maxCounter){
		            maxCounter=cbf[i];
		         }
		      }
		      return maxCounter;
		   }

}
