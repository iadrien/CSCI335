package csci335;

public class Sum3 extends Thread {
	
	
//	CopyOfSumAux use;
	SumAux use;
	
//	Sum3(CopyOfSumAux use) {
//		this.use = use;
//	}
	
	Sum3(SumAux use) {
		this.use = use;
	}
	public void run() {
		for (int i = 0; i < 50; i++) {
			use.addOne();
//			try {
//				sleep(1);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//				return;
//			}
		}
	}
}
