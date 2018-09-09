package writerReader.vsn335;

// Doug Lea's example with some alterations from
// http://gee.cs.oswego.edu/dl/cpj/classes/
// Special policy inforced:

// Waiting Readers are preffered if there is currently an active Writer,
// but a waiting Writer is preffered if there are active Readers

// Waiting Writers are guaranteed to eneter in FIFO order

class RWVSN {
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

  public void run()
  {
    while (true) {
      ctl.beforeRead();
      System.out.println("reader reading:");
      System.out.println("done reading");
      ctl.afterRead();
    }

  } // end public void run()
}
final class Reader2 extends Thread {
  protected Controller ctl;

  public Reader2(Controller c) { ctl = c;}

  public void run()
  {
    while (true) {
      ctl.beforeRead();
      System.out.println("reader reading:");
      System.out.println("done reading");
      ctl.afterRead();
    }
  } // end public void run()
}

final class Writer1 extends Thread {
  protected Controller ctl;
   
  public Writer1(Controller c) { ctl = c;}

  public void run()
  {
    while (true) {
      ctl.beforeWrite();
      System.out.println("writer writing:");
      System.out.println("done writing");
      ctl.afterWrite();
    }
  } // end public void run()
}

final class Writer2 extends Thread {
  protected Controller ctl;
   
  public Writer2(Controller c) { ctl = c;}

  public void run()
  {
    while (true) {
      ctl.beforeWrite();
      System.out.println("writer writing:");
      System.out.println("done writing");
      ctl.afterWrite();
    }
  } // end public void run()
}

final class Controller {
  protected int activeReaders_ = 0;     // counts
  protected int activeWriters_ = 0;
  protected int waitingReaders_ = 0;
  // the size of the waiting writers vector serves as its count

  // one monitor holds all waiting readers
  protected Object waitingReaderMonitor_ = this;

  // vector of monitors each holding one waiting writer
  protected Vector waitingWriterMonitors_ = new Vector();

  protected boolean allowReader() { // call under proper synch
    return activeWriters_ == 0 && 
           waitingWriterMonitors_.size() == 0;
  }

  protected boolean allowWriter() { 
    return waitingWriterMonitors_.size() == 0 && 
           activeReaders_ == 0 &&
           activeWriters_ == 0;
  } 

  protected void beforeRead() {
    synchronized(waitingReaderMonitor_) {
      synchronized(this) { // test condition under synch
        if (allowReader()) {
          ++activeReaders_;
          return;
        }
        else
          ++waitingReaders_;
      }
      try { waitingReaderMonitor_.wait(); } 
      catch (InterruptedException ex) {}
    }
  }

  protected void beforeWrite() {
    Object monitor = new Object();
    synchronized (monitor) {
      synchronized(this) {
        if (allowWriter()) {
          ++activeWriters_;
          return;
        }
        waitingWriterMonitors_.addElement(monitor); // append
      }
      try { monitor.wait(); } catch (InterruptedException ex) {}
    }
  }


  protected synchronized void notifyReaders() { // waken readers
    synchronized(waitingReaderMonitor_) { 
      waitingReaderMonitor_.notifyAll();
    }
    activeReaders_ = waitingReaders_; // all waiters now active
    waitingReaders_ = 0;
  }



  protected synchronized void notifyWriter() { // waken 1 writer
    if (waitingWriterMonitors_.size() > 0) {
      Object oldest = waitingWriterMonitors_.firstElement();
      waitingWriterMonitors_.removeElementAt(0);
      synchronized(oldest) { oldest.notify(); }
      ++activeWriters_;
    }
  }

  protected synchronized void afterRead()  { 
    if (--activeReaders_ == 0)
      notifyWriter(); 
  }


  protected synchronized void afterWrite() { 
    --activeWriters_;
    if (waitingReaders_ > 0) // prefer waiting readers
      notifyReaders();
    else
      notifyWriter();
  }
}

  class Vector
  {  
    private Object[] list = new Object[10];
    private int capacity =10;
    private int size = 0;
   
    public boolean isEmpty()
    {
        return size==0;
    }
     
    public boolean isFull()
    {  
        return size == capacity;
    }

    public int size()
    {
	return size;
    }
/**
 * Returns the component at the specified index
 */
    public Object elementAt(int index)
    {
        return list[index];
    }
/**
 * Inserts the specified object as a component in this array at the
 * specified index.
 */
    public void insertElementAt(Object obj, int index)
    {
        if(!this.isFull())
        {
            for(int i=size-1; i>=index; i--)
            {
                list[i+1] = list[i];
            }
            list[index] = obj;
            size++;
        }
    }
/**
 * Adds the specified component to the end of this array, increasing its
 * size by one.
 */  
    public void addElement(Object obj)
    {
        if(!this.isFull())
        {
            list[size] = obj;
            size++;
        }
    }        
/**
 * Returns the first component of this array
 */
    public Object firstElement()
    {
        return list[0];
    }
/**
 * Returns the last component of this array
 */
    public Object lastElement()
    {
        return list[size-1];
    }
/**  
 * Deletes the component at the specified index
 */
    public void removeElementAt(int index)
    {
        if(!this.isEmpty())
        {
            for(int i=index; i<size-1; i++)
            {
                list[i] = list[i+1];
            }
            list[size-1] = null;
            size--;
        }
    }
/**
 * Deletes the first element of the Vector
 */  
    public void removeElement()
    {
        if(!this.isEmpty())
        {
            for(int i=0; i<size-1; i++)
            {
                list[i] = list[i+1];
            }
            list[size-1] = null;
            size--;
        }
    }
   
  }

