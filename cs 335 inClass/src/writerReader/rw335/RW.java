package writerReader.rw335;

// Doug Lea's example with some alterations from
// http://gee.cs.oswego.edu/dl/cpj/classes/
// Special policy inforced:
// Block incoming Readers if there are waiting Writers

class RW {
    static Controller ctl;

    public static void main (String argv[]) {
	ctl = new Controller();
	new Reader1(ctl).start();
	new Reader2(ctl).start();
	new Writer1(ctl).start();
	new Writer2(ctl).start();
    }
}


final class Reader1 extends Thread {
  protected Controller ctl;

  public Reader1(Controller c) { ctl = c;}

/**
 * @observable predset1
 *    LOCATION[run1] reader1run;
 */
  public void run()
  {
    while (true) {
      ctl.beforeRead();
      run1:
      System.out.println("reader reading:");
      System.out.println("done reading");
      ctl.afterRead();
      System.out.println("after read");
    }

  } // end public void run()
}

final class Reader2 extends Thread {
  protected Controller ctl;

  public Reader2(Controller c) { ctl = c;}

/**
 * @observable predset1
 *    LOCATION[run1] reader2run;
 */

  public void run()
  {
    while (true) {
      ctl.beforeRead();
      run1:
      System.out.println("reader reading:");
      System.out.println("done reading");
      ctl.afterRead();
    }
  } // end public void run()
}

final class Writer1 extends Thread {
  protected Controller ctl;

  public Writer1(Controller c) { ctl = c;}

/**
 * @observable predset1
 *    LOCATION[run1] writer1run;
 */
  public void run()
  {
    while (true) {
      ctl.beforeWrite();
      run1:
      System.out.println("writer writing:");
      System.out.println("done writing");
      ctl.afterWrite();
    }
  } // end public void run()
}

final class Writer2 extends Thread {
  protected Controller ctl;
   
  public Writer2(Controller c) { ctl = c;}

/**
 * @observable predset1
 *    LOCATION[run1] writer2run;
 */
  public void run()
  {
    while (true) {   
      ctl.beforeWrite();
      run1:
      System.out.println("writer writing:");
      System.out.println("done writing");
      ctl.afterWrite();
    }
  } // end public void run()
}

/**
 * @observable
 *    EXP ActiveWritersRange: (activeWriters_ >=0 && activeWriters_ <= 1);
 *    EXP ReaderAllowed: (activeWriters_ == 0 && waitingWriters_ == 0);
 *    EXP WriterAllowed: (activeReaders_ == 0 && activeWriters_ == 0);
 */
final class Controller {
  protected int activeReaders_ = 0;  // threads executing read_
  protected int activeWriters_ = 0;  // always zero or one
  protected int waitingReaders_ = 0; // threads not yet in read_
  protected int waitingWriters_ = 0; // same for write_


  protected boolean allowReader() {
    return waitingWriters_ == 0 && activeWriters_ == 0;
  }

  protected boolean allowWriter() {
    return (activeReaders_ == 0) && (activeWriters_ == 0);
  }

/**
 * @assert
 *     POST ReaderIn: (activeWriters_ == 0 && waitingWriters_ == 0);
 */
  protected synchronized void beforeRead() {
    ++waitingReaders_;
    while (!allowReader())
      try { wait(); } catch (InterruptedException ex) {}
    --waitingReaders_;
    ++activeReaders_;
  }

  protected synchronized void afterRead()  { 
    --activeReaders_;
    notifyAll();
  }

/**
 * @assert
 *     POST  WriterIn: (activeReaders_ == 0 && activeWriters_ == 1);
 */
  protected synchronized void beforeWrite() {
    ++waitingWriters_;
    while (!allowWriter()) 
      try { wait(); } catch (InterruptedException ex) {}
    --waitingWriters_;
    ++activeWriters_;
  }

  protected synchronized void afterWrite() { 
    --activeWriters_;
    notifyAll();
  }

}

