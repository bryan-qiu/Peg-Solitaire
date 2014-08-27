/** 
 * The "PegSolitaireBoard" class
 * Handles the board play for a game of Peg Solitaire
 * @author Alvin Qiu and Bryan Qiu
 * @version January 2012
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.util.*;

@SuppressWarnings("serial")
public class PegSolitaireBoard extends JPanel implements MouseListener, MouseMotionListener
{
	public final Dimension PANEL_SIZE = new Dimension(400, 400);
	private SidePanel sidePanel;
	private int boardSetUp, boardType; // 0: Cross, 1: Octagon, 2: Triangle
	private Image crossBoardImage, octagonBoardImage, triangleBoardImage, pieceImage, highlightImage;
	private int board[][] = new int [7][7];
	private ArrayList<Piece> pieceList;
	private LinkedList<Piece> removedPieceStack;
	private Piece selectedPiece;
	private Point lastPoint;
	private boolean hintOn, gameOver, animating;
	private volatile boolean waiting;
    private int[][] movesMade = new int[40][4];
    private int numMoves, animateSpeed;
    private Timer tDoneWaiting,tAnimate;
    private Point mouseMovePoint;
    
    private Set<Long> minBoard = new HashSet<Long>();
    private long startTime;
    private int[][][] solutionMoves = new int[40][2][2];
    private int minPegs, startPegs, waitTime, curPiece, curStep;
    private int[][] holeNumber = new int[7][7];
    private int[][][] currentMoves = new int[40][2][2];
    
	/**
	 * Constructs a new PegSolitaireBoard object
	 */
	public PegSolitaireBoard()
	{
		// Initialize variables
		setPreferredSize(PANEL_SIZE);
		boardType = 0;
		boardSetUp = 0;
		hintOn = false;
		waiting = false;
		waitTime = 3;
		animating = false;
		mouseMovePoint = new Point(0,0);

		// Set background and get images
		setBackground(new Color(190, 118, 52));
		crossBoardImage = new ImageIcon("Images\\CrossBoard.png").getImage();
		octagonBoardImage = new ImageIcon("Images\\OctagonBoard.png").getImage();
		triangleBoardImage = new ImageIcon("Images\\TriangleBoard.png").getImage();
		pieceImage = new ImageIcon("Images\\Piece.gif").getImage();
		highlightImage = new ImageIcon("Images\\Highlight.gif").getImage();

		// Add mouse listeners and mouse motion listeners to game board
		addMouseListener(this);
		addMouseMotionListener(this);
		
		// Set up the StopWaiting timer
		tDoneWaiting = new Timer(0, new DoneWaiting());
		tDoneWaiting.setInitialDelay(1);
		
		// Set up the Animate timer
		animateSpeed = 20;
		tAnimate = new Timer((41 - animateSpeed)*(41 - animateSpeed), new Animating());
		tAnimate.setInitialDelay(800);
		
		newGame();
	}

	/**
	 * Attach a given SidePanel to gameBoard
	 * @param sidePanel the SidePanel to attach
	 */
	public void addSidePanel(SidePanel sidePanel)
	{
		this.sidePanel = sidePanel;
	}
	
	/**
	 * Starts a new game
	 */
	public void newGame()
	{
		// If animating last game, stop animation
		tAnimate.stop();
		animating = false;
		
		if (boardType == 0) // Cross Board
		{
			// Create an empty board
			for (int i = 0; i < 7; i++)
			{
				for (int j = 0; j < 7; j++)
					board[i][j] = 0;
			}
			board[0][0] = 2; board[0][1] = 2; board[0][5] = 2; board[0][6] = 2;
			board[1][0] = 2; board[1][1] = 2; board[1][5] = 2; board[1][6] = 2;
			board[5][0] = 2; board[5][1] = 2; board[5][5] = 2; board[5][6] = 2;
			board[6][0] = 2; board[6][1] = 2; board[6][5] = 2; board[6][6] = 2;
			
			// Add in board Set Ups
			if (boardSetUp == 0) // Solitaire
			{
				for (int i = 0; i < 7; i++)
				{
					for (int j = 0; j < 7; j++)
						board[i][j] = 1;
				}
				board[0][0] = 2; board[0][1] = 2; board[0][5] = 2; board[0][6] = 2;
				board[1][0] = 2; board[1][1] = 2; board[1][5] = 2; board[1][6] = 2;
				board[5][0] = 2; board[5][1] = 2; board[5][5] = 2; board[5][6] = 2;
				board[6][0] = 2; board[6][1] = 2; board[6][5] = 2; board[6][6] = 2;
				board[3][3] = 0;
			}
			else if (boardSetUp == 1) // Cross
			{
				board[1][3] = 1;
				board[2][2] = 1; board[2][3] = 1; board[2][4] = 1;
				board[3][3] = 1;
				board[4][3] = 1;
			}
			else if (boardSetUp == 2) // Plus
			{
				board[1][3] = 1;
				board[2][3] = 1;
				board[3][1] = 1; board[3][2] = 1; board[3][3] = 1; board[3][4] = 1; board[3][5] = 1;
				board[4][3] = 1;
				board[5][3] = 1;
			}
			else if (boardSetUp == 3) // Fireplace
			{
				board[0][2] = 1; board[0][3] = 1; board[0][4] = 1;
				board[1][2] = 1; board[1][3] = 1; board[1][4] = 1;
				board[2][2] = 1; board[2][3] = 1; board[2][4] = 1;
				board[3][2] = 1; board[3][4] = 1;
			}
			else if (boardSetUp == 4) // Pyramid
			{
				board[1][3] = 1;
				board[2][2] = 1; board[2][3] = 1; board[2][4] = 1;
				board[3][1] = 1; board[3][2] = 1; board[3][3] = 1; board[3][4] = 1; board[3][5] = 1;
				board[4][0] = 1; board[4][1] = 1; board[4][2] = 1; board[4][3] = 1; board[4][4] = 1; board[4][5] = 1; board[4][6] = 1;
			}
			else if (boardSetUp == 5) // Arrow
			{
				board[0][3] = 1;
				board[1][2] = 1; board[1][3] = 1; board[1][4] = 1;
				board[2][1] = 1; board[2][2] = 1; board[2][3] = 1; board[2][4] = 1; board[2][5] = 1;
				board[3][3] = 1;
				board[4][3] = 1;
				board[5][2] = 1; board[5][3] = 1; board[5][4] = 1;
				board[6][2] = 1; board[6][3] = 1; board[6][4] = 1; 
			}
			else if (boardSetUp == 6) // Double Arrow
			{
				board[0][3] = 1;
				board[1][2] = 1; board[1][3] = 1; board[1][4] = 1;
				board[2][1] = 1; board[2][2] = 1; board[2][3] = 1; board[2][4] = 1; board[2][5] = 1;
				board[3][2] = 1; board[3][3] = 1; board[3][4] = 1;
				board[4][1] = 1; board[4][2] = 1; board[4][3] = 1; board[4][4] = 1; board[4][5] = 1;
				board[5][2] = 1; board[5][3] = 1; board[5][4] = 1;
				board[6][3] = 1;
			}
			else if (boardSetUp == 7) // Diamond
			{
				board[0][3] = 1;
				board[1][2] = 1; board[1][3] = 1; board[1][4] = 1;
				board[2][1] = 1; board[2][2] = 1; board[2][3] = 1; board[2][4] = 1; board[2][5] = 1;
				board[3][0] = 1; board[3][1] = 1; board[3][2] = 1; board[3][4] = 1; board[3][5] = 1; board[3][6] = 1;
				board[4][1] = 1; board[4][2] = 1; board[4][3] = 1; board[4][4] = 1; board[4][5] = 1;
				board[5][2] = 1; board[5][3] = 1; board[5][4] = 1;
				board[6][3] = 1;
			}
		}
		
		else if (boardType == 1) // Octagon Board
		{
			// Create an empty board
			for (int i = 0; i < 7; i++)
			{
				for (int j = 0; j < 7; j++)
					board[i][j] = 0;
			}
			board[0][0] = 2; board[0][1] = 2; board[0][5] = 2; board[0][6] = 2;
			board[1][0] = 2; board[1][6] = 2;
			board[5][0] = 2; board[5][6] = 2;
			board[6][0] = 2; board[6][1] = 2; board[6][5] = 2; board[6][6] = 2;
			
			// Add in board Set Ups
			if (boardSetUp == 0) // Snowflake
			{
				board[0][3] = 1;
				board[1][1] = 1; board[1][3] = 1; board[1][5] = 1;
				board[2][2] = 1; board[2][3] = 1; board[2][4] = 1;
				board[3][0] = 1; board[3][1] = 1; board[3][2] = 1; board[3][3] = 1; board[3][4] = 1; board[3][5] = 1; board[3][6] = 1;
				board[4][2] = 1; board[4][3] = 1; board[4][4] = 1;
				board[5][1] = 1; board[5][3] = 1; board[5][5] = 1;
				board[6][3] = 1;
			}
			else if (boardSetUp == 1) // X-It
			{
				board[1][1] = 1; board[1][2] = 1; board[1][3] = 1; board[1][4] = 1; board[1][5] = 1;
				board[2][1] = 1; board[2][2] = 1; board[2][4] = 1; board[2][5] = 1;
				board[3][1] = 1; board[3][3] = 1; board[3][5] = 1;
				board[4][1] = 1; board[4][2] = 1; board[4][4] = 1; board[4][5] = 1;
				board[5][1] = 1; board[5][2] = 1; board[5][3] = 1; board[5][4] = 1; board[5][5] = 1;
			}
			else if (boardSetUp == 2) // Pinpoint
			{
				board[0][3] = 1;
				board[1][1] = 1; board[1][2] = 1; board[1][4] = 1; board[1][5] = 1;
				board[2][1] = 1; board[2][5] = 1;
				board[3][0] = 1; board[3][1] = 1; board[3][3] = 1; board[3][5] = 1; board[3][6] = 1;
				board[4][1] = 1; board[4][5] = 1;
				board[5][1] = 1; board[5][2] = 1; board[5][4] = 1; board[5][5] = 1;
				board[6][3] = 1;
			}
		}
		
		else if (boardType == 2) // Triangle Board
		{
			// Create a full board
			for (int i = 0; i < 7; i++)
			{
				for (int j = 0; j < 7; j++)
				{
					if (j <= i && i < 5)
						board[i][j] = 1;
					else
						board[i][j] = 2;
				}
			}
			
			// Add in board Set Ups
			if (boardSetUp == 0) // Hole at (2,1)
			{
				board[2][1]  = 0;
			}
			else if (boardSetUp == 1) // Hole at (0,0)
			{
				board[0][0]  = 0;
			}
			else if (boardSetUp == 2) // Hole at (1,0)
			{
				board[1][0]  = 0;
			}
			else if (boardSetUp == 3) // Hole at (2,0)
			{
				board[2][0]  = 0;
			}
		}
		
		// Create pieceList
		pieceList = new ArrayList<Piece>();
		if (boardType == 0 || boardType == 1) // if Cross or Octagon
		{
			for (int i = 0; i < 7; i++)
			{
				for (int j = 0; j < 7; j++)
				{
					if (board[i][j] == 1)
						pieceList.add(new Piece(i, j, boardType, this));
				}
			}
		}
		else if (boardType == 2) // Triangle
		{
			for (int i = 0; i < 5; i++)
			{
				for (int j = 0; j < 5; j++)
				{
					if (board[i][j] == 1)
						pieceList.add(new Piece(i, j, boardType, this));
				}
			}
		}
		
		// Initialize variables
		gameOver = false;
		if (sidePanel != null)
			sidePanel.gameStart();
		removedPieceStack = new LinkedList<Piece>();
		selectedPiece = null;
		numMoves = -1;
		minPegs = 99;
		
		repaint();
	}

	/**
	 * Changes boardType with given board type
	 * @param type given board type
	 */
	public void changeBoardType(int type)
	{
		boardType = type;
		newGame();
	}
	
	/**
	 * Changes boardSetUp with given board set up
	 * @param setUp given board set up
	 */
	public void changeBoardSetUp(int setUp)
	{
		boardSetUp = setUp;
		newGame();
	}

	/**
	 * Turns hints on
	 */
	public void hintOn()
	{
		hintOn = true;
	}
	
	/**
	 * Turns hints off
	 */
	public void hintOff()
	{
		hintOn = false;
	}
	
	/**
	 * Changes the hint wait time to a given time
	 * @param time the hint's wait time (in seconds)
	 */
	public void changeWaitTime(int time)
	{
		waitTime = time;
	}
	
	/**
	 * Changes the speed of animation based on given value
	 * @param time the hint's wait time (in seconds)
	 */
	public void changeAnimateSpeed(int sliderVal)
	{
		if (sliderVal == 0) // Paused
			tAnimate.stop();
		else // Not paused
		{
			tAnimate.setDelay((41 - animateSpeed)*(41 - animateSpeed));
			
			// If was previously paused
			if (animating && animateSpeed == 0)
				tAnimate.start();
		}
		// Change speed
		animateSpeed = sliderVal;
	}
	
	/**
	 * Pauses animation because window is minimized
	 */
	public void minimizePause()
	{
		tAnimate.stop();
	}
	
	/**
	 * Un-pauses animation because window is un-minimized
	 */
	public void unminimizeUnpause()
	{
		// If was previously animating
		if (animating && animateSpeed != 0)
			tAnimate.start();
	}
	
	/**
	 * Checks if a move is valid given starting position and the proposed new position
	 * @param startRow start row of a piece
	 * @param startCol start column of a piece
	 * @param newRow new row of a piece
	 * @param newCol new column of a piece
	 * @return
	 */
	private boolean isValidMove(int startRow, int startCol, int newRow, int newCol)
	{
		// Check if the move was valid (current piece jumped into an empty spot with another piece in between
		if (board[newRow][newCol] == 0 && board[(startRow + newRow) / 2][(startCol + newCol) / 2] == 1)
		{
			if (boardType == 0 || boardType == 1) // Cross or Octagon
			{
				// Check up, down, left, right directions
				// Jump is only 2 spaces away
				if ((Math.abs(startRow - newRow) == 2 && startCol == newCol) ||
					(Math.abs(startCol - newCol) == 2 && startRow == newRow))
					return true;
			}
			else if (boardType == 2) // Triangle
			{
				// Check left, right, diagonal directions
				// Jump is only 2 spaces away
				if ((Math.abs(startRow - newRow) == 2 && startCol == newCol) ||
					(Math.abs(startCol - newCol) == 2 && startRow == newRow) ||
					Math.abs((startRow - newRow) + (startCol - newCol)) == 4)
					return true;
			}
		}
		return false;
	}

	/**
	 * Undo the last move made
	 */
	public void undoMove()
	{
		//If there aren't any moves
		if (numMoves < 0)
			return;
		
		// Gets info from array
		int startRow = movesMade[numMoves][0];
		int startCol = movesMade[numMoves][1];
		int endRow = movesMade[numMoves][2];
		int endCol = movesMade[numMoves][3];
		
		// Return board to previous state
		board[startRow][startCol] = 1;
		board[(startRow + endRow) / 2][(startCol + endCol) / 2] = 1;
		board[endRow][endCol] = 0;
		
		// Find moved piece
		int movedPieceIdx = -1;
		for (int i = 0; i < pieceList.size(); i++)
		{
			if (pieceList.get(i).getRow() == endRow && pieceList.get(i).getCol() == endCol)
			{
				movedPieceIdx = i;
				break;
			}
		}
		pieceList.get(movedPieceIdx).setPosition(startRow, startCol); // Set back to old location
		
		pieceList.add(removedPieceStack.pop());
		numMoves--;
		
		if (hintOn)
		{
			sidePanel.hintMessage(3);
			paintImmediately(0, 0, (int) PANEL_SIZE.getWidth(), (int) PANEL_SIZE.getHeight());
			sidePanel.hintMessage(hasSolution());
		}
		
		repaint();
	}

	// Inner class for changing waiting status
	private class DoneWaiting implements ActionListener
	{
		/**
		 * Response to a change in the timer
		 * @param e the event that called this method
		 */
		public void actionPerformed(ActionEvent e)
		{
			waiting = false;
			sidePanel.doneWaiting();
			tDoneWaiting.stop();
			
			// Set the cursor to the hand if on piece
	        for (Piece next: pieceList)
	        {
	            if (next.contains (mouseMovePoint))
	            {
	                setCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
	                return;
	            }
	        }

	        // Otherwise we just use the default cursor
	        setCursor (Cursor.getDefaultCursor ());
		}
	}
	
	/**
	 * Animates the solution
	 */
	public void animateSolution()
	{
		setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR));
		
		//Number of pegs on the board before the solution is found
		startPegs = pegsOnBoard();
		//Number of moves in the solution should be startPegs - minPegs
		
		minBoard = new HashSet<Long>();
		minPegs = 99;
		startTime = System.nanoTime();
		setHoleNumber(board);
		

		if (boardType == 0 || boardType == 1) // Cross or Octagon Board
			findSolution(0);
		else // Triangular Board
			findSolutionTri(0);

		sidePanel.setCursor (Cursor.getDefaultCursor ());
        setCursor (Cursor.getDefaultCursor ());
		
		if (minPegs == -1) // Unknown solution
		{
			JOptionPane.showMessageDialog(this, "The solution is unknown.", "Find Solution", 
					JOptionPane.ERROR_MESSAGE);
		}
		else 
		{
			// Confirm whether solution should be animated
			if (JOptionPane.showConfirmDialog(this, "The board can be solved to a minimum of " +
					minPegs + " peg(s).\n\nWould you like to animate the solution?", "Find Solution",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				// End game and begin animating.
				gameOver = true;				
				sidePanel.gameOver();
				
				curStep = 0; // is 0 instead of 1 to allow to a correct pause at the beginning (adds an extra buffer for the counter)
				curPiece = 0;
				animating = true;
				if (animateSpeed != 0) // Not paused
					tAnimate.start();
			}
		}
	}
	
	// Inner class for changing waiting status
	private class Animating implements ActionListener
	{
		/**
		 * Response to a change in the timer
		 * @param e the event that called this method
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (curPiece == startPegs - minPegs -1 && curStep == 3) // If Done animating solution
			{
				tAnimate.stop();
				// animating will become false at the start of a new game
			}
			else // Still animating
			{
				if (curStep == 3) // un-highlight step done
				{
					// Move to next piece at step 1
					curStep = 1;
					curPiece++;
				}
				else // Not step 3
				{
					if (curStep == 1) // if it is the 1st step
					{
						// Move pieces for next animation
						
						int startRow = solutionMoves[curPiece][0][0];
						int startCol = solutionMoves[curPiece][0][1];
						int endRow = solutionMoves[curPiece][1][0];
						int endCol = solutionMoves[curPiece][1][1];
						
						board[startRow][startCol] = 0;
						board[(startRow + endRow) / 2][(startCol + endCol) / 2] = 0;
						board[endRow][endCol] = 1;
					}
					
					// Move to next step
					curStep++;
				}
			}
			
			// Update paint
			paintImmediately(0, 0, (int) PANEL_SIZE.getWidth(), (int) PANEL_SIZE.getHeight());
		}
	}

	/**
	 * Repaint the board's drawing panel
	 * @param g The Graphics context
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if (animating == true) // Animating solution
		{
			// Draw board
			if (boardType == 0) // Cross Board
				g.drawImage(crossBoardImage, 10, 10, this);
			else if (boardType == 1) // Octagon Board
				g.drawImage(octagonBoardImage, 10, 10, this);
			else if (boardType == 2) // Triangle Board
				g.drawImage(triangleBoardImage, 10, 10, this);

			if (boardType == 0 || boardType == 1) // Cross or Octagon Board
			{
				// Output board
				for (int row = 0; row < 7; row++)
				{
					for (int col = 0; col < 7; col++)
					{
						if (board[row][col] == 1) // Piece
							g.drawImage(pieceImage, 25 + col * 50, 25 + row * 50, this);
					}
				}

				// Highlight the current piece
				int startRow = solutionMoves[curPiece][0][0];
				int startCol = solutionMoves[curPiece][0][1];
				int endRow = solutionMoves[curPiece][1][0];
				int endCol = solutionMoves[curPiece][1][1];
				
				if (curStep == 1)
					g.drawImage(highlightImage, 25 + startCol * 50, 25 + startRow * 50, this);
				else if (curStep == 2)
					g.drawImage(highlightImage, 25 + endCol * 50, 25 + endRow * 50, this);
			}
			else // Triangular Board
			{
				// Output board
				for (int row = 0; row < 5; row++)
				{
					int startX = (250 - 50 * (row + 1)) / 2;
					for (int col = 0; col < 5; col++)
					{
						if (board[row][col] == 1) // Piece
							g.drawImage(pieceImage, 25 + 50 + startX + col * 50, 25 + 50 + row * 50, this);
					}
				}

				// Highlight the current piece
				int startRow = solutionMoves[curPiece][0][0];
				int startCol = solutionMoves[curPiece][0][1];
				int endRow = solutionMoves[curPiece][1][0];
				int endCol = solutionMoves[curPiece][1][1];
				
				if (curStep == 1)
				{
					int startX = (250 - 50 * (startRow + 1)) / 2;
					g.drawImage(highlightImage, 25 + 50 + startX + startCol * 50, 25 + 50 + startRow * 50, this);
				}
				else if (curStep == 2)
				{
					int startX = (250 - 50 * (endRow + 1)) / 2;
					g.drawImage(highlightImage, 25 + 50 + startX + endCol * 50, 25 + 50 + endRow * 50, this);
				}
			}
		}
		else // Not animating solution
		{
			//Draw board
			if (boardType == 0) // Cross Board
				g.drawImage(crossBoardImage, 10, 10, this); 
			else if (boardType == 1) // Octagon Board
				g.drawImage(octagonBoardImage, 10, 10, this);
			else if (boardType == 2) // Triangle Board
				g.drawImage(triangleBoardImage, 10, 10, this);
			
			// Draw pieces
			for (Piece next: pieceList)
				next.draw(g);
			// Draw the selected piece last (on top)
	        if (selectedPiece != null)
	        	selectedPiece.draw(g);
		}

	} // paint component method

	// Mouse Listener methods
	
	/**
	 * Responds to a mousePressed event
	 * @param event information about the mouse pressed event
	 */
	public void mousePressed(MouseEvent event)
	{		
		// De-select any of the choice lists in side panel
		// in order to prevent accidently starting a new game
		setFocusable(true);
		requestFocusInWindow();

		if (gameOver || waiting)
		{
			return;
		}
		
		Point selectedPoint = event.getPoint();
		// Check if we are selecting one of the pieces
		for (int i = 0; i < pieceList.size(); i++)
		{
			if (pieceList.get(i).contains(selectedPoint))
			{
				selectedPiece = pieceList.get(i);
				lastPoint = selectedPoint;
				return;
			}
         }
	}
	
	/**
	 * Responds to a mouseReleased event
	 * @param event information about the mouse released event
	 */
	public void mouseReleased(MouseEvent event)
	{
		if (gameOver || waiting)
		{
			return;
		}
		
		// If a piece was selected, release it
		if (selectedPiece != null)
        {
			int startRow = selectedPiece.getRow();
			int startCol = selectedPiece.getCol();
			int newRow = selectedPiece.getNewRow();
			int newCol = selectedPiece.getNewCol();
			
			if (newRow == -1 || newCol == -1) // If released board location is not valid (not not snap on to anything valid)
				selectedPiece.originalPos(); // Return piece to original location
			
			else // The dropped location is valid (can snap on to a new location)
			{
				// Check if the move was valid
				if (isValidMove(startRow, startCol, newRow, newCol))
				{
					numMoves++;
					// Change current piece
					board[startRow][startCol] = 0;
					board[newRow][newCol] = 1;
					selectedPiece.setPosition(newRow, newCol); // Set new location
					
					//Remove Piece
					board[(startRow + newRow) / 2][(startCol + newCol) / 2] = 0;
					int removedPieceIdx = -1;
					for (int i = 0; i < pieceList.size(); i++)
					{
						if (pieceList.get(i).getRow() == (startRow + newRow) / 2 && pieceList.get(i).getCol() == (startCol + newCol) / 2)
						{
							removedPieceIdx = i;
							break;
						}
					}
					removedPieceStack.push(pieceList.remove(removedPieceIdx));

					// Store information
					movesMade[numMoves][0] = startRow;
					movesMade[numMoves][1] = startCol;
					movesMade[numMoves][2] = newRow;
					movesMade[numMoves][3] = newCol;
					
					if (!hasMovesLeft())
					{
						gameOver = true;
						sidePanel.gameOver();
						repaint();
						JLabel label;
						if (pegsOnBoard() == 1) // One Peg Left
							label = new JLabel("You Win!", SwingConstants.CENTER);
						else // More than more peg remains
							label = new JLabel("You Lose!", SwingConstants.CENTER);
						
						label.setFont(new Font("Forte", Font.PLAIN, 24));
						JOptionPane.showMessageDialog(this, label, "Game Over", JOptionPane.PLAIN_MESSAGE);
						setCursor (Cursor.getDefaultCursor ());
					}
					else if (hintOn)
					{
						sidePanel.hintMessage(3);
						setCursor (Cursor.getDefaultCursor ());
						paintImmediately(0, 0, (int) PANEL_SIZE.getWidth(), (int) PANEL_SIZE.getHeight());
						sidePanel.hintMessage(hasSolution());
					}
				}
				else // Move was not Valid
					selectedPiece.originalPos(); // Return piece to original location
			}

			selectedPiece = null; // De-select piece
		}
		repaint();
	}

	// Un-used mouse listener methods
	
	public void mouseClicked(MouseEvent event)
	{
	}
	
	public void mouseEntered(MouseEvent event)
	{
	}

	public void mouseExited(MouseEvent event)
	{
	}

	// Mouse Motion Listener methods
	
	/**
	 * Responds to a mouseMoved event
	 * @param event information about the mouse moved event
	 */
	public void mouseMoved(MouseEvent event)
	{
		mouseMovePoint = event.getPoint ();
		if (gameOver || waiting)
		{
			return;
		}
		
		// Set the cursor to the hand if on piece
        for (Piece next: pieceList)
        {
            if (next.contains (mouseMovePoint))
            {
                setCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
                return;
            }
        }

        // Otherwise we just use the default cursor
        setCursor (Cursor.getDefaultCursor ());
	}
	
	/**
	 * Responds to a mouseDragged event
	 * @param event information about the mouse dragged event
	 */
	public void mouseDragged(MouseEvent event)
	{
		mouseMovePoint = event.getPoint ();
		
		if (gameOver || waiting)
		{
			return;
		}

		Point currentPoint = event.getPoint();
		// If a piece is being selected
		if (selectedPiece != null)
		{
			selectedPiece.move(lastPoint, currentPoint);
			lastPoint = currentPoint;
			repaint();
		}
	}

	/**
	 * Finds the number of pegs on the peg solitaire board
	 * @return the number of pegs left on the board
	 */
	private int pegsOnBoard()
	{
		int count = 0;
		// Loops through each position of the board and add to the count of pegs
		for (int j = 0; j <= 6; j++)
		{
			for (int k = 0; k <= 6; k++)
				if (board[j][k] == 1)
					count++;
		}
		return count;
	}

	/**
	 * Assigns a number to each of the holes in the board
	 * @param board the board that needs to be assigned numbers to
	 */
	public void setHoleNumber(int[][] board)
	{
		int holeCount = 0;
		for (int i = 0; i < 7; i++)
		{
			for (int i2 = 0; i2 < 7; i2++)
			{
				// Loops through the board to search for a hole in the board and assigns it a number, starting from 1
				if (board[i][i2] == 1 || board[i][i2] == 0)
				{
					holeNumber[i][i2] = holeCount;
					holeCount++;
				}
			}
		}
	}

	/**
	 * Converts a board to an integer representation
	 * @return an integer representation of the board, which is based on which holes have a peg and which don't
	 */
	private long boardToInt()
	{
		long boardValue = 0;
		for (int i = 0; i <= 6; i++)
		{
			for (int i2 = 0; i2 <= 6; i2++)
			{
				if (board[i][i2] == 1)
				{
					// Each hole on the board represents a digit for a binary number, the 1's place represents the first
					// hole, the 2's place represents the second hole, the 4's place represents the third hole and so
					// on. If there's a peg, then the digit it 1, if there isn't a peg the digit is 2, and the integer
					// representation of the board is the decimal value of this binary number.
					boardValue += Math.pow(2, holeNumber[i][i2]);
				}
			}
		}
		return boardValue;
	}

	/**
	 * Checks to see if the player has any possible moves left
	 * @return true if there are possible moves left, false otherwise
	 */
	private boolean hasMovesLeft()
	{
		int[][] modify = new int[6][2];
		modify[0][0] = 0;
		modify[0][1] = 2;
		modify[1][0] = 0;
		modify[1][1] = -2;
		modify[2][0] = 2;
		modify[2][1] = 0;
		modify[3][0] = -2;
		modify[3][1] = 0;
		modify[4][0] = 2;
		modify[4][1] = 2;
		modify[5][0] = -2;
		modify[5][1] = -2;
		int upTo = 4;
		if (boardType == 2)
			upTo = 6;
		// Loops and checks each position with a peg and check if it's possible for that peg to jump
		for (int row = 0; row <= 6; row++)
		{
			for (int col = 0; col <= 6; col++)
			{
				if (board[row][col] == 1)
				{
					for (int i = 0; i < upTo; i++)
					{
						if (row + modify[i][0] >= 0 && row + modify[i][0] < 7 && col + modify[i][1] >= 0
								&& col + modify[i][1] < 7)
						{
							if (isValidMove(row, col, row + modify[i][0], col + modify[i][1]))
								return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the board has any solution (one peg)
	 * @return true if the board has a solution, false otherwise
	 */
	public int hasSolution()
	{
		waiting = true;
		sidePanel.waiting();
		setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR));
		
		// If the game is the solitaire cross board, there's always a solution in the first three moves
		if (boardType == 0 && boardSetUp == 0 && pegsOnBoard() >= 29)
			minPegs = 1;
		else
		{
			// Initialize variables, including starting the "timer" and setting the hole numbers
			minBoard = new HashSet<Long>();
			minPegs = 99;
			startTime = System.nanoTime();
			setHoleNumber(board);
			
			// Finds a solution
			if (boardType == 2)
				findSolutionTri(0);
			else
				findSolution(0);
		}
		
		tDoneWaiting.start();
		
		// If there is a solution to 1 peg
		if (minPegs == 1)
			return 1;
		// If the solution is unknown (takes too long to run)
		else if (minPegs == -1)
			return 2;
		// Otherwise, a minimum number of pegs from this point
		else
			return 0;
	}

	/**
	 * Finds a solution for a non-triangular board (the minimum number of pegs remaining on the board)
	 * @param move the current move number of the "solution"
	 */
	private void findSolution(int move)
	{
		long temp = boardToInt();
		// If the method's been running to long, solution is "unknown"
		if ((System.nanoTime() - startTime) / 1000000000.0 > waitTime)
		{
			minPegs = -1;
			return;
		}
		// If the current position of the board hasn't be reached
		if (!(minBoard.contains(temp)) && minPegs > 1)
		{
			// Add the integer representation of this board to the set of boards visited
			minBoard.add(temp);
			// If there are only two pegs remaining, make it so that the last peg is in the middle spot
			if (pegsOnBoard() == 2)
			{
				if (board[3][3] == 0)
				{
					if (board[1][3] == 1 && board[2][3] == 1)
					{
						board[1][3] = 0;
						board[2][3] = 0;
						board[3][3] = 1;
						currentMoves[move][0][0] = 1;
						currentMoves[move][0][1] = 3;
						currentMoves[move][1][0] = 3;
						currentMoves[move][1][1] = 3;
						findSolution(move + 1);
						board[1][3] = 1;
						board[2][3] = 1;
						board[3][3] = 0;
					}
					else if (board[5][3] == 1 && board[4][3] == 1)
					{
						board[5][3] = 0;
						board[4][3] = 0;
						board[3][3] = 1;
						currentMoves[move][0][0] = 5;
						currentMoves[move][0][1] = 3;
						currentMoves[move][1][0] = 3;
						currentMoves[move][1][1] = 3;
						findSolution(move + 1);
						board[5][3] = 1;
						board[4][3] = 1;
						board[3][3] = 0;
					}
					else if (board[3][1] == 1 && board[3][2] == 1)
					{
						board[3][1] = 0;
						board[3][2] = 0;
						board[3][3] = 1;
						currentMoves[move][0][0] = 3;
						currentMoves[move][0][1] = 1;
						currentMoves[move][1][0] = 3;
						currentMoves[move][1][1] = 3;
						findSolution(move + 1);
						board[3][1] = 1;
						board[3][2] = 1;
						board[3][3] = 0;
					}
					else if (board[3][5] == 1 && board[3][4] == 1)
					{
						board[3][5] = 0;
						board[3][4] = 0;
						board[3][3] = 1;
						currentMoves[move][0][0] = 3;
						currentMoves[move][0][1] = 5;
						currentMoves[move][1][0] = 3;
						currentMoves[move][1][1] = 3;
						findSolution(move + 1);
						board[3][5] = 1;
						board[3][4] = 1;
						board[3][3] = 0;
					}
				}
			}
			for (int curx = 0; curx <= 6; curx++)
			{
				for (int cury = 0; cury <= 6; cury++)
				{
					// Loop through the board positions for where there's a peg, and at this location, and use
					// depth-first search through all possible moves
					if (board[curx][cury] == 1)
					{

						// Checks if the peg can move left
						if (cury - 2 >= 0 && board[curx][cury - 1] == 1 && board[curx][cury - 2] == 0
								&& board[curx][cury - 2] != 2)
						{
							// Change the board values
							board[curx][cury] = 0;
							board[curx][cury - 1] = 0;
							board[curx][cury - 2] = 1;
							if (minPegs != 1)
							{
								currentMoves[move][0][0] = curx;
								currentMoves[move][0][1] = cury;
								currentMoves[move][1][0] = curx;
								currentMoves[move][1][1] = cury - 2;
							}
							findSolution(move + 1);
							board[curx][cury] = 1;
							board[curx][cury - 1] = 1;
							board[curx][cury - 2] = 0;
						}
						// Checks if the peg can move downwards
						if (curx + 2 <= 6 && board[curx + 1][cury] == 1 && board[curx + 2][cury] == 0
								&& board[curx + 2][cury] != 2)
						{
							board[curx][cury] = 0;
							board[curx + 1][cury] = 0;
							board[curx + 2][cury] = 1;
							if (minPegs != 1)
							{
								currentMoves[move][0][0] = curx;
								currentMoves[move][0][1] = cury;
								currentMoves[move][1][0] = curx + 2;
								currentMoves[move][1][1] = cury;
							}
							findSolution(move + 1);
							board[curx][cury] = 1;
							board[curx + 1][cury] = 1;
							board[curx + 2][cury] = 0;
						}
						// Checks if the peg can move upwards
						if (curx - 2 >= 0 && board[curx - 1][cury] == 1 && board[curx - 2][cury] == 0
								&& board[curx - 2][cury] != 2)
						{
							board[curx][cury] = 0;
							board[curx - 1][cury] = 0;
							board[curx - 2][cury] = 1;
							if (minPegs != 1)
							{
								currentMoves[move][0][0] = curx;
								currentMoves[move][0][1] = cury;
								currentMoves[move][1][0] = curx - 2;
								currentMoves[move][1][1] = cury;
							}
							findSolution(move + 1);
							board[curx][cury] = 1;
							board[curx - 1][cury] = 1;
							board[curx - 2][cury] = 0;
						}
						// Checks if the peg can move right
						if (cury + 2 <= 6 && board[curx][cury + 1] == 1 && board[curx][cury + 2] == 0
								&& board[curx][cury + 2] != 2)
						{
							board[curx][cury] = 0;
							board[curx][cury + 1] = 0;
							board[curx][cury + 2] = 1;
							if (minPegs != 1)
							{
								currentMoves[move][0][0] = curx;
								currentMoves[move][0][1] = cury;
								currentMoves[move][1][0] = curx;
								currentMoves[move][1][1] = cury + 2;
							}
							findSolution(move + 1);
							board[curx][cury] = 1;
							board[curx][cury + 1] = 1;
							board[curx][cury + 2] = 0;
						}
					}
				}
			}
			// Checks the number of pegs on the board and if it's less than the overall minimum, set the overall minimum
			// to the current
			int tmp = pegsOnBoard();
			if (tmp < minPegs)
			{
				minPegs = tmp;
				// Updating the moves it takes to reach this current minimum
				for (int i = 0; i < move; i++)
				{
					solutionMoves[i][0][0] = currentMoves[i][0][0];
					solutionMoves[i][0][1] = currentMoves[i][0][1];
					solutionMoves[i][1][0] = currentMoves[i][1][0];
					solutionMoves[i][1][1] = currentMoves[i][1][1];
				}
			}
		}
	}

	/**
	 * Finds a solution for a triangular board (the minimum number of pegs remaining on the board)
	 * @param move the current move number of the "solution"
	 */
	private void findSolutionTri(int move)
	{
		long temp = boardToInt();
		// If the method's been running to long, solution is "unknown"
		if ((System.nanoTime() - startTime) / 1000000000.0 > 3)
		{
			minPegs = -1;
			return;
		}
		// If the current position of the board hasn't be reached
		if (!(minBoard.contains(temp)) && minPegs > 1)
		{
			// Add the integer representation of this board to the set of boards visited
			minBoard.add(temp);
			for (int curx = 0; curx < 5; curx++)
			{
				for (int cury = 0; cury <= curx; cury++)
				{
					// Loop through the board positions for where there's a peg, and at this location, and use
					// depth-first search through all possible moves
					if (board[curx][cury] == 1)
					{
						// Checks if the peg can move left
						if (cury - 2 >= 0 && board[curx][cury - 1] == 1 && board[curx][cury - 2] == 0
								&& board[curx][cury - 2] != 2)
						{
							board[curx][cury] = 0;
							board[curx][cury - 1] = 0;
							board[curx][cury - 2] = 1;
							if (minPegs != 1)
							{
								currentMoves[move][0][0] = curx;
								currentMoves[move][0][1] = cury;
								currentMoves[move][1][0] = curx;
								currentMoves[move][1][1] = cury - 2;
							}
							findSolutionTri(move + 1);
							board[curx][cury] = 1;
							board[curx][cury - 1] = 1;
							board[curx][cury - 2] = 0;
						}
						// Checks if the peg can move right
						if (cury + 2 <= 5 && board[curx][cury + 1] == 1 && board[curx][cury + 2] == 0
								&& board[curx][cury + 2] != 2)
						{
							board[curx][cury] = 0;
							board[curx][cury + 1] = 0;
							board[curx][cury + 2] = 1;
							if (minPegs != 1)
							{
								currentMoves[move][0][0] = curx;
								currentMoves[move][0][1] = cury;
								currentMoves[move][1][0] = curx;
								currentMoves[move][1][1] = cury + 2;
							}
							findSolutionTri(move + 1);
							board[curx][cury] = 1;
							board[curx][cury + 1] = 1;
							board[curx][cury + 2] = 0;
						}
						// Checks if the peg can move diagonally up and left
						if (curx - 2 >= 0 && board[curx - 1][cury] == 1 && board[curx - 2][cury] == 0
								&& board[curx - 2][cury] != 2)
						{
							board[curx][cury] = 0;
							board[curx - 1][cury] = 0;
							board[curx - 2][cury] = 1;
							if (minPegs != 1)
							{
								currentMoves[move][0][0] = curx;
								currentMoves[move][0][1] = cury;
								currentMoves[move][1][0] = curx - 2;
								currentMoves[move][1][1] = cury;
							}
							findSolutionTri(move + 1);
							board[curx][cury] = 1;
							board[curx - 1][cury] = 1;
							board[curx - 2][cury] = 0;
						}
						// Checks if the peg can move diagonally down and right
						if (curx + 2 <= 5 && board[curx + 1][cury] == 1 && board[curx + 2][cury] == 0
								&& board[curx + 2][cury] != 2)
						{
							board[curx][cury] = 0;
							board[curx + 1][cury] = 0;
							board[curx + 2][cury] = 1;
							if (minPegs != 1)
							{
								currentMoves[move][0][0] = curx;
								currentMoves[move][0][1] = cury;
								currentMoves[move][1][0] = curx + 2;
								currentMoves[move][1][1] = cury;
							}
							findSolutionTri(move + 1);
							board[curx][cury] = 1;
							board[curx + 1][cury] = 1;
							board[curx + 2][cury] = 0;
						}
						// Checks if the peg can move diagonally up and right
						if (curx - 2 >= 0 && cury - 2 >= 0 && board[curx - 1][cury - 1] == 1
								&& board[curx - 2][cury - 2] == 0 && board[curx - 2][cury - 2] != 2)
						{
							board[curx][cury] = 0;
							board[curx - 1][cury - 1] = 0;
							board[curx - 2][cury - 2] = 1;
							if (minPegs != 1)
							{
								currentMoves[move][0][0] = curx;
								currentMoves[move][0][1] = cury;
								currentMoves[move][1][0] = curx - 2;
								currentMoves[move][1][1] = cury - 2;
							}
							findSolutionTri(move + 1);
							board[curx][cury] = 1;
							board[curx - 1][cury - 1] = 1;
							board[curx - 2][cury - 2] = 0;
						}
						// Checks if the peg can move diagonally down and left
						if (curx + 2 <= 5 && cury + 2 <= 5 && board[curx + 1][cury + 1] == 1
								&& board[curx + 2][cury + 2] == 0 && board[curx + 2][cury + 2] != 2)
						{
							board[curx][cury] = 0;
							board[curx + 1][cury + 1] = 0;
							board[curx + 2][cury + 2] = 1;
							if (minPegs != 1)
							{
								currentMoves[move][0][0] = curx;
								currentMoves[move][0][1] = cury;
								currentMoves[move][1][0] = curx + 2;
								currentMoves[move][1][1] = cury + 2;
							}
							findSolutionTri(move + 1);
							board[curx][cury] = 1;
							board[curx + 1][cury + 1] = 1;
							board[curx + 2][cury + 2] = 0;
						}
					}
				}
			}
			// Checks the number of pegs on the board and if it's less than the overall minimum, set the overall minimum
			// to the current
			int tmp = pegsOnBoard();
			if (tmp < minPegs)
			{
				minPegs = tmp;
				// Updating the moves it takes to reach this current minimum
				for (int i = 0; i < move; i++)
				{
					solutionMoves[i][0][0] = currentMoves[i][0][0];
					solutionMoves[i][0][1] = currentMoves[i][0][1];
					solutionMoves[i][1][0] = currentMoves[i][1][0];
					solutionMoves[i][1][1] = currentMoves[i][1][1];
				}
			}
		}
	}
}
