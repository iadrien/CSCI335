package a7;import java.util.Iterator;import java.util.Random;import java.util.Vector;/** * Thread to add its value to the sum. Values are summed from first to last in * that order. *  * @author Steven Huss-Lederman *  */class ParallelWorkOriginal extends Thread {	/*	 * Most of the following values are static since all instances/threads	 * should see the same value. They are also volatile so they stay consistent	 * between threads/	 */	// Has the setup method been called.	private static volatile boolean initialized = false;	// Sum of the values.	private static volatile int sum;	// The ID number of this thread.	private int myId;	// Object to synchronize on when doing sum wait/notify.	private static volatile Object[] sumLock;	// The thread that should go next. Vector is mostly thread-safe.	private static volatile Vector<Integer> whoGo;	// Iterator on whoGo so threads know who goes next.	private static volatile Iterator<Integer> iWho;	// The thread that should go next.	private static volatile int whoNext;	// The values to add. Vector is mostly thread-safe.	private static volatile Vector<Integer> values;	// Next thread when no more to go. Must not be a valid thread number.	private static final int DONE = -13;	/**	 * Create instance.	 * 	 * @param number	 *            What number is this thread/instance. It should be an index .	 */	public ParallelWorkOriginal(int number) {		// Set thread name to your number.		super("T" + number);		myId = number;	}	/**	 * Initializes needed values. This is not done in the constructor since they	 * are static and only need to be done once. It should only be called once	 * but made synchronized in case this rule is violated. Since this is static	 * if you run it while the thread is active then the outcome is not	 * determined since both will run.	 * 	 * @param numValues	 *            The number of values to be summed. This should be the same as	 *            the number of threads.	 * @param seed	 *            controls value used to start random number generation	 */	public synchronized static void setup(int numValues, int seed) {		// sum is initially zero		sum = 0;		// If first time then need to create.		if (whoGo == null) {			whoGo = new Vector<Integer>(numValues);		}		if (values == null) {			values = new Vector<Integer>(numValues);		}		if (sumLock == null || sumLock.length < numValues) {			sumLock = new Object[numValues];			// Place value at each location.			for (int k = 0; k < numValues; k++) {				sumLock[k] = new Object();			}		}		// If any values already in whoGo then wipe out. Should be same as		// checking initialized and if did not just create.		if (!whoGo.isEmpty()) {			whoGo.clear();		}		if (!values.isEmpty()) {			values.clear();		}		// Set values.		for (int k = 0; k < numValues; k++) {			whoGo.add(k);			values.add(k + 1);		}		java.util.Collections.shuffle(whoGo, new Random(seed * 13));		java.util.Collections.shuffle(values, new Random(seed * 29));		// Now need its iterator that done changing.		iWho = whoGo.iterator();		// Record that initialized.		initialized = true;		// First thread to go.		whoNext = iWho.next();	}	/*	 * Which process runs next. May want to change so make separate method. It	 * is synchronized to be sure it is atomic. In the usage here it should be	 * ok since there is a lock around the code that calls this.	 */	private static synchronized void next() {		if (iWho.hasNext()) {			whoNext = iWho.next();		} else {			whoNext = DONE;		}	}	/**	 * Returns the sum. If done while the sum is being computed then the result	 * is uncertain.	 * 	 * @return Sum of values from starting the threads.	 */	public synchronized static int returnSum() {		if (whoNext == DONE) {			return sum;		} else {			// Cannot get sum until it is done.			// Not the best exception but will work.			throw new RuntimeException("Sum not done so should not try to get result");		}	}	/*	 * Does the parallel add by starting this thread's work.	 */	@Override	public void run() {		if (!initialized) {			// Not the best exception but will work.			throw new RuntimeException("Must initialize ParallelRandom before can start");		}		// Need to hold lock because sum is not atomic. Also, must have for		// wait/notify.		synchronized (sumLock[myId]) {			// Let the first one go so 1 thread runs.			// When this is fixed up it should relate to whoNext.			if (myId != whoNext) {				try {					// Wait for your turn to do sum.					sumLock[myId].wait();				} catch (InterruptedException ex) {					System.err.println(ex);				}			}			// Your turn to go - add sum			sum += values.get(myId);			// Set next person to go			next();			if (whoNext != DONE) {				synchronized (sumLock[whoNext]) {					// Wake up ones waiting since more to do.					sumLock[whoNext].notify();				}			}		}	}}