package newBall;

/**
 * Solves ballgame where kids share one ball.
 * 
 * @author Steven Huss-Lederman
 */
public class Driver {

	/**
	 * Runs the show.
	 * 
	 * @param args
	 *            Not used.
	 */
	public static void main(String[] args) {
		System.out.println("Starting ball game");
		// Number of players in the game.
		int numPlayers = 3;
		// Create display for the needed number of balls.
		Display gameBoard = new Display(numPlayers);
	}
}
