package PDP_SDP;

import java.util.ArrayList;

import Structures.Vertex;
import Structures.RoleVertex;

public class PDP_Response implements SDP_Data_Structure {
  private ArrayList<RoleVertex> Roles;
  private ArrayList<Vertex> UserGraph;
  private ArrayList Permissions;

  public ArrayList<RoleVertex> getRoles() {
    return Roles;
  }
  public ArrayList<Vertex> getUserGraph() {
    return UserGraph;
  }
  public ArrayList getPermissions() {
    return Permissions;
  }

  public void setRoles(ArrayList<RoleVertex> R) {
    Roles = R;
  }
  public void setUserGraph(ArrayList<Vertex> U) {
    UserGraph = U;
  }
  public void setPermissions(ArrayList P) {
    Permissions = P;
  }


  PDP_Response(ArrayList<RoleVertex> R, ArrayList<Vertex> U, ArrayList P) {
    this.Roles = R;
    this.UserGraph = U;
    this.Permissions = P;
  }
}
