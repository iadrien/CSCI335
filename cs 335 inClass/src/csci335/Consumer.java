package csci335;

import java.util.Random;

/**
 * Implements the consumer using the bounded buffer.
 * 
 * @author Steven Huss-Lederman
 * 
 */
public class Consumer extends Thread {
	// To generate random numbers for sleep.
	Random ran;
	// Holds minimum and maximum sleep time.
	int minSleep, maxSleep;
	// Bounded buffer to use.
	BounderBuffer<Integer> bqueue;

	/**
	 * Construct consumer with needed information.
	 * 
	 * @param name
	 *            Name of this thread.
	 * @param minTime
	 *            Minimum time to wait.
	 * @param maxTime
	 *            Maximum time to wait.
	 * @param queue
	 *            Bounded buffer to use (queue).
	 */
	public Consumer(String name, int minTime, int maxTime,
			BounderBuffer<Integer> queue) {
		// Record name via thread constructor.
		super(name);
		// Initialize random number generator.
		ran = new Random();
		/* Record other values in fields. */
		minSleep = minTime;
		maxSleep = maxTime;
		bqueue = queue;
	}

	/**
	 * Runs consumer thread.
	 */
	public void run() {
		// Item to be consumed.
		int item;
		// Do this forever.
		for (;;) {
			try {
				// Sleep between minimum and maximum time. Do before produce an
				// item so first producer or consumer can come first.
				sleep(minSleep + ran.nextInt(maxSleep - minSleep));
			} catch (InterruptedException e) {
				// Allowed to continue since only changed the time that slept.
				e.printStackTrace();
			}
			// Get item from bounded buffer.
			item = bqueue.retrieve();
			System.out.println("Consumer " + getName() + " just received item "
					+ item);
		}
	}
}