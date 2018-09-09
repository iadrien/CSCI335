package a4;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import a4Helper.Helper;

public class ParaPrefix extends Thread {

	private static double[] valueList;
	private static double sum = 0;
	private static CyclicBarrier barrier;
	private static int processStage = 1;
	private static boolean barrierReset = false;
	private static boolean stageIncrement = false;
	private static int threadExecuted = 0;

	private int index;
	private int threadStage = 1;

	@Override
	public void run() {

		try {

			valueList[index] = Helper.procValue(index);

			barrier.await();

			while (threadStage < valueList.length) {
				if (index % threadStage == 1) {
					break;
				} else {

					synchronized (valueList) {
						if (threadStage == processStage) {

							if (index + threadStage < valueList.length) {
								valueList[index] = valueList[index]
										+ valueList[index + threadStage];
							}
							threadStage = threadStage * 2;
							threadExecuted = threadExecuted + 1;
						} else {
							valueList.wait();
						}

						double numToExecute = Math.ceil(valueList.length
								/ processStage);
						if (threadExecuted == numToExecute) {
							processStage = processStage * 2;

						}
						valueList.notify();
					}

				}
			}

		} catch (InterruptedException | BrokenBarrierException e) {

			e.printStackTrace();
		}
	}

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
	public double getFinal() {
		return valueList[0];
	}
}
