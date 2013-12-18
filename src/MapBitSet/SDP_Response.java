package MapBitSet;

import java.util.*;

import PDP_SDP.SDP_Data_Structure;

public class SDP_Response implements SDP_Data_Structure {
  private BitSet b;

  SDP_Response(BitSet b) {
    if ( b != null )
      this.b = (BitSet)b.clone();
    else
      b = null;
  }

  BitSet getBitSet() {
    return b;
  }
}
