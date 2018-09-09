package csci335;

/**
 * Runs this package. This implements the producer/consumer or bounded buffer
 * problem. This code contains both the solution from the OS and threads books.
 * 
 * @author Steven Huss-Lederman.
 * 
 */
public class Driver {
	/**
	 * Run samples of the bounded buffer problem.
	 * 
	 * @param args
	 *            Not used.
	 */
	public static void main(String[] args) {
		// Size of the bounded buffer.
		int size = 3;
		// Decides what type of implementation to use for the bounded buffer.
		int type;
		// Use an array where the code must take care of synchronization and
		// notifications.
//		type = BounderBuffer.BB_ARRAY;
		// Use the Collections class.
		type = BounderBuffer.BB_COLLECTION;
		// Create the bounded buffer of the type and size desired.
		BounderBuffer<Integer> bb = new BounderBuffer<Integer>(type, size);

		/*
		 * This produces some number of producers and consumers. If you uncomment
		 * multiple sections in the loop then multiple producers/consumers are
		 * created each time through the loop. The arguments are set differently
		 * so that they create different values and do so different random
		 * times.
		 */
		Producer p;
		Consumer c;
		for (int i = 1; i < 4; i++) {
			p = new Producer(Integer.toString(i), i * 1000, 100, 2000, bb);
			c = new Consumer(Integer.toString(i + 200), 100, 2000, bb);
			p.start();
			c.start();

//			p = new Producer(Integer.toString(i + 300), i * 1000, 100, 1000, bb);
//			c = new Consumer(Integer.toString(i + 400), 500, 2000, bb);
//			p.start();
//			c.start();
//
//			p = new Producer(Integer.toString(i + 500), i * 1000, 500, 2000, bb);
//			c = new Consumer(Integer.toString(i + 600), 100, 1000, bb);
//			p.start();
//			c.start();
		}
	}
}