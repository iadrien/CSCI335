package a7;

import java.util.Iterator;
import java.util.Vector;


public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Vector<Integer> whoGo = new Vector<Integer>(4);
		
		for (int k = 0; k < 4; k++) {
			whoGo.add(k);
		}
		
		Iterator<Integer> iWho = whoGo.iterator();
		
		int whoNext = iWho.next();
		
		System.out.print(whoNext);
	}

}
