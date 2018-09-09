package writerReader.notify335;

// Unrolled and not inlined version with 2 readers and 2 writers
// No data structure used for readers and writers to work with
// Specific notification locks are used to improve performance

class RWnotif {
	static Controller ctl;

	public static void main(String argv[]) {
		ctl = new Controller();

		new Reader1(ctl).start();
		new Reader2(ctl).start();
		new Writer1(ctl).start();
		new Writer2(ctl).start();
	}
}

final class Reader1 extends Thread {
	protected Controller ctl;

	public Reader1(Controller c) {
		ctl = c;
	}

	public void run() {
		while (true) {
			ctl.startRead();
			System.out.println("reader reading:");
			System.out.println("done reading");
			ctl.stopRead();
		}

	} // end public void run()
}

final class Reader2 extends Thread {
	protected Controller ctl;

	public Reader2(Controller c) {
		ctl = c;
	}

	public void run() {
		while (true) {
			ctl.startRead();
			System.out.println("reader reading:");
			System.out.println("done reading");
			ctl.stopRead();
		}
	} // end public void run()
}

final class Writer1 extends Thread {
	protected Controller ctl;

	public Writer1(Controller c) {
		ctl = c;
	}

	public void run() {
		while (true) {
			ctl.startWrite();
			System.out.println("writer writing:");
			System.out.println("done writing");
			ctl.stopWrite();
		}
	} // end public void run()
}

final class Writer2 extends Thread {
	protected Controller ctl;

	public Writer2(Controller c) {
		ctl = c;
	}

	public void run() {
		while (true) {
			ctl.startWrite();
			System.out.println("writer writing:");
			System.out.println("done writing");
			ctl.stopWrite();
		}
	} // end public void run()
}

class Controller {
	private int nr = 0;
	private int nw = 0;
	private Object or = new Object();
	private Object ow = new Object();

	public void startRead() {
		synchronized (or) {
			while (!checkRead())
				try {
					or.wait();
				} catch (InterruptedException e) {
				}
		}
	}

	private synchronized boolean checkRead() {
		if (nw == 0) {
			nr++;
			return true;
		} else
			return false;
	}

	public void stopRead() {
		synchronized (this) {
			nr--;
		}
		synchronized (ow) {
			ow.notify();
		}
	}

	public void startWrite() {
		synchronized (ow) {
			while (!checkWrite())
				try {
					ow.wait();
				} catch (InterruptedException e) {
				}
		}
	}

	private synchronized boolean checkWrite() {
		if ((nw == 0) && (nr == 0)) {
			nw++;
			return true;
		} else
			return false;
	}

	public void stopWrite() {
		synchronized (this) {
			nw--;
		}
		synchronized (or) {
			or.notifyAll();
		}
		synchronized (ow) {
			ow.notify();
		}
	}
}
