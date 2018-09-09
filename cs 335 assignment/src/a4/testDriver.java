package a4;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

import a4Helper.Helper;
public class testDriver {

	
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub

		int numOfValues = 5;
		Helper.isTesting();
		
		ArrayList<ParaPrefix> sumThread = new ArrayList<ParaPrefix>();

		// create a cyclic barrier
		final CyclicBarrier barrier1 = new CyclicBarrier(numOfValues);

		// create multiple threads each holds a value
		// set the index of the thread & add the thread to the array list
		for (int i = 0; i < numOfValues; i++) {

			ParaPrefix ranThreads = new ParaPrefix();

			ranThreads.setIndex(i);
			sumThread.add(ranThreads);
		}

		// initialize the static field (array, barrier) in thread class
		sumThread.get(0).setArray(numOfValues);
		sumThread.get(0).setBarrier(barrier1);

		// start the threads in random order
		for (int i = 0; i < numOfValues; i++) {
			sumThread.get(i).start();
		}
			
	}

}
