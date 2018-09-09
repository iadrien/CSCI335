//<PRE>
import java.awt.*; // Panel, Button, Label, TextArea, TextField, Color, etc.
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
// Compilable in Java 1.1.8 for compatibility with Internet Explorer.
// The main differences:  java.awt.* for javax.swing.*; no nextInt(N);
// lose the "J" on component types; TextAreas have the scrollbar built-in; 
// Auction.init() uses "this." instead of "this.getContentPane().";

class AuctionApp
{	
   public static void main (String[] args)
   {	Frame game = new Frame ("AUCTION by William C. Jones");
	game.setSize (750, 560);  // larger than actual applet -- allow for edges
	game.addWindowListener (new WindowCloser());
	Auction display = new Auction();
	display.init();
	game.add (display);
	game.setVisible (true);
   }

   private static class WindowCloser extends java.awt.event.WindowAdapter
   {	public void windowClosing (java.awt.event.WindowEvent e)
	{	System.exit (0);
	}
   }
}

/* The MAIN part of the software (the init method):  22 classes plus 2 above 

	The MAIN part creates the various MODEL, VIEW, and CONTROLLER objects
	used by the software.  12 CONTROLLER classes are defined on pp3-7, as
	inner classes of Auction (to give them access to MODEL and VIEW objects).
	The Auction class (pp1-7) uses the 4 VIEW classes Player (pp8-9, Panel), 
	BidPrompter (p10, Panel), RentLabel (p11, Label), Property (p11, Label); 
	plus the 4 MODEL classes GameStatus (p12), TwoPlayers (p13), TotalGames 
	(p14), and PropList (pp14-16). Those 8 classes are independent of each 
	other except TwoPlayers uses Player, Player uses Property, PropList uses 
	Property & RentLabel. PropList also uses the MODEL Bid class (pp16-19). */

public class Auction extends java.applet.Applet // every Applet is a Panel
{
   public static final int UNIT = 5;  // difference between 2 consec. bids
   public static final int BREAK = 5; // separate first 5 from last 4
   public static final String REPEAT = "repeat game";

   // VIEW objects
   private Panel boardPanel = new Panel();
   private Panel biddingPanel = new Panel();
   private TextArea output = new TextArea (11, 93);
   private TextArea area = new TextArea (15, 75);
   private TextArea crutch = null;  // an option changes this to area
   private BidPrompter bidPrompt = new BidPrompter(); // extends Panel
   private Player humanPanel = new Player ("HUMAN");  // extends Panel
   private Player compyPanel = new Player ("COMPY");  // extends Panel
   private TwoPlayers twoGuys = new TwoPlayers (humanPanel, compyPanel);


   // MODEL objects
   private GameStatus status = new GameStatus();
   private TotalGames gameCounter = new TotalGames();  // tracks num games
   private PropList propList = new PropList();	// tracks 20 properties


   public void init()  // only on initial setup, not for additional games
   {	boardPanel.setLayout (new GridLayout (4, 7, 6, 4));  
	boardPanel.setBackground (Color.white);
	addManyComponentsToBiddingPanel();
	Panel bottom = new Panel();
	bottom.setLayout (new GridLayout (1, 2, 40, 0));
	bottom.add (humanPanel);
	bottom.add (compyPanel);
	this.setLayout (new BorderLayout (2, 2));
	this.add (boardPanel, BorderLayout.NORTH);
	this.add (biddingPanel, BorderLayout.CENTER);
	this.add (bottom, BorderLayout.SOUTH);
	startNewGame ("");
   }

   private void addManyComponentsToBiddingPanel() // only called by init()
   {	biddingPanel.setBackground (Color.yellow);
	biddingPanel.add (bidPrompt);     // using FlowLayout
	biddingPanel.add (newButton ("  bid  ", new MakeBidAl()));
	biddingPanel.add (newButton ("pass", new PassBidAL()));
	biddingPanel.add (newButton (UNIT +" higher", new HigherAL()));
	biddingPanel.add (newButton (UNIT +" lower", new LowerAL()));
	biddingPanel.add (newButton ("start new game", new NewGameAL()));
	biddingPanel.add (newButton (REPEAT, new NewGameAL()));
	biddingPanel.add (newButton ("options", new MoreOptionsAL()));
	biddingPanel.add (output);
	output.addKeyListener (new Responder());
	output.setEditable (false);  // I'll echo user's keys myself
	output.setBackground (new Color (240, 255, 240)); // pastel green
	output.append ("THE AUCTION GAME   by William C. Jones, Jr.\n"
			+ "Objective:  At CASH IN, to have more total value "
			+ "(property@$100...$300 plus cash) than compy.\n"
			+ "The hint is the bid that compy thinks the property "
			+ "is worth (though bids must be multiples of 5).\n"
			+ "If you pass, compy takes it for the hint, "
			+ "rounded down to a multiple of 5 if necessary.\n"
			+ "If you bid what is shown after compy's first bid, "
			+ "you get it for that higher bid.\n"
			+ "If you bid what is shown for your first bid, you "
			+ "get it for that or else compy takes it for 5 more;\n"
			+ "     if the hint ends in 3,4,8,or 9, raise your bid " 
			+ "5 higher to be sure to get the property.\n");
   }

