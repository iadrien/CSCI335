package inCalssPractice;

/**
 * Basic ThreadCreation class that extends Thread class to creation specified
 * thread
 * 
 * @author Zuoting Xie
 *
 */
public class ThreadCreation extends Thread {

	// declare private fields to store
	// the summation, the number start to sum
	// and the number of integers to sum
	private int sum;
	private int numStart;
	private int numInt;

	// constructor takes an string and
	// call the constructor in the super class
	public ThreadCreation(String name) {
		super(name);
	}

	// standard run method
	public void run() {

		// display thread starting message
		System.out.println(Thread.currentThread().getName()
				+ " is now starting!");

		// calculation from the number start and add numInt integers
		for (int i = 0; i < numInt; i++) {
			sum = sum + numStart;
			numStart = numStart + 1;
		}

		// display thread ending message
		System.out.println(Thread.currentThread().getName()
				+ " is now stopping!");
	}

	// method used to set start number value and the number of int to sum
	public void setValues(int numStart, int numInt) {
		this.numStart = numStart;
		this.numInt = numInt;
	}

	// method used to return the summation
	public int sum() {
		return sum;
	}
}
