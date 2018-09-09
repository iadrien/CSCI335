
import java.util.Vector;
import java.util.*;

public class MoreThreads {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int numOfThreads = 5;
		Vector<ActualThreads> sumThreads = new Vector<ActualThreads>();
		
		
		for(int i = 0; i<numOfThreads;i++){
			sumThreads.add(new ActualThreads(Integer.toString(i),i));
		}
		
		for (ActualThreads s : sumThreads) {
			s.start();
		}
		
		for (ActualThreads s : sumThreads) {
			try {
				s.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
		
		Vector newList = sumThreads.get(0).getList();
		
		System.out.println(newList.size());
		
		for(int i = 0; i<newList.size();i++){
			System.out.println("element " + newList.get(i));
		}
		
	}

}