   public Button newButton (String label, ActionListener alis)
   {	Button but = new Button (label);
	but.addActionListener (alis);
	return but;
   }


/* The CONTROL part of the software -- 11 ActionListeners + Keys on 5 pages */

   private class NewGameAL implements ActionListener // click 1 of 2 buttons
   {	public void actionPerformed (ActionEvent ev)
	{	int numSold = propList.numSold();
		if (numSold >= PropList.FIRST_GROUP && numSold < PropList.MAX) 
		{	int score = twoGuys.score();
			int cash = humanPanel.cash() - compyPanel.cash();
			if ((score > 60 && cash > 0) || (score < -60 && cash < 0))
				output.append (gameCounter.updateTotals (score));
		}
		boardPanel.removeAll();
		humanPanel.removeAll();
		compyPanel.removeAll();
		startNewGame (((Button) ev.getSource()).getLabel());
	}
   }

  /** startNewGame resets all variables and displays for the start of a 
	 new game. This may occur in the middle of the current game. 
       Called by init() and also by clicking a NewGameAL button. */

   public void startNewGame (String newGame)
   {	if ( ! newGame.equals (REPEAT))  // no repeat for first game
	{	int id = status.nextGameID();
		propList.shuffleToRandomize (id);
		newGame = "Game " + id;
	}
	twoGuys.makeHumanFirst (true);
	addPropertiesToTopPanel();
	int initial = status.getInitialCash();
	propList.resetIterator (initial);
	humanPanel.addHoldingsToPanel (initial, 0);
	compyPanel.addHoldingsToPanel (initial, status.getLoan());
	int best = propList.chooseBestBid (true, humanPanel.cash(), 
						compyPanel.cash(), 0, crutch);
	bidPrompt.updateForNewBestBid (true, false, false, best);
	int val = best * 100 / ((Property) propList.getNext()).getCost();
	output.append ("\n" + newGame + ": The first " + PropList.FIRST_GROUP
		+ " properties total $" + propList.getTotalOfFirstGroup() 
		+ ".  Compy bids " + val + " on 100s, " + (2 * val) 
		+ " on 200s, and " + (3 * val) + " on 300s.\n");
	biddingPanel.validate();  // re-layout components
   }

   private void addPropertiesToTopPanel()  // only called by startNewGame()
   {	propList.resetIterator (0);
	boardPanel.add (new Label ("Up for bid ==>"));
	for (int k = 0;  k < PropList.TOTAL;  k++)
		boardPanel.add (propList.next());
	boardPanel.add (new Label (""));
	boardPanel.add (new Label (""));  // so 28 labels = 7 x 4
	boardPanel.validate();    // re-layout components
   }



   private class LowerAL implements ActionListener // click "5 lower"
   {	public void actionPerformed (ActionEvent ev)
	{	if (bidPrompt.getDisplayedBid() > 0)
			bidPrompt.changeDisplayedBid (-UNIT);
	}
   }

   private class HigherAL implements ActionListener // click "5 higher"
   {	public void actionPerformed (ActionEvent ev)
	{	int bid = bidPrompt.getDisplayedBid();
		if (bid <= twoGuys.getFirst().cash()
						&& bid < twoGuys.getSecond().cash())
			bidPrompt.changeDisplayedBid (UNIT);
	}
   }

   private class Responder extends java.awt.event.KeyAdapter 
	// respond to certain chars typed inside textarea
   {	public void keyTyped (java.awt.event.KeyEvent ev)
	{	char key = ev.getKeyChar();
		if (key == 'b')
			new MakeBidAl().actionPerformed (null);
		else if (key == 'n')
			new PassBidAL().actionPerformed (null);
		else if (key == 'm')
		{	new HigherAL().actionPerformed (null);
			new MakeBidAl().actionPerformed (null);
		}
		else if (key == 's')
			new NewGameAL().actionPerformed 
				(new ActionEvent (new Button ("start"), 0, null));
		else if (key == 'r')
			new NewGameAL().actionPerformed 
				(new ActionEvent (new Button (REPEAT), 0, null));
	}
   }

   private class PassBidAL implements ActionListener // click "pass"
   {	public void actionPerformed (ActionEvent ev)
	{	if ( ! (propList.getNext() instanceof Property))
			return;
		twoGuys.makeHumanFirst (true);
		output.append (propList.numSold() == BREAK ? ".p" : "p");
		compyPanel.makeSale (propList.next(), boardPanel, 
				twoGuys.bothAreReal() ? bidPrompt.getDisplayedBid() 
					: bidPrompt.getBestBid() / UNIT * UNIT);
		prepareForNextTurn();
	}
   }








   private void prepareForNextTurn() // only called by PassBidAL or MakeBidAl
   {	if ( ! (propList.getNext() instanceof Property))
	{	((RentLabel) propList.getNext()).flashIntermittently();
		if (propList.numSold() >= PropList.MAX) 
		{	output.append (gameCounter.updateTotals (twoGuys.score())); 
			return;
		}
		payOutRentAndShowResults();  // removes the RENT label
	}
	boolean humanFirst = twoGuys.getFirst() == humanPanel;
	int best = propList.chooseBestBid (humanFirst, 
			twoGuys.getFirst().cash(), twoGuys.getSecond().cash(), 
			compyPanel.prop() - humanPanel.prop(), crutch);
	bidPrompt.updateForNewBestBid (humanFirst, humanPanel.cash() <= best, 
						 compyPanel.cash() <= best, best);
	boardPanel.validate();    // re-layout components
   }

