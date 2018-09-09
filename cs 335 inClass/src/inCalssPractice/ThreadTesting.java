package inCalssPractice;

import java.util.ArrayList;

/**
 * program using multiple threads to do summation for number>=0 case tested and
 * succeeded: 1,2,3,4,5,6,7,8,10,88,90 case tested and failed:-1(return 0),
 * -8(return 0)
 * 
 * @author Zuoting Xie
 * @version 2, 9/15/2015
 *
 */

public class ThreadTesting {

	public static void main(String[] args) throws InterruptedException {

		// display testing code starting message
		System.out.println("The testing code is beginning!");

		// check if the length of args is equal to 1
		// if so do nothing, else execute the code
		if (args.length != 1) {

		} else {

			// declare variables needed
			// convert the string to integer
			int num = Integer.parseInt(args[0]);

			// initialize String name as part of thread name
			String name = "my thread #";

			// initialize arraylist of ThreadCreation to store ThreadCreation
			// object
			ArrayList<ThreadCreation> waitingThreads = new ArrayList<ThreadCreation>();

			// initialize integers
			int initialNum = 1;// the number that the summation starts from
			int numInt = 0;// the number that the summation ends at(inclusive)
			int addStep = 0;// the number of successive integers to sum
			int sum = 0;// the sum
			int remain = 0;// the number of integers that aren't added to summ

			// set numInt to a value to test
			numInt = 90;

			
			// num = number of threads&addstep=calculation for each thread
			// because of truncation sometimes it misses a number or two
			// numInt = num*addstep + remain
			addStep = numInt / num;
			remain = numInt % num;

			// loop to create threads
			for (int i = 0; i < num; i++) {

				// create threads with name "my thread #"+index
				ThreadCreation test = new ThreadCreation(name
						+ String.valueOf(i + 1));

				// add the thread to the arraylist
				waitingThreads.add(test);

				// set the initial values so the thread can do
				// the calculation appropriately
				test.setValues(initialNum, addStep);

				// modify the values so next thread can do a different
				// calculation
				initialNum = initialNum + addStep;

				// start the thread
				test.start();

			}

			// check if there is any integer that is not added
			if (remain != 0) {

				// create an extra thread named "extra thread"
				ThreadCreation extraTest = new ThreadCreation("extra thread");

				// set the values so it adds up sums of 
				// the integer remained unsummed
				extraTest.setValues(numInt - remain + 1, remain);

				// add the extra thread to the arraylist
				waitingThreads.add(extraTest);

				// run the thread
				extraTest.start();

			}

			// loop to make sure each thread finishes in timely manner
			// and sum up sub-sums provided in each thread
			for (int i = 0; i < waitingThreads.size(); i++) {

				waitingThreads.get(i).join();
				sum = sum + waitingThreads.get(i).sum();
			}

			// display the final sum
			System.out.println(sum);
		}

	}
}
