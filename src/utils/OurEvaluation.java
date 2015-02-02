package utils;

import java.util.HashSet;

import ai.OurBoard;
import ai.OurPair;

public class OurEvaluation 
{
	
	public static int evaluateBoard(OurBoard board)
	{
		HashSet<OurPair<Integer, Integer>> whitePositions = board.getWhitePositions();
		HashSet<OurPair<Integer, Integer>> blackPositions = board.getBlackPositions();
		
		//mark up this board with minimum moves to each tile for black and for white (represented in the last dimension)
		int[][][] tempBoard = new int[10][10][2];
		
		//fill board with max values
		for (int x = 0; x < 10; x++)
		{
			for (int y = 0; y < 10; y++)
			{
				tempBoard[x][y][0] = Integer.MAX_VALUE;
				tempBoard[x][y][1] = Integer.MAX_VALUE;
				
			}
		}
		//evaluate board for each queen (white)
		for (OurPair<Integer, Integer> queen : whitePositions)
		{
			tempBoard[queen.getX()][queen.getY()][0] = 0;
			paintBoardWithQueen(board, tempBoard, queen, 1, 0);
		}
		
		//evaluate board for each queen (black)
		for (OurPair<Integer, Integer> queen : blackPositions)
		{
			tempBoard[queen.getX()][queen.getY()][1] = 0;
			paintBoardWithQueen(board, tempBoard, queen, 2, 0);
		}
		
		
		int white = 0;
		int black = 0;
		
		int whiteTemp = 0;
		int blackTemp = 0;
		
		for (int x = 0; x < 10; x++)
		{
			for (int y = 0; y < 10; y++)
			{
				whiteTemp = tempBoard[x][y][0];
				blackTemp = tempBoard[x][y][1];
				
				if (whiteTemp == 0 || blackTemp == 0)
				{
					continue;
				}
				
				if (whiteTemp > blackTemp)
					white++;
				else if (blackTemp > whiteTemp)
					black++;
			}
		}
		
		return white - black;
	}
	
	private static void paintBoardWithQueen(OurBoard board, int[][][] tempBoard, OurPair<Integer, Integer> queen, int side, int depth)
	{
		//get positions the queen can move now
		HashSet<OurPair<Integer, Integer>> moves = GameRules.getLegalQueenMoves(board, queen, side);
		
		//for every move available to queen, see how expensive it is
		for (OurPair<Integer, Integer> move : moves)
		{
			if (tempBoard[move.getX()][move.getY()][side-1] > depth+1)
			{
				tempBoard[move.getX()][move.getY()][side-1] = depth+1;
				paintBoardWithQueen(board, tempBoard, move, side, depth+1);
			}
		}
	}

}
