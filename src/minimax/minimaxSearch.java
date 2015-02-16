package minimax;

import utils.GameRules;
import utils.Move;
import utils.OurEvaluation;
import ai.OurBoard;

public class minimaxSearch 
{
	
	public minimaxNode minimax(OurBoard board, int depth, boolean maximizingPlayer, int side, int alpha, int beta)
	{
		//evaluation
		int[] eval = OurEvaluation.evaluateBoard(board, side);
		
		
		
		//if we have run out of depth or one side has pretty much won
		if (depth == 0 || eval[1] != 0)
		{
			return new minimaxNode(eval[0], null);
		}
		
		int bestValue;
		Move bestMove = null;
		int val;
		minimaxNode node;
		
		if (maximizingPlayer) 
		{
			bestValue = Integer.MIN_VALUE;
			//maximizing our moves
			for (Move m: GameRules.getLegalMoves(board, side)) 
			{
				board.makeMove(m);
				
				node = minimax(board, depth-1, false, (side==1)?2:1, alpha, beta);
				
				val = node.value;
				
				if (bestValue < val)
				{
					bestValue = val;
					bestMove = m;
				}
				
				//undo move
				board.undoMove(m);
				
				//alpha check
				alpha = Math.max(alpha, bestValue);
				
				if (beta <= alpha)
				{
					System.out.println("pruned from max");
					break;
				}
				
			}
			
			return new minimaxNode(bestValue, bestMove);
			
		}
		else {
			bestValue = Integer.MAX_VALUE;
			//minimizing other player's moves
			for (Move m : GameRules.getLegalMoves(board, side)) 
			{
				board.makeMove(m);
				node = minimax(board, depth-1, true, (side==1)?2:1, alpha, beta);
				
				val = node.value;
				
				if (bestValue > val)
				{
					bestValue = val;
					bestMove = m;
				}
				//undo move
				board.undoMove(m);
				
				//alpha beta check
				beta = Math.min(beta, bestValue);
				
				if (beta <= alpha)
				{
					System.out.println("pruned from min");
					break;
				}
			}
			
			return new minimaxNode(bestValue, bestMove);
		}
		
	}
	
	public class minimaxNode
	{
		int value;
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "minimaxNode [value=" + value + ", move=" + move + "]";
		}
		/**
		 * @return the value
		 */
		public int getValue() {
			return value;
		}
		/**
		 * @param value the value to set
		 */
		public void setValue(int value) {
			this.value = value;
		}
		/**
		 * @return the move
		 */
		public Move getMove() {
			return move;
		}
		/**
		 * @param move the move to set
		 */
		public void setMove(Move move) {
			this.move = move;
		}
		Move move;
		public minimaxNode(int value, Move move) {
			super();
			this.value = value;
			this.move = move;
		}
	}

	
}
