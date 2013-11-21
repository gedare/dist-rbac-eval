package HWDS;
import java.util.BitSet;

public class CountedBitSet {
  private int refcnt;
  private BitSet bits;

  public CountedBitSet() {
    refcnt = 1;
    bits = new BitSet();
  }
  public CountedBitSet(int nbits) {
    refcnt = 1;
    bits = new BitSet(nbits);
  }
  public CountedBitSet(BitSet b) {
    refcnt = 1;
    bits = b;
  }

  public void obtain() {
    refcnt++;
  }
  public void release() {
    refcnt--;
  }
  public int getCount() {
    return refcnt;
  }
  public BitSet getBits() {
    return bits;
  }
}
