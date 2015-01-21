package ai;

import java.util.ArrayList;

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
	
	ArrayList<OurPair<Integer, Integer> > whitePositions;
	ArrayList<OurPair<Integer, Integer> > blackPositions;
	
	/**
	 * constructor to initialize our board
	 * @param rows the number of rows on the board
	 * @param columns the number of columns on the board
	 */
	public OurBoard(int rows, int columns) {
		//set rows and columns
		this.rows = rows;
		this.columns = columns;
		//instantiate 2D array
		board = new int[rows][columns];
		whitePositions = new ArrayList<>();
		blackPositions = new ArrayList<>();
		
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
		
		ArrayList<OurPair<Integer, Integer>> positions;
		
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
	public ArrayList<OurPair<Integer, Integer> > getBlackPositions(){
		return blackPositions;
	}
	
	/**
	 * return the ArrayList of white positions
	 * @return
	 */
	public ArrayList<OurPair<Integer, Integer> > getWhitePositions(){
		return whitePositions;
	}
}
