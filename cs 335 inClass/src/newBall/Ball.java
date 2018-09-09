package newBall;

/**
 * Holds the ball and keeps track of when someone has it.
 * 
 * @author Steven Huss-Lederman
 * 
 */
public class Ball {
	private final static int TOTAL_BALLS = 1;
	private static int numBalls= TOTAL_BALLS;

	/**
	 * If a ball is available then it is removed so another player cannot get
	 * it.
	 * 
	 * @return True if the ball was available and allocated. False if ball not
	 *         given (not currently available).
	 */
	public static synchronized boolean getBall() {
		if (numBalls != 0) {
			// Give a ball.
			numBalls--;
			return true;
		} else {
			// No ball available.
			return false;
		}
	}

	/**
	 * Return ball. Cannot fail.
	 */
	public static synchronized void giveBall() {
		numBalls++;
	}
}
