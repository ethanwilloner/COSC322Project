package ai;

import java.util.HashSet;

import utils.GameRules;
import utils.Move;

import com.rits.cloning.Cloner;

/**
 * Board class that contains the instance of the board
 * 
 * @author Yarko Senyuta
 *
 */
public class OurBoard implements Cloneable{
	
	static Cloner clone = new Cloner();
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
	
	HashSet<OurPair> whitePositions;
	HashSet<OurPair> blackPositions;
	
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
		whitePositions = new HashSet<OurPair>();
		blackPositions = new HashSet<OurPair>();
		
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
		board[0][3] = BQUEEN;
		board[6][0] = BQUEEN;
		board[3][0] = BQUEEN;
		board[9][3] = BQUEEN;

		blackPositions.add(new OurPair(0,3));
		blackPositions.add(new OurPair(6,0));
		blackPositions.add(new OurPair(3,0));
		blackPositions.add(new OurPair(9,3));

		board[0][6] = WQUEEN;
		board[6][9] = WQUEEN;
		board[3][9] = WQUEEN;
		board[9][6] = WQUEEN;	

		whitePositions.add(new OurPair(0, 6));
		whitePositions.add(new OurPair(6, 9));
		whitePositions.add(new OurPair(3, 9));
		whitePositions.add(new OurPair(9, 6));
		
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
		
		HashSet<OurPair> positions;
		
		//System.out.println(blackPositions+"\n"+whitePositions);
		
		if (queenCode == WQUEEN)
			positions = whitePositions;
		else
			positions = blackPositions;
			
		for (OurPair p : positions){
			
			if (p.getX() == oldX && p.getY() == oldY){
				p.setX(newX);
				p.setY(newY);
				break;
				
			}	
		}
		if (queenCode == WQUEEN)
			whitePositions=positions;
		else
			blackPositions = positions;
	}
	
	/**
	 * return the ArrayList of black positions
	 * @return black positions
	 */
	public HashSet<OurPair > getBlackPositions(){
		return blackPositions;
	}
	
	/**
	 * return the ArrayList of white positions
	 * @return
	 */
	public HashSet<OurPair> getWhitePositions(){
		return whitePositions;
	}
	
	/**
	 * make given move to the board
	 * @param action move to be made
	 * @param side which side is making the move
	 * @return
	 */
	public boolean makeMove(Move action)
	{
		//get code of queen
		int side = board[action.getInitialQ().getX()][action.getInitialQ().getY()];
		
		
		//check if this move is legal
		if (GameRules.isLegalMove(this, action, side))
		{	
			//free old queen position
			placeMarker(action.getInitialQ().getX(), action.getInitialQ().getY(), FREE);
			
			//place new queen
			placeMarker(action.getFinalQ().getX(), action.getFinalQ().getY(), side);
			
			//place arrow
			placeMarker(action.getArrow().getX(), action.getArrow().getY(), ARROW);
			
			
			//update queen in hashset
			updateQueenPosition(action.getInitialQ().getX(), action.getInitialQ().getY(), action.getFinalQ().getX(), action.getFinalQ().getY(), side);
			return true;
		}
		
		System.out.println(action + " is illegal");
		
		throw new NullPointerException();
		
	}
	
	
	public void undoMove(Move move)
	{
		//get rid of arrow
		board[move.getArrow().getX()][move.getArrow().getY()] = FREE;
		
		//figure out side
		int side = board[move.getFinalQ().getX()][move.getFinalQ().getY()];
		
		
		//place old queen position
		placeMarker(move.getInitialQ().getX(), move.getInitialQ().getY(), side);
		
		//free new queen
		placeMarker(move.getFinalQ().getX(), move.getFinalQ().getY(), FREE);
		
		
		//update queen in hashset
		updateQueenPosition(move.getFinalQ().getX(), move.getFinalQ().getY(), move.getInitialQ().getX(), move.getInitialQ().getY(), side);
		
		
	}
	
	
	/**
	 * returns true if designated position is a queen
	 * @param x x value
	 * @param y y value
	 * @return true if queen is at the given position
	 */
	public boolean isQueen(int x, int y)
	{
		OurPair thisSpace = new OurPair(x, y);
		
		if (blackPositions.contains(thisSpace) || whitePositions.contains(thisSpace))
			return true;
		else 
			return false;
	}
	
	@Override
	public String toString()
	{
		String s = "";
		
		for (int j = 0; j < 10; j++)
		{
			s+="-----------------------------------------\n";
			s+="|";
			for (int i = 0; i<10; i++)
			{
				s += " " + board[i][j] + " |";
			}
			s+= "\n";
		}
		
		s+="-----------------------------------------\n";
		
		s = s.replaceAll("-1", " ");
		s = s.replaceAll("3", "X");
		
		
		return s;
		
	}
	
	public boolean cutoffTest(int depth, long startTime)
	{
		//  watch memory usage
		if (Runtime.getRuntime().freeMemory() <= 5000000)
		{
			return true;
		}
		
		// watch the time
		long time = System.currentTimeMillis() - startTime;
		if (time >= 25000)
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public OurBoard clone()
	{
		return clone.deepClone(this);
	}
}