   private void payOutRentAndShowResults() // only called by prepareForNextTurn
   {	boardPanel.remove (propList.next());
	humanPanel.updateForIncomeReceived (-1);
	compyPanel.updateForIncomeReceived (-1);
	output.append (((propList.numSold() > PropList.FIRST_GROUP) 
			? "            " : "") + "   After " + propList.numSold() 
		+ ", human: rent= " + (humanPanel.prop() / 10) + ", cash= " 
		+ humanPanel.cash() + ";  compy: rent= " + (compyPanel.prop() / 10) 
		+ ", cash= " + compyPanel.cash() 
		+ ((status.getLoan() == 0) ? "" : ", loan= " + (-status.getLoan()))
		+ ";   human " + ((twoGuys.score() >= 0)
					? "is ahead by " + twoGuys.score() 
					: "is losing by " + (-twoGuys.score())) + ".\n");
   }

   private class MakeBidAl implements ActionListener // click "bid"
   {	public void actionPerformed (ActionEvent ev)
	{	if ( ! (propList.getNext() instanceof Property))
			return;
		int bid = bidPrompt.getDisplayedBid();
		int price = twoGuys.compyPayment (bid, bidPrompt.getBestBid());
		output.append ((propList.numSold() == BREAK ? "." : "") 
				+ bidPrompt.echo (price >= 0, ! twoGuys.bothAreReal()));
		if (price >= 0) 
			compyPanel.makeSale (propList.next(), boardPanel, price);
		else
			humanPanel.makeSale (propList.next(), boardPanel, bid); 
		twoGuys.makeHumanFirst (price >= 0);
		prepareForNextTurn();
	}
   }








   private class MoreOptionsAL implements ActionListener // click "options"
   {	public void actionPerformed (ActionEvent ev)
	{	final Frame opt = new Frame ("MORE OPTIONS FOR AUCTION");
		opt.setLayout (new FlowLayout());
		opt.setSize (600, 360);
		opt.setBackground (new Color (255, 248, 248));
		opt.add (new Label ("Enter a game ID number or initial cash"));
		TextField field = new TextField (10);
		field.addActionListener (new SetStatusAL());
		opt.add (field);
		opt.add (newButton ("vs person   ", new SwitchOpponentAL()));
		opt.add (newButton ("gotcha!", new GotchaAL()));
		opt.add (new Label ("loan compy interest-free?"));
		opt.add (newButton ("loan $50", new MakeItHarderAL()));
		opt.add (newButton ("use the crutch", new DisplayChoiceTreeAL()));
		opt.add (area);
		opt.setVisible (true);
		field.requestFocus();
		opt.addWindowListener (new java.awt.event.WindowAdapter()
		{	public void windowClosing(java.awt.event.WindowEvent ev)
			{	opt.setVisible (false);
			}
		});
	}
   }

   private class MakeItHarderAL implements ActionListener // click "loan 50"
   {	public void actionPerformed (ActionEvent ev)
	{	status.increaseLoan (50);
		area.append ("Compy has an advantage of " + status.getLoan() +"\n");
	}
   }

   private class DisplayChoiceTreeAL implements ActionListener // click "crutch"
   {	public void actionPerformed (ActionEvent ev)
	{	crutch = area;
		area.append ("For each group of 3 properties between RENTs, you "
			+ "have up to 8 different sequences\n of 3 choices.  We list " 
			+ "here all of your choices and their consequences.\n"
			+ "The listing is 4 columns; an entry such as 200C80 in "
			+ "the first 3 columns\n means that on a $200 property Compy "
			+ "buys it for $80.\n The fourth column tells how much "
			+ "cash the two players (H and C) are left with after that \n"
			+ " sequence of choices. It also tells the net change in "
			+ "your cash and property holdings.\n\n");
	}
   }

   private class SwitchOpponentAL implements ActionListener // click "vs..."
   {	public void actionPerformed (ActionEvent ev)
	{	((Button) ev.getSource()).setLabel ("vs " + twoGuys.opponent());
		twoGuys.toggleOpponent();
		area.append ("You are now playing against a " + twoGuys.opponent()
					+ ".\n");
	}
   }

   private class SetStatusAL implements ActionListener // for options TextField
   {	public void actionPerformed (ActionEvent ev)
	{	TextField input = (TextField) ev.getSource();
		try
		{	int num = Integer.parseInt (input.getText());
			if (num < 100)
				area.append ("INVALID: " + num + "\n");
			else if (num < 1000)  // 3 digits
			{	status.setInitialCash (num);
				area.append ("START = $" + num + "\n");
			}
			else  // 4 or more digits
			{	status.setGameID (num);
				area.append ("NEW GAME ID = " + num + "\n");
			}
		}
		catch (RuntimeException e)
		{ 	int num = Math.abs (input.getText().hashCode());
			status.setGameID (num);
			area.append ("numerically, NEW GAME ID = " + num + "\n");
		}
		input.setText ("");
		input.requestFocus();
	}
   }

