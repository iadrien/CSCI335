package writerReader.empty335;

import java.util.Random;

// Unrolled and not inlined version with 2 readers and 2 writers
// No data structure used for readers and writers to work with
// No priorities given or policies used in this example

class RWempty {
	static Controller ctl;

	public static void main(String argv[]) {
		ctl = new Controller();

		// Different readers at different speeds but writers have same timings
//		new Reader(ctl, "r1", 100, 1000).start();
//		new Reader(ctl, "r2", 500, 2000).start();
//		new Writer(ctl, "w1", 100, 1000).start();
//		new Writer(ctl, "w2", 500, 2000).start();

		// Writers sleep less so run more.
//		new Reader(ctl, "r1", 250, 1000).start();
//		new Reader(ctl, "r2", 500, 2000).start();
//		new Writer(ctl, "w1", 100, 500).start();
//		new Writer(ctl, "w2", 200, 500).start();

		// Readers sleep less so run more.
		new Reader(ctl, "r1", 100, 500).start();
		new Reader(ctl, "r2", 200, 500).start();
		new Writer(ctl, "w1", 250, 1000).start();
		new Writer(ctl, "w2", 500, 2000).start();
	}
}

final class Reader extends Thread {
	protected Controller ctl;
	Random ran;
	int minSleep, maxSleep;

	public Reader(Controller c, String name, int minTime, int maxTime) {
		super(name);
		ran = new Random();
		minSleep = minTime;
		maxSleep = maxTime;
		ctl = c;
	}

	public void run() {
		long time;
		int numReaders;
		String stars;
		while (true) {
			time = minSleep + ran.nextInt(maxSleep - minSleep);
			System.out.println("reader " + getName() + " sleeping for time "
					+ time);
			try {
				sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			numReaders = ctl.startRead();
			time = minSleep + ran.nextInt(maxSleep - minSleep);
			stars = "";
			for (int i = 0; i < numReaders; i++) {
				stars += "*";
			}
			System.out.println(stars + "reader " + getName()
					+ " reading for time " + time);
			try {
				sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			numReaders = ctl.stopRead();
			stars = "";
			for (int i = 0; i < numReaders; i++) {
				stars += "*";
			}
			System.out.println(stars + "reader " + getName() + " done reading");
		}
	}
}

final class Writer extends Thread {
	protected Controller ctl;
	Random ran;
	int minSleep, maxSleep;

	public Writer(Controller c, String name, int minTime, int maxTime) {
		super(name);
		ran = new Random();
		minSleep = minTime;
		maxSleep = maxTime;
		ctl = c;
	}

	public void run() {
		long time;
		while (true) {
			time = minSleep + ran.nextInt(maxSleep - minSleep);
			System.out.println("writer " + getName() + " sleeping for time "
					+ time);
			try {
				sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ctl.startWrite();
			time = minSleep + ran.nextInt(maxSleep - minSleep);
			System.out.println("#writer " + getName() + " writing for time "
					+ time);
			try {
				sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("#writer " + getName() + " done writing:");
			ctl.stopWrite();
		}
	}
}

final class Controller {
	protected int activeReaders = 0;
	protected boolean writerPresent = false;

	protected boolean writeCondition() {
		return activeReaders == 0 && !writerPresent;
	}

	protected boolean readCondition() {
		return !writerPresent;
	}

	protected synchronized int startRead() {
		while (!readCondition()) {
			try {
				wait();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		++activeReaders;
		return activeReaders;
	}

	protected synchronized int stopRead() {
		int num = activeReaders--;
		notifyAll();
		return num;
	}

	protected synchronized void startWrite() {
		while (!writeCondition()) {
			try {
				wait();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		writerPresent = true;
	}

	protected synchronized void stopWrite() {
		writerPresent = false;
		notifyAll();
	}
}
