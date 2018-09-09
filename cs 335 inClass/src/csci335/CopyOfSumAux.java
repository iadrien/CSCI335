package csci335;

import java.util.concurrent.atomic.AtomicInteger;

public class CopyOfSumAux {
	static AtomicInteger sum = new AtomicInteger(0);
	int localSum = 0;
	// Note synchronized even though not extend Thread
	public  void addOne() {
		sum.addAndGet(1);
		
//		while(!sum.compareAndSet(localSum,localSum+1)){
//		}
//		localSum=localSum+1;
		
		try {
			// How is this sleep different?
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
	}

	public static int getSum() {
		return sum.get();
	}
}
