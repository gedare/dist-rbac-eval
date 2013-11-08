package Structures;
import java.util.ArrayList;
import java.util.List;


public abstract class Vertex {
	public static int staticID;
	public int ID;
	public ArrayList<Vertex> neighbours;

	public Vertex() {
	    neighbours = null;
	}

	public int getID() {
	    return ID;
	}

	public void AddNeighbour(Vertex v) {
	        /*
	        if(v.getStringID().equals("R1") ||
		   this.getStringID().equals("R1")) {
		    System.out.println("Vertex::Adding "+this.getStringID()+" --> "+v.getStringID());
		}
		/* */
		neighbours.add(v);
	}

	public void RemoveNeighbour(Vertex v) {
		neighbours.remove(v);
	}

	public List<Vertex> getNeighbours() {
	    return neighbours;
	}

	public int getNumNeighbours() {
	    return this.neighbours.size();
	}

	public Vertex getNeighbour(int i){
		return this.neighbours.get(i);
	}

	public abstract String getStringID();

	/*
	public boolean equals(Vertex v) {
	    return(this.getStringID().equals(v.getStringID()));
	}
	*/
}
