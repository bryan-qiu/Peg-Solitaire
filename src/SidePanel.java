/** 
 * The "SidePanel" class
 * Allows for the use of a Side Panel in a game of Peg Solitaire
 * The Side Panel contains choice lists and buttons
 * @author Alvin Qiu and Bryan Qiu
 * @version January 2012
 */

import java.awt.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.*;
import java.util.Hashtable;

@SuppressWarnings("serial")
public class SidePanel extends JPanel implements ActionListener, ItemListener
{
	public final Dimension PANEL_SIZE = new Dimension(170, 400);
	private PegSolitaireBoard gamePanel;
	Choice typeChoice, setUpChoice;
	JButton undoButton, solutionButton;
	private int boardSetUp, boardType; // 0: Cross, 1: Octagon, 2: Triangle
	private String[] crossSetUp = new String[] { "Solitaire", "Cross", "Plus", "Fireplace", "Pyramid", "Arrow",
			"Double Arrow", "Diamond" };
	private String[] octagonSetUp = new String[] { "Snowflake", "X-It", "Pinpoint" };
	private String[] triangleSetUp = new String[] { "Hole at (2,1)", "Hole at (0,0)", "Hole at (1,0)", "Hole at (2,0)" };
	private boolean hintOn, gameOver, waiting, paused;
	private String hintMessage;
	private int waitTime;
	private JSlider speedSlider;
	
	/**
	 * Constructs a new SidePanel object with given PegSolitaireBoard object
	 * @param gameBoard the given PegSolitaireBoard object
	 */
	public SidePanel(PegSolitaireBoard gameBoard)
	{
		// Initialize variables
		setPreferredSize(PANEL_SIZE);
		boardType = 0;
		boardSetUp = 0;
		this.gamePanel = gameBoard;
		this.setLayout(null);
		hintOn = false;
		gameOver = false;
		waiting = false;
		waitTime = 3;
		paused = false;
		
		// Note: The setBounds method sets the position (x,y) and size (width, height) of a component

		// Choice list for Board Type
		typeChoice = new Choice();
		typeChoice.add("Cross");
		typeChoice.add("Octagon");
		typeChoice.add("Triangle");
		typeChoice.setBounds(10, 10, 150, 40);
		this.add(typeChoice);
		typeChoice.addItemListener(this);

		// Choice list for board set up
		setUpChoice = new Choice();
		for (String next : crossSetUp)
			setUpChoice.add(next);
		setUpChoice.setBounds(10, 50, 150, 40);
		this.add(setUpChoice);
		setUpChoice.addItemListener(this);

		// Add Buttons
		undoButton = new JButton("Undo Move");
		undoButton.setBounds(10, 190, 150, 40); 
		this.add(undoButton);
		undoButton.addActionListener(this);

		solutionButton = new JButton("Find Solution");
		solutionButton.setBounds(10, 240, 150, 40);
		this.add(solutionButton);
		solutionButton.addActionListener(this);
		
		// Set up JSlider
		speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 40, 20);
		//testSlider.setSnapToTicks(true);
		
		// Set major and minor tick marks.
		speedSlider.setMajorTickSpacing(10);
		speedSlider.setMinorTickSpacing(5);
		speedSlider.setPaintTicks(true);

		// Set Labels
		Hashtable labelTable = new Hashtable();
		labelTable.put(new Integer(0), new JLabel("Stop"));
		labelTable.put(new Integer(11), new JLabel("Slow"));
		labelTable.put(new Integer(40), new JLabel("Fast"));
		speedSlider.setLabelTable(labelTable);
		speedSlider.setPaintLabels(true);

		
		speedSlider.setBounds(10, 320, 145, 40);
		speedSlider.setFont(new Font("Arial", Font.PLAIN, 10));  
		this.add(speedSlider);
		speedSlider.setBackground(new Color(190, 118, 52));

