package newBall;

import java.util.Random;

/**
 * Represents a kid in the game. Each kid is their own thread. They constantly
 * try to get a ball and if they get one they display this and hold it for
 * random amount of time.
 * 
 * @author Steven Huss-Lederman
 * 
 */
public class Kid extends Thread {
	// Kid's number.
	private int myNum;
	// Display to use.
	private Display gameBoard;
	// Maximum wait in ms.
	private final int MAX_WAIT = 2000;
	// Minimum wait in ms.
	private final int MIN_WAIT = 1000;
	// Used to generate random wait.
	private Random ran;
	// Run as long as false.
	private volatile boolean done;

	/**
	 * Creates Kid and records needed information provided.
	 * 
	 * @param number
	 *            The number of this Kid. Must be between 0 and number of
	 *            players minus 1 for game to work.
	 * @param gameBoard
	 *            Display associated with the game.
	 */
	public Kid(int number, Display gameBoard) {
		myNum = number;
		this.gameBoard = gameBoard;
		ran = new Random();
		done = false;
	}

	/**
	 * The kid plays the game.
	 */
	@Override
	public synchronized void run() {
		while (!done) {
			if (Ball.getBall()) {
				// Show who has ball via print. Helps to see if same kid gets
				// twice in a row.
				System.out.println("Kid " + myNum + " has ball");
				// Now have ball so show it.
				gameBoard.showBall(myNum);
				// Wait a random amount between min and max.
				try {
					int waitTime = (int) (ran.nextDouble() * MAX_WAIT);
					if (waitTime < MIN_WAIT) {
						waitTime = MIN_WAIT;
					}
					sleep(waitTime);
				} catch (InterruptedException e) {
					// If sleep fails then ignore since only changes timing.
				}
				// Done with ball so hide.
				gameBoard.hideBall(myNum);
				// Give it back.
				Ball.giveBall();
				// Yield so others get chance to get ball.
				notify();
				
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// Whether got ball or failed you try again.
		}
	}

	public void setDone() {
		done = true;
	}
}
