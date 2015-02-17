package utils;

import java.util.HashSet;

import ai.OurBoard;
import ai.OurPair;

public class OurEvaluation 
{
	
	/**evaluation function that computes a value for whose in a better spot;
	 * a negative value places black ahead;
	 * a positive value places white ahead;
	 * a negative max is a black win;
	 * a positive max is a white win;
	 * 
	 * @param board the board evaluated
	 * @return
	 */
	public static int[] evaluateBoard(OurBoard board, int side)
	{
		HashSet<OurPair> whitePositions = board.getWhitePositions();
		HashSet<OurPair> blackPositions = board.getBlackPositions();
	
		
		//mark up this board with minimum moves to each tile for black and for white (represented in the last dimension)
		//USE STATIC VAR SO DON"T HAVE TO ALLOCATE MEM DURING RUN
		int[][][] tempBoard = new int[10][10][2];
		
		
		//keep track if we need to check for end of game
		boolean isEnd = true;
		
		//fill board with max values
		for (int x = 0; x < 10; x++)
		{
			for (int y = 0; y < 10; y++)
			{
				tempBoard[x][y][0] = Integer.MAX_VALUE;
				tempBoard[x][y][1] = Integer.MAX_VALUE;
				
			}
		}
		
		//iterative deepening
		int iterativeDepth = 1;
		
		//evaluate board for each queen (white)
		//while all queens haven't explored the places they can
		
		boolean keepGoing = true;
		int canKeepGoing = 0;
		
		while (keepGoing)
		{
			canKeepGoing = 0;
			for (OurPair queen : whitePositions)
			{
				tempBoard[queen.getX()][queen.getY()][0] = 0;
				tempBoard[queen.getX()][queen.getY()][1] = 0;
				
				if (paintBoardWithQueen(board, tempBoard, queen, 1, 0, iterativeDepth))
					canKeepGoing++;
			}
			
			//can keep exploring?
			keepGoing = !(canKeepGoing == 0);
			//increase depth
			iterativeDepth++;
		}
		
		keepGoing = true;
		iterativeDepth = 1;
		
		//evaluate board for each queen (black)
		while (keepGoing)
		{
			canKeepGoing = 0;
			for (OurPair queen : blackPositions)
			{
				tempBoard[queen.getX()][queen.getY()][1] = 0;
				tempBoard[queen.getX()][queen.getY()][0] = 0;
				
				if (paintBoardWithQueen(board, tempBoard, queen, 2, 0, iterativeDepth))
					canKeepGoing++;
			}
			
			//can keep exploring?
			keepGoing = !(canKeepGoing == 0);
			//increase depth
			iterativeDepth++;
			
		}
		
		
		int white = 0;
		int black = 0;
		
		int whiteOnly = 0;
		int blackOnly = 0;
		
		int whiteTemp = 0;
		int blackTemp = 0;
		
		for (int x = 0; x < 10; x++)
		{
			for (int y = 0; y < 10; y++)
			{
				whiteTemp = tempBoard[x][y][0];
				blackTemp = tempBoard[x][y][1];
				
				//is this space not free? or if both black and white can reach in same number of moves,
				if (!board.isFree(x,  y) || whiteTemp == blackTemp)
					continue;
				
				
				//if white's value is less than black's or blacks never reached this tile, 
				if (whiteTemp < blackTemp || blackTemp == Integer.MAX_VALUE)
				{
					white++;
					
					//if black cannot reach this tile, it's whiteOnly
					if (blackTemp == Integer.MAX_VALUE && whiteTemp != Integer.MAX_VALUE)
					{
						whiteOnly++;
					}
				}
				//if blacks got there quicker or white never got there
				else if (blackTemp < whiteTemp || whiteTemp == Integer.MAX_VALUE)
				{
					black++;
					
					//if white cannot reach this tile, it's ghetto
					if (whiteTemp == Integer.MAX_VALUE && blackTemp != Integer.MAX_VALUE)
					{
						blackOnly++;
					}
				}
			}
		}
		
		
		//what we return
		int[] rtn = new int[2];
		
		//if an end-game is reached
		if (blackOnly == black && whiteOnly == white)
		{
			//black wins
			if (black > white)
				rtn[1] = Integer.MIN_VALUE;
			//white wins
			else
				rtn[1] = Integer.MAX_VALUE;
		}
	
		
		//general evaluation
		rtn[0] = white - black;
		
		//if we're playing as black
		if (side == 2)
		{
			rtn[0] = -rtn[0];
			rtn[1] = -rtn[1];
		}
		
		
		
		
		return rtn;
	}
	
	private static boolean paintBoardWithQueen(OurBoard board, int[][][] tempBoard, OurPair queen, int side, int depth, int maxDepth)
	{
		//get positions the queen can move now
		HashSet<OurPair> moves = GameRules.getLegalQueenMoves(board, queen, side);
		
		boolean rtn = false;
		//for every move available to queen, see how expensive it is
		for (OurPair move : moves)
		{
			//prune search
			//can we improve this locale?
			if (tempBoard[move.getX()][move.getY()][side-1] > depth+1)
			{
				tempBoard[move.getX()][move.getY()][side-1] = depth+1;
				
				if (depth < maxDepth)
				{
					rtn = paintBoardWithQueen(board, tempBoard, move, side, depth+1, maxDepth);
				}
				//we could make a better move but cut by depth
				else
					rtn = true;
			}
		}
		
		return rtn;
	}

}
