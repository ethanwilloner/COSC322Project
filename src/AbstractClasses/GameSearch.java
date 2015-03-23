package AbstractClasses;

import AmazonBoard.GameMove;
import AmazonBoard.IllegalMoveException;
import AmazonBoard.GameBoard;

public abstract class GameSearch {
	
	protected Evaluation eval;
	
	/**
	 * Get next best move for side according to this search
	 * @param board game board
	 * @param side side of player
	 * @return the best move decided by search
	 */
	public abstract GameMove getMove(GameBoard board, int side) throws IllegalMoveException;

	public void setEvaluation(Evaluation e)
	{
		eval = e;
	}

}
