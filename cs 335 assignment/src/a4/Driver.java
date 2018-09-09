package a4;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;

import a4Helper.Helper;

/**
 * sequential summation in normal order/random order/parallel order parallel
 * summation in normal order/random order
 * 
 * @author Calcifer Xie
 *
 */
public class Driver {

	/**
	 * code to do the sequential summation & to create instance of prallel
	 * summation class
	 * 
	 * @param args
	 *            not used
	 */

	public static void main(String[] args) {

		// system scanner to read user input
		Scanner scan = new Scanner(System.in);

		// declare int variable to record
		// number of values to sum & seed
		// declare double variable to record
		// sum generated by sequential normal order
		// sequential random & sequential parallel
		int numOfValues, seed;
		double sumS, sumR, sumPP;

		// display message to ask for input
		System.out.println("Starting a4!");
		System.out.println("Input number of values to do > 0 "
				+ "and hit return?");

		// do loop to have number of values >0
		do {

			numOfValues = scan.nextInt();

			// display message if number input is no bigger than 0
			if (numOfValues <= 0) {
				System.out.println("Come on! Input something >0!");
			}
		} while (numOfValues <= 0);

		// display message to ask for seed
		System.out.println("Input a seed for the permutation "
				+ "and hit return?");

		// record the user input
		seed = scan.nextInt();

		// create a double list to store the values
		double[] valueList = new double[numOfValues];

		// for loop to store each value generated by
		// calling method in helper with index as parameter
		for (int i = 0; i < numOfValues; i++) {
			valueList[i] = Helper.procValue(i);
		}

		// displaying testing message
		System.out.println("Starting sequential sums....");

		// initialize the standard sum
		// use for loop to sum
		sumS = 0;
		for (int i = 0; i < numOfValues; i++) {
			sumS = sumS + valueList[i];
		}

		// print out the standard sum
		System.out.println("Sum (Standard) = " + sumS);

		// create a int array to record the "random" permutation
		int[] permutationList = Helper.permute(numOfValues, seed);

		// initialize the random sum and
		// use for loop to sum
		sumR = 0;
		for (int i = 0; i < numOfValues; i++) {
			sumR = sumR + valueList[permutationList[i]];
		}

		// print out the random sum & relative error to standard
		System.out.println("Sum (random) = " + sumR);
		System.out.println("Sum (random) difference = " + (sumS - sumR) / sumS);

		// call the method to have parallel sum
		sumPP = paraSum(valueList);

		// print out the parallel sum & relative error to standard
		System.out.println("Sum (parallel prefix) = " + sumPP);
		System.out.println("Sum (parallel prefix) difference = "
				+ (sumS - sumPP) / sumS);

		// display testing message
		System.out.println("Ending sequential sums....");
		System.out.println("Starting parallel sums....");

		// create array list to store the threads for random parallel sum
		ArrayList<ParallelSumRandom> sumThread = new ArrayList<ParallelSumRandom>();

		// create a cyclic barrier
		final CyclicBarrier barrier1 = new CyclicBarrier(numOfValues);

		// create multiple threads each holds a value
		// set the index of the thread & add the thread to the array list
		for (int i = 0; i < numOfValues; i++) {

			ParallelSumRandom ranThreads = new ParallelSumRandom();

			ranThreads.setIndex(i);
			sumThread.add(ranThreads);
		}

		// initialize the static field (array, barrier) in thread class
		sumThread.get(0).setArray(numOfValues);
		sumThread.get(0).setBarrier(barrier1);

		// start the threads in random order
		for (int i = 0; i < numOfValues; i++) {
			sumThread.get(permutationList[i]).start();
		}

		// join the thread in the same random order
		for (int i = 0; i < numOfValues; i++) {

			try {
				sumThread.get(permutationList[i]).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// print out parallel random sum &
		// relative error to serial random sum
		System.out.println("Sum (parallel random) = "
				+ sumThread.get(0).getSum());
		System.out.println("serial vs parallel (random) difference = "
				+ (sumR - sumThread.get(0).getSum()) / sumR);

		// create array list to store the threads for ordered parallel sum
		ArrayList<ParallelSumOrdered> sumThread2 = new ArrayList<ParallelSumOrdered>();

		// create a new cyclic barrier for the new threads
		final CyclicBarrier barrier2 = new CyclicBarrier(numOfValues);

		// create multiple threads each holds a value
		// set the index of the thread & add the thread to the array list
		for (int i = 0; i < numOfValues; i++) {

			ParallelSumOrdered ranThreads = new ParallelSumOrdered();

			ranThreads.setIndex(i);
			sumThread2.add(ranThreads);
		}

		// initialize the static field (array, barrier) in thread class
		sumThread2.get(0).setArray(numOfValues);
		sumThread2.get(0).setBarrier(barrier2);

		// start the thread
		for (int i = 0; i < numOfValues; i++) {
			sumThread2.get(i).start();
		}

		// join the thread
		for (int i = 0; i < numOfValues; i++) {

			try {
				sumThread2.get(i).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// print out paralle ordered sum &
		// relative error to serial ordered sum
		System.out.println("Sum (parallel ordered) = "
				+ sumThread2.get(0).getSum());
		System.out.println("serial vs parallel (ordered/normal) difference = "
				+ (sumS - sumThread2.get(0).getSum()) / sumS);

		// display testing message
		System.out.println("Ending arallel sums");
		System.out.println("ending a4..");
		
		scan.close();
		System.exit(0);
	}

	/**
	 * code to do serial parallel sum
	 * 
	 * @param toSumList
	 *            double list contains doubles to sum up
	 * @return the sum of the list
	 */
	public static double paraSum(double[] toSumList) {

		// outer loop to monitor the stage
		// inner loop do the calculation of each stage
		for (int k = 1; k < toSumList.length; k = k * 2) {
			for (int i = 0; i < toSumList.length; i = i + 2 * k) {
				// if the index goes out of bound
				// do nothing
				if (i + k < toSumList.length) {
					toSumList[i] = toSumList[i] + toSumList[i + k];
				}
			}
		}

		// return the sum stored in the array at index 0
		return toSumList[0];
	}
}
