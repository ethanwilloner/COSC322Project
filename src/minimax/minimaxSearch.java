package minimax;

import utils.GameRules;
import utils.Move;
import utils.OurEvaluation;
import ai.OurBoard;

public class minimaxSearch 
{
	
	public minimaxNode minimax(OurBoard board, int depth, boolean maximizingPlayer, int side)
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
			for (Move m: GameRules.getLegalMoves(board, side)) 
			{
				board.makeMove(m);
				
//				System.out.println("Board after move:");
//				System.out.println(board);
				
				
				node = minimax(board, depth-1, false, (side==1)?2:1);
				val = node.value;
				
				if (bestValue < val)
				{
					bestValue = val;
					bestMove = m;
				}
				
				//undo move
				board.undoMove(m);
				
//				System.out.println("Board after undo:");
//				System.out.println(board);
				
			}
			return new minimaxNode(bestValue, bestMove);
			
		}
		else {
			bestValue = Integer.MAX_VALUE;
			for (Move m : GameRules.getLegalMoves(board, side)) 
			{
				board.makeMove(m);
				node = minimax(board, depth-1, false, (side==1)?2:1);
				
				val = node.value;
				
				if (bestValue > val)
				{
					bestValue = val;
					bestMove = m;
				}
				//undo move
				board.undoMove(m);
			}
			
			return new minimaxNode(bestValue, bestMove);
		}
		
	}
	
	private class minimaxNode
	{
		int value;
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "minimaxNode [value=" + value + ", move=" + move + "]";
		}
		Move move;
		public minimaxNode(int value, Move move) {
			super();
			this.value = value;
			this.move = move;
		}
	}

	
}
