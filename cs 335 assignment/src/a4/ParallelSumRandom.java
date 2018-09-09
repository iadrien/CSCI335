package a4;

import a4Helper.Helper;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * class extends thread to do sum in parallel random order
 * 
 * @author Zuoting Xie
 *
 */
public class ParallelSumRandom extends Thread {

	// declare one double array shared by all instance
	// declare & initialize one int to record the sum, shared by all instances
	// declare one cyclic barrier shared by all instance
	private static double[] valueList;
	private static double sum = 0;
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

				// add the value to the sum
				sum = sum + valueList[index];
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
	 * @param barrierPassed cyclic barrier
	 */
	public void setBarrier(CyclicBarrier barrierPassed) {
		barrier = barrierPassed;
	}

	/**
	 * initialize/set the array
	 * 
	 * @param size integer the size of the array
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
