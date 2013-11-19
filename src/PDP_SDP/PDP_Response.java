package PDP_SDP;

import java.util.ArrayList;

import Structures.Vertex;
import Structures.RoleVertex;

public class PDP_Response implements SDP_Data_Structure {
  private ArrayList<RoleVertex> Roles;
  private ArrayList<Vertex> UserGraph;

  public ArrayList<RoleVertex> getRoles() {
    return Roles;
  }
  public ArrayList<Vertex> getUserGraph() {
    return UserGraph;
  }

  public void setRoles(ArrayList<RoleVertex> R) {
    Roles = R;
  }

  public void setUserGraph(ArrayList<Vertex> U) {
    UserGraph = U;
  }

  PDP_Response(ArrayList<RoleVertex> R, ArrayList<Vertex> U) {
    this.Roles = R;
    this.UserGraph = U;
  }
}
