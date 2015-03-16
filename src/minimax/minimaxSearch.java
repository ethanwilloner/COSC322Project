package minimax;

import utils.GameRules;
import utils.Move;
import AbstractClasses.GameSearch;
import ai.OurBoard;


/**
 * a sequential minimax search
 * @author Yarko Senyuta
 *
 */
public class minimaxSearch  extends GameSearch
{
	
	private static long startTime;
	private static boolean isCutoff;
	
	

	public minimaxNode minimax(OurBoard board, int depth, int maxDepth, boolean maximizingPlayer, int side, int alpha, int beta)
	{
        if (board.cutoffTest(depth, startTime))
        {
            isCutoff = true;
            return new minimaxNode(eval.evaluateBoard(board, side), null);
        }

        //evaluation
		int evaluation = eval.evaluateBoard(board, side);
		
		//if we have run out of depth or one side has pretty much won
		if (depth > maxDepth || GameRules.checkEndGame(board) != 0/*eval[1] != 0*/)
		{
			return new minimaxNode(evaluation, null);
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
				
				node = minimax(board, depth+1, maxDepth, false, side /*(side==1)?2:1*/, alpha, beta);
				
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
					//System.out.println("pruned from max");
					break;
				}
				
				//check if we can keep looking
				//check if out of time
				if (board.cutoffTest(depth, startTime) == true)
				{
					isCutoff = true;
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
				node = minimax(board, depth+1, maxDepth, true, side /*(side==1)?2:1*/, alpha, beta);
				
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
					//System.out.println("pruned from min");
					break;
				}
				
				//check if we can keep looking
				//check if out of time
				if (board.cutoffTest(depth, startTime) == true)
				{
					isCutoff = true;
					break;
				}
			}
			
			return new minimaxNode(bestValue, bestMove);
		}
		
	}

	@Override
	public Move getMove(OurBoard board, int side) {
		
		//use iterative deepening
		int depth = 1;
		
		startTime = System.currentTimeMillis();
		isCutoff = false;
		
		Move bestMoveSoFar = null;

		int bestValSoFar = Integer.MIN_VALUE;
		
		//while we still have time, do iterative deepening
		while (!isCutoff)
		{
			System.out.println("Scanning depth: " + depth);
			minimaxNode node = minimax(board, 1, depth, true, side, Integer.MIN_VALUE, Integer.MAX_VALUE);
			//update best so far only if we aren't cut off or we don't have any best so far
			if (!isCutoff || bestMoveSoFar == null)
			{
				if (bestValSoFar < node.getValue())
				{
					bestMoveSoFar = node.getMove();
					bestValSoFar = node.getValue();
				}
			}
			depth++;
		}
		System.out.println("Got to depth " + (depth-1) + " in sequential search");
		
		return bestMoveSoFar;
		
	}

	
}
