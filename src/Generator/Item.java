package Generator;

import java.util.ArrayList;

//item is pretty much like a session
//each item has a list of roles that it has requested during the initiation phase
//and it has item_id which can be interpreted as a session_id

public class Item {
	public ArrayList elems;//list of requested roles
	public int item_id;
	
	public Item(int item_id, ArrayList elems){
		setItem_id(item_id);
		setElems(elems);
	}
		
	public ArrayList getElems() {
		return elems;
	}

	public void setElems(ArrayList elems) {
		this.elems = elems;
	}

	public int getItem_id() {
		return item_id;
	}

	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}

	
}
