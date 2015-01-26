package ai;

import java.util.HashSet;

import utils.GameRules;
import utils.Move;

/**
 * Board class that contains the instance of the board
 * 
 * @author Yarko Senyuta
 *
 */
public class OurBoard {

	/**
	 * store board as a 2D array
	 */
	private int[][] board;
	/**
	 * number of rows on board
	 */
	private int rows;
	
	/**
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @param rows the rows to set
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * @return the columns
	 */
	public int getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(int columns) {
		this.columns = columns;
	}

	/**
	 * number of columns on the board
	 */
	private int columns;

	/**
	 * white queen code
	 */
	public final int WQUEEN = 1;
	/**
	 * black queen code
	 */
	public final int BQUEEN = 2;
	/**
	 * arrow code
	 */
	public final int ARROW = 3;
	/**
	 * free space code
	 */
	public final int FREE = -1;
	
	HashSet<OurPair<Integer, Integer> > whitePositions;
	HashSet<OurPair<Integer, Integer> > blackPositions;
	
	/**
	 * constructor to initialize our board
	 * @param rows the number of rows on the board
	 * @param columns the number of columns on the board
	 */
	public OurBoard() {
		//set rows and columns
		this.rows = 10;
		this.columns = 10;
		//instantiate 2D array
		board = new int[rows][columns];
		whitePositions = new HashSet<OurPair<Integer, Integer>>();
		blackPositions = new HashSet<OurPair<Integer, Integer>>();
		
		initialize();		
	}
	
	/**
	 * initialize instance of the board
	 */
	private void initialize(){
		
		//color the board free
		for (int i = 0; i < rows; i++){
			for (int j = 0; j < columns; j++){
				board[i][j] = FREE;
			}
		}
		
		//set initial positions of the queens
		board[0][3] = WQUEEN;
		board[0][6] = WQUEEN;
		board[3][0] = WQUEEN;
		board[3][9] = WQUEEN;

		whitePositions.add(new OurPair<Integer, Integer>(0,3));
		whitePositions.add(new OurPair<Integer, Integer>(0,6));
		whitePositions.add(new OurPair<Integer, Integer>(3,0));
		whitePositions.add(new OurPair<Integer, Integer>(3,9));

		board[6][0] = BQUEEN;
		board[6][9] = BQUEEN;
		board[9][3] = BQUEEN;
		board[9][6] = BQUEEN;	

		blackPositions.add(new OurPair<Integer, Integer>(6, 9));
		blackPositions.add(new OurPair<Integer, Integer>(9, 3));
		blackPositions.add(new OurPair<Integer, Integer>(6, 0));
		blackPositions.add(new OurPair<Integer, Integer>(9, 6));
		
	}
	/**
	 * free a square at pos (x, y)
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public void freeSquare(int x, int y){
		board[x][y] = FREE;
	}
	
	public boolean isFree(int x, int y){
		return (board[x][y] == FREE);
	}
	
	/**
	 * set a specific piece at a specific location
	 * @param x x-coordinate of location
	 * @param y y-coordinate of location
	 * @param piece piece to be placed
	 */
	public void placeMarker(int x, int y, int piece){
		board[x][y] = piece;
	}
	
	/**
	 * check if specific space is not free
	 * @param x x-coordinate of space
	 * @param y y-coordinate of space
	 * @return true if space is occupied, false if free
	 */
	public boolean isMarked(int x, int y){
		if (board[x][y] == FREE){
			return false;
		}
		return true;
	}
	
	/**
	 * get the code at a specific location
	 * @param x x-coordinate of space
	 * @param y y-coordinate of space
	 * @return the code of space at location
	 */
	public int getPiece(int x, int y){
		return board[x][y];
	}

	public void updateQueenPosition(int oldX, int oldY, int newX, int newY, int queenCode){
		
		HashSet<OurPair<Integer, Integer>> positions;
		
		if (queenCode == WQUEEN)
			positions = whitePositions;
		else
			positions = blackPositions;
			
		for (OurPair<Integer, Integer> p : positions){
			
			if (p.getLeft() == oldX && p.getRight() == oldY){
				p.setLeft(newX);
				p.setRight(newY);
				return;
			}	
		}
	}
	
	/**
	 * return the ArrayList of black positions
	 * @return black positions
	 */
	public HashSet<OurPair<Integer, Integer> > getBlackPositions(){
		return blackPositions;
	}
	
	/**
	 * return the ArrayList of white positions
	 * @return
	 */
	public HashSet<OurPair<Integer, Integer> > getWhitePositions(){
		return whitePositions;
	}
	
	/**
	 * make given move to the board
	 * @param move move to be made
	 * @param side which side is making the move
	 * @return
	 */
	public boolean makeMove(Move move)
	{
		//get code of queen
		int side = board[move.getInitialQ().getLeft()][move.getInitialQ().getRight()];
		
		
		//check if this move is legal
		if (GameRules.isLegalMove(this, move, side))
		{	
			//free old queen position
			placeMarker(move.getInitialQ().getLeft(), move.getInitialQ().getRight(), FREE);
			
			//place new queen
			placeMarker(move.getFinalQ().getLeft(), move.getFinalQ().getRight(), side);
			
			//place arrow
			placeMarker(move.getArrow().getLeft(), move.getArrow().getRight(), ARROW);
			
			
			//update queen in hashset
			updateQueenPosition(move.getInitialQ().getLeft(), move.getInitialQ().getRight(), move.getFinalQ().getLeft(), move.getFinalQ().getRight(), side);
			return true;
		}
		
		return false;
	}
	
	/**
	 * returns true if designated position is a queen
	 * @param x x value
	 * @param y y value
	 * @return true if queen is at the given position
	 */
	public boolean isQueen(int x, int y)
	{
		OurPair<Integer, Integer> thisSpace = new OurPair<Integer, Integer>(x, y);
		
		if (blackPositions.contains(thisSpace) || whitePositions.contains(thisSpace))
			return true;
		else 
			return false;
	}
}
