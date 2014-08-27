/**
 * The Piece Class
 * Keeps track a Piece's location on the board for a game of Peg Solitaire
 * @author Alvin Qiu and Bryan Qiu
 * @version January 2012
 */

import java.awt.*;
import javax.swing.ImageIcon;

public class Piece
{
	private Point centre;
	private int row, col, boardType, newCol;
	private PegSolitaireBoard gamePanel;
	private final int RADIUS = 20;
	private Image pieceImage = new ImageIcon("Images\\Piece.gif").getImage();

	/**
	 * Create a Piece object with given row, column, board type and PegSolitaireBoard
	 * @param row the row of the piece
	 * @param col the column of the piece
	 * @param boardType the type of board
	 * @param gameBoard the board the piece in on
	 */
	public Piece(int row, int col, int boardType, PegSolitaireBoard gameBoard)
	{
		this.row = row;
		this.col = col;
		this.boardType = boardType;
		this.gamePanel = gameBoard;
		// Find centre of Piece depending on boardType
		if (this.boardType == 0 || boardType == 1) // Cross or Octagon
			centre = new Point(25 + col * 50 + 24, 25 + row * 50 + 24);
		else if (this.boardType == 2) // Triangle
		{
			int startX = (250 - 50 * (row + 1)) / 2;
			centre = new Point(25 + 50 + startX + col * 50 + 24, 25 + 50 + row * 50 + 24);
		}
	}

	/**
	 * Move the position of a Piece from a given point to another given point
	 * @param from the position to move from
	 * @param to the position to move to
	 */
	public void move(Point from, Point to)
	{
		centre = new Point(centre.x - from.x + to.x, centre.y - from.y + to.y);
	}

	/**
	 * Check if a given Point is contained within a Piece
	 * @param p the point to check
	 * @return true if the point is within a piece false otherwise
	 */
	public boolean contains(Point p)
	{
		int distance = (int) Math.sqrt(Math.pow(p.x - centre.x, 2) + Math.pow(p.y - centre.y, 2));
		return distance <= RADIUS;
	}

	/**
	 * Gets the row of a Piece
	 * @return the row of this Piece
	 */
	public int getRow()
	{
		return row;
	}

	/**
	 * Gets the column of a Piece
	 * @return the column of this Piece
	 */
	public int getCol()
	{
		return col;
	}

	/**
	 * Gets the new proposed row of a Piece depending on where its been moved
	 * @return the new column of this Piece (-1: if not a valid location to release piece)
	 */
	public int getNewRow()
	{
		if (this.boardType == 0 || boardType == 1) // Cross or Octagon
		{
			for (int i = 0; i < 7; i++)
			{
				for (int j = 0; j < 7; j++)
				{
					// Check if the centre of this Piece is close enough to snap
					// on to a board location (within 20 pixels)
					int checkX = 25 + j * 50 + 24;
					int checkY = 25 + i * 50 + 24;
					int distance = (int) Math.sqrt(Math.pow(checkX - centre.x, 2) + Math.pow(checkY - centre.y, 2));
					if (distance <= 20)
					{
						newCol = j;
						return i;
					}	
				}
			}
		}
		else if (this.boardType == 2) // Triangle
		{
			for (int i = 0; i < 5; i++)
			{
				for (int j = 0; j < 5; j++)
				{
					// Check if the centre of this Piece is close enough to snap
					// on to a board location (within 10 pixels)
					int startX = (250 - 50 * (i + 1)) / 2;
					int checkX = 25 + 50 + startX + j * 50 + 24;
					int checkY = 25 + 50 + i * 50 + 24;
					int distance = (int) Math.sqrt(Math.pow(checkX - centre.x, 2) + Math.pow(checkY - centre.y, 2));
					if (distance <= 10)
					{
						newCol = j;
						return i;
					}	
				}
			}
		}
		// No valid board spot found
		newCol = -1;
		return -1;
	}

	/**
	 * Gets the new proposed column of a Piece depending on where its been moved
	 * @return the new column of this Piece (-1: if not a valid location to release piece)
	 */
	public int getNewCol()
	{
		// newCol is always already calculated in getNewRow()
		return newCol;
	}

	/**
	 * Set the position of the Piece back to the its original positions before being moved
	 */
	public void originalPos()
	{
		if (this.boardType == 0 || boardType == 1) // Cross or Octagon
			centre = new Point(25 + col * 50 + 24, 25 + row * 50 + 24);
		else if (this.boardType == 2) // Triangle
		{
			int startX = (250 - 50 * (row + 1)) / 2;
			centre = new Point(25 + 50 + startX + col * 50 + 24, 25 + 50 + row * 50 + 24);
		}
	}

	/**
	 * Set the position of a piece in its new location on the board with given row and column
	 * @param row the row of the piece
	 * @param col the column of the piece
	 */
	public void setPosition(int row, int col)
	{
		this.row = row;
		this.col = col;
		if (this.boardType == 0 || boardType == 1) // Cross or Octagon
			centre = new Point(25 + col * 50 + 24, 25 + row * 50 + 24);
		else if (this.boardType == 2) // Triangle
		{
			int startX = (250 - 50 * (row + 1)) / 2;
			centre = new Point(25 + 50 + startX + col * 50 + 24, 25 + 50 + row * 50 + 24);
		}
	}

	/**
	 * Draw the given piece
	 * @param g
	 */
	public void draw(Graphics g)
	{
		g.drawImage(pieceImage, centre.x - 24, centre.y - 24, gamePanel);
	}
}
