package ai.search;

import ai.OurBoard;


/**
 * Search agent
 * 
 * @author Mike Nowicki
 *
 */
public class Agent {

	public final int WQUEEN = 1;
	public final int BQUEEN = 2;
	public final int ARROW = 3;
	public final int FREE = -1;	 
	
	/**
	 * our board instance
	 */
	private OurBoard board;
	private SuccessorGenerator scg;

	private int rows;
	private int columns;
		
	/**
	 * a constructor for the Agent
	 * @param board an instance of the board
	 * @param rows number of rows on board
	 * @param columns number of columns on board
	 * @param ourColour the color of our side
	 */
	public Agent(OurBoard board, int rows, int columns, int ourColour){
		this.board = board;
		this.rows = rows;
		this.columns = columns;
		scg = new SuccessorGenerator(board, ourColour);
	}
	
	public String selectMove(){
		String move = "";
		
		// some search function call or something
		move = "a3-d3-d8";
		
		
		return move;
				
	}
	
}