		// Gets the speed of animation from the slider
		speedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				JSlider source = (JSlider) e.getSource();
				if (source.getValue() == 0)
					paused = true;
				else
					paused = false;
				// Change the speed
				gamePanel.changeAnimateSpeed(source.getValue());
				repaint();
			}
		});

		repaint();
	}

	/**
	 * Turns hints on
	 */
	public void hintOn()
	{
		hintOn = true;
		if (gameOver)
			hintMessage = "Game Over";
		else
		{
			hintMessage = "Waiting...";
			paintImmediately(0, 0, (int) PANEL_SIZE.getWidth(), (int) PANEL_SIZE.getHeight());
			hintMessage(gamePanel.hasSolution());
		}
		repaint();
	}
	
	/**
	 * Turns hints off
	 */
	public void hintOff()
	{
		hintOn = false;
		repaint();
	}
	
	/**
	 * Disables features (e.g. Buttons) as game is waiting
	 */
	public void waiting()
	{
		waiting = true;
		setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR));
	}
	
	/**
	 * Resumes features (e.g. Buttons) as game is done waiting
	 */
	public void doneWaiting()
	{
		waiting = false;
		setCursor (Cursor.getDefaultCursor());
	}
	
	/**
	 * Changes the hint wait time to a given time
	 * @param time the hint's wait time (in seconds)
	 */
	public void changeWaitTime(int time)
	{
		waitTime = time;
		repaint();
	}
	
	/**
	 * Allows for the use of the sidePanel buttons when game starts
	 */
	public void gameStart()
	{
		gameOver = false;
		if (hintOn)
		{
			hintMessage = "Waiting...";
			paintImmediately(0, 0, (int) PANEL_SIZE.getWidth(), (int) PANEL_SIZE.getHeight());
			hintMessage(gamePanel.hasSolution());
		}
		repaint();
	}
	
	/**
	 * Prevents the use of the sidePanel buttons when game is over
	 */
	public void gameOver()
	{
		gameOver = true;
		if (hintOn)
			hintMessage = "Game Over";
		repaint();
	}
	
	/**
	 * Generates the appropriate hint message given messageType
	 * @param messageType the type of message
	 */
	public void hintMessage(int messageType)
	{
		if (messageType == 0)
			hintMessage = "Unsolvable";
		else if (messageType == 1)
			hintMessage = "Solvable";
		else if (messageType == 2)
			hintMessage = "Unknown";
		else if (messageType == 3)
			hintMessage = "Waiting...";

		paintImmediately(0, 0, (int) PANEL_SIZE.getWidth(), (int) PANEL_SIZE.getHeight());
	}
	
	/**
	 * Response to a change in one of the choice lists
	 * @param event the event that called this method
	 */
	public void itemStateChanged(ItemEvent event)
	{
		if (event.getItemSelectable() == typeChoice)
		{
			if (typeChoice.getSelectedIndex() == 0) // Cross board type
			{
				// Change board
				boardType = 0;
				boardSetUp = 0;
				gamePanel.changeBoardType(boardType);
				gamePanel.changeBoardSetUp(boardSetUp);
				//Update Choice list
				setUpChoice.removeAll();
				for (String next : crossSetUp)
					setUpChoice.add(next);
			}
			else if (typeChoice.getSelectedIndex() == 1) // Octagon board type
			{
				// Change board
				boardType = 1;
				boardSetUp = 0;
				gamePanel.changeBoardType(boardType);
				gamePanel.changeBoardSetUp(boardSetUp);
				//Update Choice list
				setUpChoice.removeAll();
				for (String next : octagonSetUp)
					setUpChoice.add(next);
			}
			else if (typeChoice.getSelectedIndex() == 2) // Triangle board type
			{
				// Change board
				boardType = 2;
				boardSetUp = 0;
				gamePanel.changeBoardType(boardType);
				gamePanel.changeBoardSetUp(boardSetUp);
				//Update Choice list
				setUpChoice.removeAll();
				for (String next : triangleSetUp)
					setUpChoice.add(next);
				
			}
			repaint();
		}
		else if (event.getItemSelectable() == setUpChoice)
		{
			// Change board set up
			boardSetUp = setUpChoice.getSelectedIndex();
			gamePanel.changeBoardSetUp(boardSetUp);
			repaint();
		}
	}
	
	/**
	 * Handles the button choice
	 * @param event the event that called this method (only one)
	 */
	public void actionPerformed(ActionEvent event)
	{
		if (event.getSource() == solutionButton)
		{
			if (gameOver || waiting)
				return;
			
			setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR));
			gamePanel.animateSolution();
		}
		else if (event.getSource() == undoButton)
		{
			if (gameOver || waiting)
				return;
			
			gamePanel.undoMove();
		}
		repaint();
	}

	/**
	 * Repaint the board's drawing panel
	 * @param g The Graphics context
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		// Fill background
		g.setColor(new Color(190, 118, 52));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		// Display hints
		g.setColor(Color.BLACK);
		g.setFont(new Font("Cambria", Font.BOLD, 17));
		if (hintOn)
		{
			g.drawString("Hints: On", 10, 110);
			g.drawString("Wait Time: " + waitTime + " sec", 10, 140);
			g.drawString("Status: " + hintMessage, 10, 170);
		}
		else // Hint off
		{
			g.drawString("Hints: Off", 10, 110);
			g.drawString("Wait Time: " + waitTime + " sec", 10, 140);
		}
		
		// Display animation messages
		g.setFont(new Font("Arial", Font.BOLD, 13));
		g.drawString("Animation Speed", 25, 310);
		g.setFont(new Font("Arial", Font.ITALIC, 13));
		if (paused)
			g.drawString("Animation PAUSED", 24, 380);
	} // paint component method
}
