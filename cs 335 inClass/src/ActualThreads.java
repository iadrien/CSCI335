
import java.util.ArrayList;
import java.util.*;

public class ActualThreads extends Thread {

	private static Vector<Integer> list = new Vector<Integer>();
	private int index;

//	private static List<Integer> synlist = Collections.synchronizedList(list);
	
	
	public ActualThreads(String name, int index) {
		super("T" + name);
		this.index = index;
	}

	public void run() {

		for (int i = 0; i < 5; i++) {
			if (list.add(index)) {
				System.out.println(getName() + " adding successfully!");
			} else {
				System.out.println(getName() + " adding unsuccessfully!");
			}
			
			try {
				sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		

	}

	public Vector<Integer> getList() {

		return list;
	}
}
