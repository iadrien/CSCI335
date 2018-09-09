package a7;

import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

/**
 * Calculates the sum of values using threads.
 * 
 * @author Steven Huss-Lederman
 * 
 *         November 7, 2015
 * 
 *         There are no known bugs but the ones that are there on purpose to
 *         show ideas.
 */
public class A7 {
	// Which thread class to run based on available choices.
	final static int USE_NOTIFYALL = 1;
	final static int USE_NOTIFY_BROKEN = 3;
	// Use one of the next two lines to choose which version to run.
	final static int USE = USE_NOTIFY_BROKEN;
	// final static int USE = USE_NOTIFY_BROKEN;
	// Error code to return on exit if join fails.
	final static int JOIN_FAILED = 77;
	// Error code to return on bad USE choice.
	final static int USE_FAILED = 66;

	/**
	 * Run the sum process with threads. Prompts the user for needed values.
	 * 
	 * @param args
	 *            Not used.
	 */
	public static void main(String[] args) {
		// Used to store a line of input
		String line;
		// Prompt message
		String prompt;

		System.out.println("starting a7...");

		// Tell what going to run based on request by user - set at top of
		// class.
		switch (USE) {
		case USE_NOTIFYALL:
			System.out.println("\nDoing ParallelWorkNotifyAll");
			break;
		case USE_NOTIFY_BROKEN:
			System.out.println("\nDoing ParallelWorkNotifyBroken");
			break;
		default:
			System.err.println("Unknown USE set in code so cannot run.  Exit with code " + USE_FAILED);
			System.exit(USE_FAILED);
		}

		// Create a scanner for System.in.
		Scanner s = new Scanner(System.in);

		// Get minimum number of values/processors
		int minThreads = 0;
		prompt = "\nInput minimum number of values/threads to do (must be > 0) and hit return? ";
		while (minThreads < 1) {
			System.out.print(prompt);
			line = s.nextLine();
			minThreads = Integer.parseInt(line);
			prompt = "Value input " + minThreads + " was not greater than 0.  Please input again." + prompt;
		}

		// Get maximum number of values/processors
		/**
		 * maximum number should also check if the max>min
		 */
		
		int maxThreads = 0;
		prompt = "\nInput maximum number of values/threads to do (must be > 0) and hit return? ";
		while (maxThreads < 1) {
			System.out.print(prompt);
			line = s.nextLine();
			maxThreads = Integer.parseInt(line);
			prompt = "Value input " + maxThreads + " was not greater than 0.  Please input again." + prompt;
		}

		// Get first seed to use for the random permutation.
		System.out.print("\nInput a seed for the permutation and hit return? ");
		line = s.nextLine();
		int firstSeed = Integer.parseInt(line);

		// Get step in seed to use for the random permutation.
		System.out.print("\nInput step in seed for the permutation and hit return? ");
		line = s.nextLine();
		int stepSeed = Integer.parseInt(line);

		// Get number of tries for each thread size.
		System.out.print("\nInput number of tries for each values/threads size and hit return? ");
		line = s.nextLine();
		int tries = Integer.parseInt(line);

		s.close();

		// Records number of errors so can tell at end.
		int numErrors = 0;
		// Holds threads that will run.
		Vector<Thread> runningThreads = new Vector<Thread>(maxThreads);
		// Loop of all thread values to try
		for (int numThreads = minThreads; numThreads <= maxThreads; numThreads++) {
			System.out.println("Starting test of " + numThreads + " thread(s)");
			// Current seed.
			int curSeed = firstSeed;
			// Loop over tries for each thread
			for (int k = 0; k < tries; k++) {
				// Create threads for this number
				for (int m = 0; m < numThreads; m++) {
					// Run class requested by user - set at top of class.
					switch (USE) {
					case USE_NOTIFYALL:
						runningThreads.add(new ParallelWorkNotifyAll(m));
						break;
					case USE_NOTIFY_BROKEN:
						runningThreads.add(new ParallelWorkNotifyAllBroken(m));
						break;
					default:
						System.err.println("Unknown USE set in code so cannot run.  Exit with code " + USE_FAILED);
						System.exit(USE_FAILED);
					}
				}
				// Initialized ParallelOrdered based on request by user - set at
				// top of class.
				switch (USE) {
				case USE_NOTIFYALL:
					ParallelWorkNotifyAll.setup(numThreads, curSeed);
					break;
				case USE_NOTIFY_BROKEN:
					ParallelWorkNotifyAllBroken.setup(numThreads, curSeed);
					break;
				default:
					System.err.println("Unknown USE set in code so cannot run.  Exit with code " + USE_FAILED);
					System.exit(USE_FAILED);
				}
				// Scramble up order based on seed given.
				java.util.Collections.shuffle(runningThreads, new Random(curSeed));
				for (Thread pw : runningThreads) {
					pw.start();
				}

				// Have this thread wait for the others
				for (Thread pw : runningThreads) {
					try {
						pw.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
						System.err.println("Join failed so stopping code with error code " + JOIN_FAILED);
						System.exit(JOIN_FAILED);
					}
				}

				// Check result
				int sumResult = -99;
				// Initialized ParallelOrdered based on request by user - set at
				// top of class.
				switch (USE) {
				case USE_NOTIFYALL:
					sumResult = ParallelWorkNotifyAll.returnSum();
					break;
				case USE_NOTIFY_BROKEN:
					sumResult = ParallelWorkNotifyAllBroken.returnSum();
					break;
				default:
					System.err.println("Unknown USE set in code so cannot run.  Exit with code " + USE_FAILED);
					System.exit(USE_FAILED);
				}
				int expect = (numThreads * (numThreads + 1)) / 2;
				if (sumResult != expect) {
					System.out.println("*********The result should have been " + expect + " but got " + sumResult
							+ " for numThreads **********" + numThreads + " and curSeed " + curSeed);
					numErrors++;
				}
				// Use new seed on next try.
				curSeed += stepSeed;
				// Going to create new thread instances.
				runningThreads.clear();
			}
		}
		if (numErrors > 0) {
			System.err.println("******* " + numErrors + " errors were found during this run **********");
		}
		System.out.println("ending a7...\n");
	}

}
