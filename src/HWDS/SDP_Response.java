package HWDS;

import java.util.*;

import PDP_SDP.SDP_Data_Structure;

public class SDP_Response implements SDP_Data_Structure {
  private ArrayList<Integer> Values;

  SDP_Response(ArrayList<Integer> V) {
    if ( V != null )
      Values = V;
    else
      Values = null;
  }

  ArrayList<Integer> getValues() {
    return Values;
  }
}
