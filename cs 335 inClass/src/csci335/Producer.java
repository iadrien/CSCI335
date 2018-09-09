package csci335;

import java.util.Random;

/**
 * Implements the producer using the bounded buffer.
 * 
 * @author Steven Huss-Lederman
 * 
 */
public class Producer extends Thread {
	// To generate random numbers for sleep.
	Random ran;
	// Holds minimum and maximum sleep time.
	int minSleep, maxSleep;
	// Value to start producing.
	int startValue;
	// Bounded buffer to use.
	BounderBuffer<Integer> bqueue;

	/**
	 * Construct producer with needed information.
	 * 
	 * @param name
	 *            Name of this thread.
	 * @param beginValue
	 *            First value to generate.
	 * @param minTime
	 *            Minimum time to wait.
	 * @param maxTime
	 *            Maximum time to wait.
	 * @param queue
	 *            Bounded buffer to use (queue).
	 */
	public Producer(String name, int beginValue, int minTime, int maxTime,
			BounderBuffer<Integer> queue) {
		// Record name via thread constructor.
		super(name);
		// Initialize random number generator.
		ran = new Random();
		/* Record other values in fields. */
		startValue = beginValue;
		minSleep = minTime;
		maxSleep = maxTime;
		bqueue = queue;
	}

	/**
	 * Runs producer thread.
	 */
	public void run() {
		// Produce forever from start value and added 10 each time to allow
		// different producers to have different values.
		for (int i = startValue;; i += 10) {
			try {
				// Sleep between minimum and maximum time. Do before produce an
				// item so first producer or consumer can come first.
				sleep(minSleep + ran.nextInt(maxSleep - minSleep));
			} catch (InterruptedException e) {
				e.printStackTrace();
				// Allowed to continue since only changed the time that slept.
			}
			// Place new item in bounded buffer.
			bqueue.insert(i);
			System.out.println("Producer " + getName() + " just added item "
					+ i);
		}
	}
}