   private class GotchaAL implements ActionListener // click "gotcha"
   {	public void actionPerformed (ActionEvent ev)
	{	Player seller = twoGuys.getFirst();  // because he lost the bid
		Player buyer  = twoGuys.getSecond();
		int price = buyer.getPriceOfLastBuy();
		if (! twoGuys.bothAreReal() || buyer.cash() < price)
			return;
		Label matching = seller.getMatchFor (buyer.lastPurchaseInitial());
		if (matching != null)
		{	area.append ("Distress sale of " + matching.getText()
						+ " for a price of " + price + ".\n");
			buyer.makeSale (matching, seller, price);
			seller.updateForIncomeReceived (price);
			seller.validate();
		}
	}
   }
} // END OF Auction CLASS
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

   /* Version 1.1 (a) allow words as gameIDs in SetStatusAL (p7); 
	(b) correct toString (p19) to let Human pass e.g. comCash=75, best=79
	(c) correct BidPrompter (p10) to keep width of promptForBid constant
   */






/* The VIEW part of the software:  4 classes on 4 pages: 
		Player (Panel), BidPrompter (Panel), 
		RentLabel (Label), Property (Label) 

	A VIEW object should only have methods that create displayable objects,
	display info on them, and report on the info currently on display.
	The Player class however is not pure VIEW -- in addition to the display 
	info on assets of cashAmt and propAmt, it keeps track of two related
	numbers: loanAmt (a claim against assets) and priceOfLastBuy 
	(the difference between	the previous cashAmt and the current cashAmt).
*/


class Player extends Panel 
{
   private Label cashLabel;	// heading on first column of properties
   private Label propLabel;	// heading on second column of properties
   private String cashTag;	// self-explanatory note on the cashLabel
   private String propTag;	// self-explanatory note on the propLabel
   private int cashAmt = 0;	// amount of cash currently on hand
   private int propAmt = 0;	// value of property currently owned
   private int loanAmt = 0;	// amount to be repaid at the end of the game
   private int priceOfLastBuy = 100000;	// the last property purchase made

   private static final int NUM_BLANKS = 9; // blank labels to fill out grid
   private Label[] blanks = new Label [NUM_BLANKS];	// added as filler
   private int size = NUM_BLANKS;	// number of blanks currently on this panel

   public Player (String whoIsIt) // only called once
   {	cashTag = whoIsIt + " CASH: ";
	propTag = whoIsIt + " PROPERTY: ";
	cashLabel = new Label (cashTag + "000");
	propLabel = new Label (propTag + "0000");
	setLayout (new GridLayout (8, 3, 6, 4));
	for (int k = 0;  k < NUM_BLANKS;  k++)
		blanks[k] = new Label ("");
	setBackground (new Color (240, 240, 255));
   } 

   /** Called once at the beginning of each game. */

   public void addHoldingsToPanel (int initialCash, int loan) 
   {	propAmt = 0;
	cashAmt = initialCash + loan;
	loanAmt = loan;
	priceOfLastBuy = 100000;   // just so it is initially larger than any cash

	propLabel.setText (propTag + propAmt);
	cashLabel.setText (cashTag + cashAmt);
	this.add (propLabel);
	this.add (cashLabel);
	size = NUM_BLANKS;
	for (int k = NUM_BLANKS - 1;  k >= 0;  k--)
		this.add (blanks[k]);
	validate();  // re-layout components
   }
   public int cash()
   {	return cashAmt;
   }

   public int prop()
   {	return propAmt;
   }

   public int assets()
   {	return cashAmt + propAmt - loanAmt;
   }

   public int getPriceOfLastBuy()
   {	return priceOfLastBuy;
   }

   public void makeSale (Label prop, Panel boardPanel, int priceOfProp)
   {	cashAmt -= priceOfProp;
	propAmt += ((Property) prop).getCost();
	priceOfLastBuy = priceOfProp; 

	if (size == 0)
		this.add (prop);
	else
	{	this.add (prop, 2 + NUM_BLANKS - size); // 2,3,...
		boardPanel.add (blanks[--size]);   // also removes from own
	}
	cashLabel.setText (cashTag + cashAmt);
	propLabel.setText (propTag + propAmt);
	this.validate();  // re-layout components
   }

   public void updateForIncomeReceived (int increase) 
   {	cashAmt += (increase >= 0)  ?  increase  :  propAmt / 10;
	cashLabel.setText (cashTag + cashAmt);
   }

   public char lastPurchaseInitial() // only called by GotchaAL
   {	Component[] data = this.getComponents();
	int pos = data.length - 1;
	while (! (data[pos] instanceof Property))//skip blank labels
		pos--;
	return ((Property) data[pos]).getText().charAt (4);
   }

   public Label getMatchFor (char chr) // only called by GotchaAL
   {	Component[] data = this.getComponents();
	for (int pos = data.length - 1;  pos >= 0;  pos--)
	{	if (data[pos] instanceof Property 
				&& ((Property) data[pos]).getText().charAt (4) == chr)
			return (Label) data[pos];
	}
	return null;
   }
}// END OF Player CLASS
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
class BidPrompter extends Panel
{
   public static final String HUM_DOWN = "            You first; bid this?";
   public static final String HUM_UP = "You first; this or more?";
   private Label hintLabel = new Label ("hint=000");
   private Label promptForBid;    // tells human what the bid choices are
   private Label currentBid = new Label ("000");  // current or suggested bid
   private int displayedBid = 0;   // the bid showing in the currentBid label
   private int bestBid = 0;         // best estimate of amount to bid
   private boolean bidHasBeenRaised = false;

