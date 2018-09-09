package newBall;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This class creates the visual display for the ballgame.
 * 
 * @author Steven Huss-Lederman
 **/
public class Display extends JFrame {
	// Define the colors for having a ball and not.
	private final Color NO_BALL_COLOR = Color.gray;
	private final Color BALL_COLOR = Color.GREEN;
	// The maximum with of the frame but can be exceeded if too many balls.
	private final int MAX_WIDTH = 1000;
	// Make sure that the square showing the ball never gets too small if there
	// a lot of balls.
	private final int MIN_WIDTH_PER_BALL = 5;
	// Stores a reference to all squares for balls.
	private Vector<Space> sp = new Vector<Space>();
	// Number of players and thus the number of squares displayed.
	private int numPlay;
	// Holds buttons.
	private JButton startButton, stopButton, quitButton;
	// Holds list of all kids playing game.
	Vector<Kid> kids;

	/**
	 * Constructs a display where squares are placed for each player in the
	 * game. All players do not have a ball at the start.
	 * 
	 * @param numPlayers
	 *            How many players.
	 */
	public Display(int numPlayers) {
		numPlay = numPlayers;
		// The with of the square for player where this is the desired size.
		int widthPerPlayer = 25;
		// The width of the entire frame.
		int frameWidth = 25 * numPlay;
		if (frameWidth > MAX_WIDTH) {
			// The frame is too wide so reduce the width for each player.
			widthPerPlayer = MAX_WIDTH / numPlay;
			if (widthPerPlayer < MIN_WIDTH_PER_BALL) {
				// It was too narrow for each player so reset and know that the
				// total frame with will expand.
				widthPerPlayer = MIN_WIDTH_PER_BALL;
			}
			frameWidth = widthPerPlayer * numPlay;
		}
		// The height is the same as the width but may not appear that way
		// because of aspects ratios of the screen.
		int frameHeight = widthPerPlayer;
		// If the user closes the window then close the application.
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(frameWidth, frameHeight);
		setTitle("Ball Game");

		// Put the player squares in a panel so can be placed as a group on the
		// frame.
		JPanel pp = new JPanel();
		// Create a one by number of players grid so that when the squares are
		// placed they come out in a straight line.
		pp.setLayout(new GridLayout(1, numPlay));
		// Create a square for each player that is empty and remember each
		// square in the vector.
		for (int i = 0; i < numPlay; i++) {
			sp.add(new Space(NO_BALL_COLOR));
			pp.add(sp.lastElement());
		}
		// Add panel with player squares to frame.
		add(pp, BorderLayout.NORTH);

		// Create start and quit buttons in a panel.
		JPanel pb = new JPanel();
		startButton = new JButton();
		startButton.setText("Start");
		pb.add(startButton);
		stopButton = new JButton();
		stopButton.setText("Stop");
		// Cannot stop until start.
		stopButton.setEnabled(false);
		pb.add(stopButton);
		quitButton = new JButton();
		quitButton.setText("Quit");
		pb.add(quitButton);
		// Add panel with buttons to frame.
		add(pb, BorderLayout.SOUTH);

		// Action to take when start button pushed. Game begins.
		startButton.addActionListener(new ActionListener() {
			/**
			 * @param evt unused 
			 */
			@Override
			public void actionPerformed(ActionEvent evt) {
				// Cannot restart until stop so fix up buttons.
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				// Create list to hold kids.
				kids = new Vector<Kid>();
				// Create needed number of kids/players.
				for (int i = 0; i < numPlay; i++) {
					// Display.this is how you get your hands on the outer class
					// reference.
					Kid k = new Kid(i, Display.this);
					kids.add(k);
					k.start();
				}
			}
		});

		// Action to take when start button pushed. Game begins.
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// Now cannot stop until start so fix up buttons.
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				// Notify all kids to stop.
				for (Kid k : kids) {
					k.setDone();
				}
				/*
				 * There is a subtle bug here. If you hit start too quickly then
				 * all the threads may not be done. Then multiple threads are
				 * trying at the same time. I have not yet carefully figured out
				 * the issue and put it aside for now.
				 */
			}
		});

		// Action to take when quit button pushed. Program stops.
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		});

		// Get everything to show up properly on the frame.
		pack();

		// Now that it is set up show to the user.
		setVisible(true);
	}

	/**
	 * For the player number provided, make it so the graphic indicates the
	 * player has no ball.
	 * 
	 * @param which
	 *            The number of the player to use. If it is less than 0 or
	 *            greater than the number of players minus 1 then throw an
	 *            exception.
	 */
	public void hideBall(int which) {
		if (which >= 0 && which < numPlay) {
			// Set this player to have no
			// ball.
			sp.get(which).setColor(NO_BALL_COLOR);
		} else {
			throw new IllegalArgumentException("The ball number of " + which
					+ " is not valid (<0 and < " + numPlay + ")");
		}
	}

	/**
	 * For the player number provided, make it so the graphic indicates the
	 * player has a ball.
	 * 
	 * @param which
	 *            The number of the player to use. If it is less than 0 or
	 *            greater than the number of players minus 1 then throw an
	 *            exception.
	 */
	public void showBall(int which) {
		if (which >= 0 && which < numPlay) {
			// Set this player to have a
			// ball.
			sp.get(which).setColor(BALL_COLOR);
		} else {
			throw new IllegalArgumentException("The ball number of " + which
					+ " is not valid (>=0 and < " + numPlay + ")");
		}
	}

	/* Nested class that creates a square for a player. */
	class Space extends JPanel {
		/*
		 * Construct a square of the color provided. Also give it a black
		 * border.
		 */
		Space(Color c) {
			setBackground(c);
			setBorder(BorderFactory.createLineBorder(Color.black));
		}

		/* Change the background color to the one provided. */
		public void setColor(Color c) {
			setBackground(c);
		}
	}
}