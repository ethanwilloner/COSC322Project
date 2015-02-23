package AbstractClasses;

import utils.Move;
import ai.OurBoard;

public abstract class GameSearch {
	
	/**
	 * Get next best move for side according to this search
	 * @param board game board
	 * @param side side of player
	 * @return the best move decided by search
	 */
	public abstract Move getMove(OurBoard board, int side);

}
