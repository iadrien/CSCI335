package a4;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import a4Helper.Helper;

/**
 * class extends thread to do sum in parallel normal order
 * 
 * @author Zuoting Xie
 *
 */
public class ParallelSumOrdered extends Thread {

	// declare one double array shared by all instance
	// declare & initialize one int to record the sum, shared by all instances
	// declare & initialize on int to record whose turn to run
	// declare one cyclic barrier shared by all instance
	private static double[] valueList;
	private static double sum = 0;
	private static int turn = 0;
	private static CyclicBarrier barrier;

	// instance/thread specific int to record which number it holds
	private int index;

	// actual code to do calculation/sum
	@Override
	public void run() {

		try {

			// initialize one element of the array
			valueList[index] = Helper.procValue(index);

			// wait until all other threads finish initializing
			barrier.await();

			// lock the array so the operation would be "atomic"
			synchronized (valueList) {

				// check if it is the turn to run
				// if not, wait
				while (index != turn) {

					valueList.wait();
				}

				// calculation
				// change the trun and notify all other threads
				// when calculation done
				sum = sum + valueList[index];
				turn = turn + 1;
				valueList.notifyAll();
			}
		} catch (InterruptedException | BrokenBarrierException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * set the index
	 * 
	 * @param index
	 *            integer so thread know which number in the array it holds
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * set the cyclic barrier
	 * 
	 * @param barrierPassed
	 *            cyclic barrier
	 */
	public void setBarrier(CyclicBarrier barrierPassed) {
		barrier = barrierPassed;
	}

	/**
	 * initialize/set the array
	 * 
	 * @param size
	 *            integer the size of the array
	 */
	public void setArray(int size) {
		valueList = new double[size];
	}

	/**
	 * return the sum
	 * 
	 * @return integer sum of the values in the array
	 */
	public double getSum() {
		return sum;
	}
}
