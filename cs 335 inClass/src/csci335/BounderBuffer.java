package csci335;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * This class implements the bounded buffer. It can use the Java Collections or
 * an array for the implementation.
 * 
 * @author Steven Huss-Lederman
 * 
 * @param <T>
 *            The type of object to store.
 */
public class BounderBuffer<T> {
	// Which implementation of the bounded buffer to use.
	private int typeQueue;
	/**
	 * type to pass the constructor to use the Java Collections class for the
	 * implementation.
	 **/
	public static final int BB_COLLECTION = 10;
	/**
	 * type to pass the constructor to use an array for the implementation.
	 **/
	public static final int BB_ARRAY = 20;
	// The maximum number of items in the bounded buffer.
	private int maxSize;
	// Use the Collections class array-based blocking queue if doing a
	// Collections implementation.
	private ArrayBlockingQueue<T> queue;
	/*
	 * Use an array if doing an array implementation. The items are stored in a
	 * circular buffer.
	 * 
	 * invariant:
	 * 
	 * 0 <= numOccupied <= buffer.length
	 * 
	 * and
	 * 
	 * 0 <= firstOccupied < buffer.length
	 * 
	 * buffer[(firstOccupied + i) % buffer.length] contains the (i+1)th oldest
	 * entry, for all i such that 0 <= i < numOccupied
	 */
	private T buffer[];
	// Number of items in the bounded buffer for array case.
	private int numOccupied;
	// Index of first occupied item in bounded buffer for array case.
	private int firstOccupied;

	/**
	 * Constructor for the correct type and size of bounded buffer.
	 * 
	 * @param type
	 *            One of the standard types of allowed bounded buffer. See above
	 *            for known types.
	 * @param size
	 *            The maximum number of items allowed in the bounded buffer.
	 * @exception IllegalArgumentException
	 *                Thrown if size is zero or less or type is unknown.
	 * 
	 *                Must suppress the warning about typecast due to the use of
	 *                an array and parameterized types. If the code is properly
	 *                implemented then this is safe.
	 */
	@SuppressWarnings("unchecked")
	public BounderBuffer(int type, int size) {
		// The maximum number of items must be positive.
		if (size <= 0) {
			throw new IllegalArgumentException("size must be > 0 but was "
					+ size);
		}
		// Record maximum number of entries.
		maxSize = size;
		if (type == BB_COLLECTION) {
			// If put a breakpoint here or before any other ArrayBlockingQueue
			// call then can step into it in the debugger. If have loaded Java
			// sources onto your machine and set up in Eclipse then can see the
			// source. We will do this to see how it is implemented.
			// Create appropriate Collections class data structure to hold the
			// bounded buffer.
			queue = new ArrayBlockingQueue<T>(maxSize);
		} else if (type == BB_ARRAY) {
			// Allocate the array for the bounded buffer and set values so no
			// items are stored.
			buffer = (T[]) new Object[maxSize];
			numOccupied = 0;
			firstOccupied = 0;
		} else {
			// Unknown type so exception.
			throw new IllegalArgumentException("Unknown type of " + type);
		}
		// Record type of queue being used.
		typeQueue = type;
	}

	/**
	 * Inserts an item into the queue. This will block if the queue is full.
	 * Once space becomes available it will continue.
	 * 
	 * @param item
	 *            Object to add to the queue.
	 * @exception RuntimeException
	 *                If inserting fails.
	 */
	public void insert(T item) {
		if (typeQueue == BB_COLLECTION) {
			try {
				// Add item to the Collections queue. Note if you use add it
				// won't block.
				queue.put(item);
			} catch (InterruptedException e) {
				// This is not expected but stop if it ever occurs.
				e.printStackTrace();
				throw new RuntimeException("interrupted while adding to buffer");
			}
		} else if (typeQueue == BB_ARRAY) {
			/* This uses an array implementation. */
			// You must synchronize this code to avoid race conditions. It is
			// also necessary for using wait/notify.
			// Use the instance of this class.
			synchronized (this) {
				// You could also synchronize on the array that holds the
				// bounded buffer. Not necessarily better just shown as an
				// example.
				// synchronized (buffer) {
				while (numOccupied == buffer.length) {
					// wait for space
					try {
						wait();
						// You must call weight on the buffer object if you
						// synchronized on buffer.
						// buffer.wait();
					} catch (InterruptedException e) {
						// This is not expected but stop if it ever occurs.
						e.printStackTrace();
						throw new RuntimeException(
								"interrupted while adding to buffer");
					}
				}
				// There is space so add the item.
				buffer[(firstOccupied + numOccupied) % buffer.length] = item;
				numOccupied++;
				// In case any retrieves are waiting for data, wake them.
				notifyAll();
				// You must call wait on the buffer object if you synchronized
				// on buffer.
				// buffer.notifyAll();
			}
		}
	}

	/**
	 * Returns an item from the bounded buffer. This will block if the queue is
	 * empty. Once an item becomes available it will continue.
	 * 
	 * @return Next item in the buffer.
	 * @exception RuntimeException
	 *                If inserting fails.
	 */
	public T retrieve() {
		// Item that will be returned. Set to null so Java happy. Must be one of
		// these choices due to constructor.
		T item = null;
		if (typeQueue == BB_COLLECTION) {
			try {
				// Remove item to the Collections queue.
				item = queue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
				// This is not expected but stop if it ever occurs.
				e.printStackTrace();
				throw new RuntimeException("interrupted while adding to buffer");
			}
		} else if (typeQueue == BB_ARRAY) {
			/* This uses an array implementation. */
			// You must synchronize this code to avoid race conditions. It is
			// also necessary for using wait/notify.
			// Use the instance of this class.
			synchronized (this) {
				// You could also synchronize on the array that holds the
				// bounded buffer. Not necessarily better just shown as an
				// example.
				// synchronized (buffer) {
				while (numOccupied == 0) {
					try {
						wait();
						// You must call wait on the buffer object if you
						// synchronized on buffer.
						// buffer.wait();
					} catch (InterruptedException e) {
						// This is not expected but stop if it ever occurs.
						e.printStackTrace();
						throw new RuntimeException(
								"interrupted while adding to buffer");
					}
				}
				// Item available in buffer so remove it.
				item = buffer[firstOccupied];
				// May help garbage collector
				buffer[firstOccupied] = null;
				// Reset location of first item in circular queue.
				firstOccupied = (firstOccupied + 1) % buffer.length;
				numOccupied--;
				// In case any inserts are waiting for space, wake them
				notifyAll();
				// You must call wait on the buffer object if you synchronized
				// on buffer.
				// buffer.notifyAll();
			}
		}
		// Item to return no matter which storage method used.
		return item;
	}
}