   public BidPrompter()
   {	super();
	promptForBid = new Label (HUM_UP, Label.RIGHT);
	add (hintLabel);     // using FlowLayout
	add (promptForBid);
	add (currentBid);
   }

   public void updateForNewBestBid (boolean humanFirst, boolean humanIsBroke, 
					boolean compyIsBroke, int best)
   {	bestBid = best;
	displayedBid = bestBid / Auction.UNIT * Auction.UNIT;
	boolean goUp = bestBid > displayedBid + Auction.UNIT / 2;
	hintLabel.setText ( (goUp && compyIsBroke) ? "CHEAP!"
				: (goUp && humanIsBroke) ? "GONE!"
				: "hint=" + bestBid);
	if (humanFirst)
		promptForBid.setText ((goUp && ! compyIsBroke) ? HUM_UP : HUM_DOWN);
	else
	{	promptForBid.setText ("Compy bids " + displayedBid
				+ ((goUp && ! humanIsBroke) ? "; go up?" : "; you?"));
		displayedBid += Auction.UNIT;
	}
	currentBid.setText ("" + displayedBid);
	bidHasBeenRaised = false;
   }

   public int getBestBid()
   {	return bestBid;
   }

   public int getDisplayedBid()
   {	return displayedBid;
   }
   public void changeDisplayedBid (int change)
   {	displayedBid += change;
	currentBid.setText ("" + displayedBid);
	bidHasBeenRaised = change > 0;
   }

   public String echo (boolean humanLostBid, boolean vsComputer)
   {	return humanLostBid ? "b" : (vsComputer && bidHasBeenRaised) ? "M" : "B";
   }
} // END OF BidPrompter CLASS
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

class RentLabel extends Label  
{
   public RentLabel (String name)
   {	super (name, Label.CENTER);
	setBackground (Color.magenta);
   }

   public void paint (Graphics page)
   {	page.setColor (Color.blue);
	Dimension dim = this.getSize();   // required in Java 1.1, not getHeight()
	int bottom = dim.height;
	int reps = (dim.width - 2) / 8;
	int x = (dim.width - 8 * reps) / 2;
	page.drawLine (x, 1, x, bottom - 1);
	for (;  x < 8 * reps;  x += 8)
	{	page.drawLine (x, 1, x + 4, 5);
		page.drawLine (x + 4, 5, x + 8, 1);
		page.drawLine (x, bottom - 1, x + 4, bottom - 5);
		page.drawLine (x + 4, bottom - 5, x + 8, bottom - 1);
	}
	page.drawLine (x, 1, x, bottom - 1);
   }
   public void flashIntermittently()  // only called from prepareForNextTurn
   {	long later = System.currentTimeMillis() + 1000;  // 1 second
	while (later > System.currentTimeMillis())
	{	this.setBackground (this.getBackground() == Color.white
				? Color.blue : Color.white);
		for (int k = 0;  k < 6000000;  k++)
		{ }
	}
	this.setBackground (Color.magenta);
   }
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

class Property extends Label
{
   private int itsCost;

   public Property (int givenCost, String givenName)
   {	super (givenCost + "   " + givenName);
	itsCost = givenCost;
   }

   public void paint (Graphics page)
   {	page.setColor (Color.black);
	Dimension dim = this.getSize();   // required in Java 1.1, not getHeight()
	page.drawRect (1, 1, dim.width - 2, dim.height - 2);
   }
   public int getCost()
   {	return itsCost;
   }
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

/* The MODEL part of the software:  5 classes on 8 pages: 
		GameStatus, TotalGames, TwoPlayers, PropList, Bid
	A MODEL object store non-IO info.  They should not have methods that 
	obtain input from the user (input comes via parameters) or produce output 
	directly (instead, return a value or maybe send messages to IO objects).
*/


class GameStatus extends Object // tracks status variables set once each game
{
   private int initialCash = 600 + new java.util.Random().nextInt() % 3 * 50;
		// nextInt()%3 is -2 to 2, so the result is 500/550/600/650/700
   private int compyLoan = 0;
   private int gameID = 0; 
   private boolean haveUsedGameID = true;

   public void increaseLoan (int num)	// only called by MakeItHarderAL
   {	compyLoan += num;
   }

   public int getLoan()
   {	return compyLoan;
   }

   public String showLoan()
   {	return (compyLoan == 0) ? "" : ", loan= " + (-compyLoan);
   }

   public void setGameID (int num)		// only called by SetStatusAL
   {	gameID = num;
	haveUsedGameID = false;
   }

   public int nextGameID()
   {	if (haveUsedGameID)
		gameID = 55000 + new java.util.Random().nextInt() % 45000;
		// so the result is 10001 through 99999
	haveUsedGameID = true;
	return gameID;
   }

   public void setInitialCash (int num)	// only called by SetStatusAL
   {	initialCash = num;
   }

