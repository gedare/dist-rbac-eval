package Generator;

import java.util.ArrayList;
import java.util.Iterator;


public class SET {

	public ArrayList<String> elements;
	
	public SET(){
		elements = new ArrayList();
	}
	public SET(ArrayList elements){
		this.elements = elements;
	}
	public void add(String elem){
		elements.add(elem);
	}
	public boolean contains(String elem){
		return elements.contains(elem);
	}
	//takes an integer as an input parameter
	//converts it to binary number
	//depending on a bytes it puts a specific Set's element into a subset
	//0: do not put it
	//1: put it in a subset
	//subset is returned; 
	//example SET = {1, 2, 3, 4}; we call SubSet(5); 
	//5 = 0101; returned subset should be subset = {1, 3}
	public SET SubSet(int number){
		SET subset = new SET();
		String binary = Integer.toBinaryString(number);
		//goes through all the bytes
		int j = 0;
		for(int i = binary.length() - 1; i >= 0; i--){
			if (binary.charAt(i) != '0' ) {
				subset.add(this.elements.get(j));
				//System.out.println(elements.get(i));
			}
			j++;
		}

		return subset;
	}
	public void print(){
		Iterator it = elements.iterator();
		while(it.hasNext()){
			String s = (String) it.next();
			System.out.print(s + " ");
			}
		System.out.println();		

	}
}