   public int getInitialCash()
   {	return initialCash;
   }
} // END OF GameStatus CLASS
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$






class TwoPlayers  // tracks which of the two has the first bid
{
   private Player first, second;
   private final Player human, compy;
   private boolean twoRealPeople = false;

   public TwoPlayers (Player one, Player two)
   {	human = one;
	compy = two;
	makeHumanFirst (true);
   }

   public boolean bothAreReal()
   {	return twoRealPeople;
   }

   public String opponent()
   {	return twoRealPeople ? "person" : "computer";
   }
   public void toggleOpponent()
   {	twoRealPeople = ! twoRealPeople;
   }

   public Player getFirst()
   {	return first;
   }
   public Player getSecond()
   {	return second;
   }

   public void makeHumanFirst (boolean yes)
   {	if (yes)
	{	first = human;
		second = compy;
	}
	else
	{	first = compy;
		second = human;
	}
   }

   public int score()
   {	return human.assets() - compy.assets();
   }

   public int compyPayment (int bid, int best) // -1 if compy doesn't buy it
   {	int half = Auction.UNIT / 2;  // half is 2 if UNIT is 5
	int low = best / Auction.UNIT * Auction.UNIT;  // rounding down
	int p = (first == human && best > low + half) ? low + Auction.UNIT : low;
	return twoRealPeople ? -1 : (bid > human.cash()) ? p : (bid > best) ? -1
			: (bid < low || first == compy) ? p
			: (bid >= best - half || bid >= compy.cash()) ? -1 : p;
   }
} // END OF TwoPlayers CLASS
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

class TotalGames  // tracks multiple games and totals
{
   private int itsNumGames = 0;
   private int itsTotalScore = 0;
   private int itsNumWon = 0;

   public String updateTotals (int score)
   {	itsTotalScore += score;
	itsNumGames++;
	if (score > 0)
		itsNumWon++;
	return "  ==> GAME OVER: Your score =  " + score 
		 + (itsNumGames == 1 ? ".\n" 
			: ".   You won " + itsNumWon + " out of " + itsNumGames 
				+ "; your average is " 
				+ (itsTotalScore / itsNumGames) + ".\n");
   }
} // END OF TotalGames CLASS
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$


class PropList
{
   public static final int MAX = 20;   // number of properties
   public static final int TOTAL = MAX + 5;  // allow for 4 rents + 1 cash-in
   public static final int FIRST_GROUP = 9;
   public static final int UNIT = Auction.UNIT;

   private Label[] allData = new Label [TOTAL];
   private Property[] itsProperty;
   private Label[] fixed = {new RentLabel ("COLLECT RENT"),	
	     new RentLabel ("COLLECT RENT"), new RentLabel ("COLLECT RENT"), 
	     new RentLabel ("COLLECT RENT"), new RentLabel ("CASH IN")};
   private int nextAvailable = 0;
   private int totalOfFirstGroup;
   private int valueOfFirstGroup;

   public PropList()
   {	itsProperty = new Property[MAX];
	String[] name = {"St.Louis RR", "SantaFe RR", "B&O RR", "Boston RR",
				"Arizona", "Alabama", "Connecticut", "California", 
				"Illinois", "Indiana", "Maryland", "Mass.", 
				"New York", "New Jersey", "Oregon", "Oklahoma",
				"Tennessee", "Texas", "Virginia", "Vermont"};
	int[] cost = {100, 100, 100, 100, 100, 100, 100, 100, 200, 200, 200, 200, 
			  200, 200, 200, 200, 300, 300, 300, 300};
	final Color RED = new Color (255, 96, 96);
	final Color GRAY = new Color (216, 216, 216);
	for (int k = 0;  k < MAX;  k++) // inefficient but only done once per game
	{	itsProperty[k] = new Property (cost[k], name[k]);
		itsProperty[k].setBackground (k < 4 ? GRAY : k < 8 ? Color.cyan 
				: k < 12 ? Color.orange : k < 16 ? RED : Color.green); 
	}
   }


   public void resetIterator (int initial)
   {	nextAvailable = 0;
	valueOfFirstGroup = Math.min (64 + Math.max (0, (initial - 650) / 18), 
			initial * 200 / (totalOfFirstGroup + 505 + 
			(1500-totalOfFirstGroup) / 100 * (1000-initial) / 20));
   }

   public Label next()
   {	return allData[nextAvailable++];
   }

   public Label getNext()
   {	return allData[nextAvailable];
   }

   public int numSold() // returns 9, 12, 15, 18 on 10, 14, 18, 22
   {	return nextAvailable <= FIRST_GROUP ? nextAvailable
			: (nextAvailable + 2) - (nextAvailable + 2) / 4;
   }


   /** Produce a random ordering of the 20 properties at the beginning of
       a new game.  Also calculate the total value of the first 9. */

   public void shuffleToRandomize (int givenSeed)
   {	java.util.Random randy = new java.util.Random (givenSeed);
	Property[] copy = new Property[MAX];
	System.arraycopy (itsProperty, 0, copy, 0, MAX);
	for (int k = 0;  k < MAX;  k++)
	{	int spot = k + Math.abs (randy.nextInt()) % (MAX - k);
		Property temp = copy[spot];
		copy[spot] = copy[k];
		copy[k] = temp;
	}

	totalOfFirstGroup = 0;
	for (int k = 0;  k < FIRST_GROUP;  k++)
		totalOfFirstGroup += copy[k].getCost();

	int pos = 0;
	for (int k = 0;  k < MAX;  k++)
	{	allData[pos++] = copy[k];
		if (pos == 9 || pos == 13 || pos == 17 || pos == 21)
			allData[pos++] = fixed[(pos - FIRST_GROUP) / 4];
	}
	allData[pos] = fixed[4];
   }

   public int getTotalOfFirstGroup()
   {	return totalOfFirstGroup;
   }






   public int chooseBestBid (boolean humanFirst, int firstBidder, int other,
					int comPlus, TextArea options)
   {	int cost = ((Property) allData[nextAvailable]).getCost();
	if (nextAvailable < FIRST_GROUP)  // so in the first group of 9
	{	if (nextAvailable == 5 && Math.abs (comPlus) >= 200)
			valueOfFirstGroup -= UNIT * comPlus / Math.abs (comPlus);
		return Math.min (valueOfFirstGroup * cost / 100,
					Math.min (firstBidder, other) + UNIT - 1);
	}
	if (nextAvailable < 22)  // reduce to $75 per 100 except last group
		cost = cost * 3 / 4;
	if ( ! (allData[nextAvailable + 1] instanceof Property)) // one left
		return Bid.bestBid (firstBidder, other, cost).itsBid;
	int b = ((Property) allData[nextAvailable + 1]).getCost() * 3
			/ (nextAvailable < 22 ? 4 : 3);
	if ( ! (allData[nextAvailable + 2] instanceof Property)) // two left
		return Bid.bestBid (firstBidder, other, cost, b).itsBid;
	int c =  ((Property) allData[nextAvailable + 2]).getCost();
	Bid firstOf3 = Bid.bestBid (firstBidder, other, cost, b, c * 3 / 4);
	if (options != null && humanFirst)
		options.append (firstOf3.toString (humanFirst, firstBidder, other));
	else if (options != null)
		options.append (firstOf3.toString (humanFirst, other, firstBidder));
	return firstOf3.itsBid;
   }
}// END OF PropList CLASS
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$



class Bid  // only used by chooseBestBid() in PropList
{
   /** For "me" vs "him":  "me" denotes the one who has first bid. */

   public static final int UNIT = Auction.UNIT;  // for convenience
   private static final Bid EMPTY_BID = new Bid (-101, 10000, 100);

   public final int itsBid;     // always non-negative
   private Bid bestBidIfKept;   // not used by bestBid()
   private Bid bestBidIfLost;   // not used by bestBid()
   private int itsGain;  // gain for whoever is first; thus it may be negative
   private int itsProp;  // always 100 or 200 or 300


   /** A bid on the last property.  Precondition:  itsBid % 5 is 3 or 4 */

   public Bid (int bid, int gain, int prop) // only called by 3-param bestBid()
   {	itsBid = bid; 
	itsProp = prop;
	bestBidIfKept = EMPTY_BID; 
	bestBidIfLost = EMPTY_BID;
	itsGain = gain;
   }



   /** ONE property left.  THREE-parameter bestBid calls THREE-parameter Bid 
	constructor. It is me's bid first. All parameters are non-negative. */

   public static Bid bestBid (int me, int him, int current)
   {	if (me >= current && him >= current)
		return new Bid (current - 1, 0, current);
	else if (him > me) // so me < current
		return new Bid (me + UNIT - 1, me + UNIT - current, current);
	else // so him <= me && him < current
		return new Bid (him + UNIT - 1, current - him, current);
   }

   /** A bid on the next-to-last of two properties before a RENT. 
	Precondition: -5 <= estimate <= minimum of other 3 parameters. */

   public Bid (int estimate, int me, int him, int one, int two)
   {	itsBid = estimate;  // always a multiple of 5
	itsProp = one;
	bestBidIfKept = bestBid (him, me - estimate, two);
	bestBidIfLost = (him <= estimate) ? EMPTY_BID  // gain of infinity
					: bestBid (me, him - estimate - UNIT, two);
	itsGain = Math.min (itsProp - itsBid - bestBidIfKept.itsGain,
			UNIT + itsBid - itsProp + bestBidIfLost.itsGain);
   }

   /** The best bid revised for an estimate 4 higher (to be overbid). */

   public Bid (Bid basis)
   {	this.itsBid = basis.itsBid + 4;
	this.itsProp = basis.itsProp;
	this.bestBidIfKept = basis.bestBidIfKept;
	this.bestBidIfLost = basis.bestBidIfLost;
	this.itsGain = basis.itsGain;
   }

   private int gainIfKept()
   {	return itsProp - itsBid - bestBidIfKept.itsGain;
   }

   private int gainIfLost()  // infinity if opponent cannot overbid 
   {	return itsBid + UNIT - itsProp + bestBidIfLost.itsGain;
   }











   /** TWO properties left.  FOUR-parameter bestBid calls FIVE-parameter Bid 
	constructor, which in turn calls THREE-parameter bestBid. */

   public static Bid bestBid (int me, int him, int one, int two)
   {	int estimate = Math.min (him, Math.min (me, one));
	Bid high = new Bid (estimate, me, him, one, two);
	if (estimate < one && high.gainIfLost() < high.gainIfKept()) 
		return new Bid (high);
	estimate -= UNIT;
	Bid lower = new Bid (estimate, me, him, one, two);
	while (lower.gainIfLost() > lower.gainIfKept() && estimate >= 0)
	{	high = lower;
		estimate -= UNIT;
		lower = new Bid (estimate, me, him, one, two);
	}
	return (estimate < 0 || high.gainIfKept() >= lower.gainIfLost()) 
			? high : new Bid (lower);
   }

   /** A bid on the third-to-last of three before a RENT. */

   public Bid (int estimate, int me, int him, int one, int two, int three)
   {	itsBid = estimate;
	itsProp = one;
	bestBidIfKept = bestBid (him, me - estimate, two, three);
	bestBidIfLost = (him <= estimate) ? EMPTY_BID  // gain of infinity
					: bestBid (me, him - estimate - UNIT, two, three);
	itsGain = Math.min (itsProp - itsBid - bestBidIfKept.itsGain,
			UNIT + itsBid - itsProp + bestBidIfLost.itsGain);
   }

   /** THREE properties left.  FIVE-parameter bestBid calls SIX-parameter Bid 
	constructor, which in turn calls FOUR-parameter bestBid. */

   public static Bid bestBid (int me, int him, int one, int two, 
						int three)
   {	int estimate = Math.min (him, Math.min (me, one));
	Bid high = new Bid (estimate, me, him, one, two, three);
	if (estimate < one && high.gainIfLost() < high.gainIfKept()) 
		return new Bid (high);
	estimate -= UNIT;
	Bid lower = new Bid (estimate, me, him, one, two, three);
	while (lower.gainIfLost() > lower.gainIfKept() && estimate >= 0)
	{	high = lower;
		estimate -= UNIT;
		lower = new Bid (estimate, me, him, one, two, three);
	}
	return (estimate < 0 || high.gainIfKept() >= lower.gainIfLost()) 
			? high : new Bid (lower);
   }

   private static final String BLANKS = "                    ";

   public String toString (boolean humanFirst, int humCash, int comCash)
   {	return "human has $" + humCash + "; compy has $" + comCash 
			+ (humanFirst ? "; human" : "; compy") + "'s bid first.  "
			+ "Your choices are...\n"
		+ toString (humanFirst, "", humCash, comCash, 0, 0, 3) + "\n";
   }

   public String toString (boolean humanFirst, String prefix, int humCash, 
					int comCash, int cahead, int pahead, int level)
   {	if (humCash < 0 || comCash < 0)
		return "";  // skip this line, since it is impossible by the rules
	int padding = Math.max (0, 30 - prefix.length());
	if (this == EMPTY_BID)  // only after 3 bids
		return prefix + BLANKS.substring (0, padding)
				+ "  H$" + humCash + ": C$" + comCash + " so cash: "
				+ cahead + ", prop: " + (pahead * 4 / 3) 
				+ ", net= " + (cahead + pahead * 4 / 3) + "\n";

	int loBid = itsBid - itsBid % UNIT;
	boolean goUp = itsBid % UNIT > UNIT / 2;
	int comPaid = humanFirst ? loBid + UNIT : loBid;
	Bid comBid =  humanFirst ? bestBidIfLost : bestBidIfKept;
	int humPaid = humanFirst ? loBid : loBid + UNIT;
	Bid humBid =  humanFirst ? bestBidIfKept : bestBidIfLost;

	// correction to allow for human choosing a subjectively better bid;
	// omit this if-structure to trace the computer's best-bid reasoning.
	if (humanFirst && goUp && comCash > itsBid) // can bid more to get it
	{	humPaid = loBid + UNIT;
		humBid = (level == 1 || humCash < humPaid) ? EMPTY_BID
				 : (level == 2) ?  bestBid (comCash, humCash - humPaid, 
							bestBidIfKept.itsProp)
				 : bestBid (comCash, humCash - humPaid, 
							bestBidIfKept.itsProp, 
							bestBidIfKept.bestBidIfKept.itsProp);
	}
	else if (humanFirst) // can pass to leave compy with it 
	{	comPaid = loBid;
		comBid = (level == 1) ? EMPTY_BID
				 : (level == 2) ?  bestBid (humCash, comCash - comPaid, 
							bestBidIfKept.itsProp)
				 :  bestBid (humCash, comCash - comPaid, 
							bestBidIfKept.itsProp, 
							bestBidIfKept.bestBidIfKept.itsProp);
	}

	char up = (goUp && (humanFirst ? comCash > itsBid : humCash > itsBid)) 
			? '^' : ' ';
	return comBid.toString (true, prefix + (itsProp * 4 / 3) + "C" + comPaid 
			+ up + "  ", humCash, comCash - comPaid, cahead + comPaid,
			pahead - itsProp, level - 1)
		+ humBid.toString (false, prefix + (itsProp * 4 / 3) + "H" + humPaid 
			+ up + "  ", humCash - humPaid, comCash, cahead - humPaid,
			pahead + itsProp, level - 1);
   }
}// END OF Bid CLASS
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//</pre>

