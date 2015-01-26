package utils;

import java.util.HashSet;

import ai.OurBoard;
import ai.OurPair;

/**
 * A class for the rules of the game
 * 
 * @author Yarko Senyuta
 *
 */
public class GameRules 
{
	/**
	 * check if given move is legal for the given side
	 * @param move move to check
	 * @param side 1 for white queen, 2 for black queen
	 * @return true if move is legal, false otherwise
	 */
	public static boolean isLegalMove(OurBoard board, Move move, int side)
	{
		//check if initial queen is indeed a queen
		if (!board.getBlackPositions().contains(move.getInitialQ()) && !board.getWhitePositions().contains(move.getInitialQ()))
			return false;
		HashSet<Move> moves = getLegalMoves(board, side);
		return (moves.contains(move));
			
	}
	
	/**
	 * get a set of legal moves for the given side with the given board
	 * @param board game board instance
	 * @param side 1 for white queen, 2 for black queen
	 * @return a set of legal moves
	 */
	public static HashSet<Move> getLegalMoves(OurBoard board, int side)
	{
		HashSet<Move> toReturn = new HashSet<Move>();
		
		
		HashSet<OurPair<Integer, Integer>> queens; 
		//get queens
		if (side == 1)
			queens = board.getWhitePositions();
		else
			queens = board.getBlackPositions();
		
		
		HashSet<OurPair<Integer, Integer>> queenToTiles;
		HashSet<OurPair<Integer, Integer>> arrowToTiles;
		
		//for every given queen,
		for (OurPair<Integer, Integer> queen : queens)
		{
			//get where the queen can move
			queenToTiles = getMoveCross(board, queen);
			
			//for every place the queen can move, get where arrow can be thrown
			for (OurPair<Integer, Integer> newQueen : queenToTiles)
			{
				arrowToTiles = getMoveCross(board, newQueen);
				//add to moves
				for (OurPair<Integer, Integer> arrow : arrowToTiles)
				{
					toReturn.add(new Move(queen, newQueen, arrow));
				}
			}
		}
		
		return toReturn;
	}
	
	/**
	 * get the tiles available to move by given tile (up, down, left, right, and all the diagonals)
	 * @param board the board instance
	 * @param tileToMove the tile we are interested in
	 * @return tiles where the given one is allowed to move to
	 */
	private static HashSet<OurPair<Integer, Integer>> getMoveCross(OurBoard board, OurPair<Integer, Integer> tileToMove)
	{
		HashSet<OurPair<Integer, Integer>> toReturn = new HashSet<OurPair<Integer, Integer>>();
		
		int x = tileToMove.getLeft();
		int y = tileToMove.getRight();
		
		//first check all the forward moves on x
		for (int tempX = x+1; tempX < board.getColumns(); tempX++)
		{
			//is this a viable place to move tile, add it to hashset
			if (board.isFree(tempX, y))
			{
				toReturn.add(new OurPair<Integer, Integer>(tempX, y));
			}
			//do not need to check any further, cannot get past a blocked tile
			else
				break;
		}
		//now check all the backward moves on x
		for (int tempX = x-1; tempX >= 0; tempX--)
		{
			//is this a viable place to move tile, add it to hashset
			if (board.isFree(tempX, y))
			{
				toReturn.add(new OurPair<Integer, Integer>(tempX, y));
			}
			//do not need to check any further, cannot get past a blocked tile
			else
				break;
		}
		
		//first check all the forward moves on y
		for (int tempY = y+1; tempY < board.getRows(); tempY++)
		{
			//is this a viable place to move tile, add it to hashset
			if (board.isFree(x, tempY))
			{
				toReturn.add(new OurPair<Integer, Integer>(x, tempY));
			}
			//do not need to check any further, cannot get past a blocked tile
			else
				break;
		}
		//now check all the backward moves on y
		for (int tempY = y-1; tempY >= 0; tempY--)
		{
			//is this a viable place to move tile, add it to hashset
			if (board.isFree(x, tempY))
			{
				toReturn.add(new OurPair<Integer, Integer>(x, tempY));
			}
			//do not need to check any further, cannot get past a blocked tile
			else
				break;
		}
		
		//now check all the diagonal moves
		//first check x+, y+
		int tempX = x;
		int tempY = y;
		tempX++;
		tempY++;
		
		while(tempX < board.getColumns() && tempY < board.getRows())
		{
			
			if (board.isFree(tempX, tempY))
				toReturn.add(new OurPair<Integer, Integer>(tempX, tempY));
			else
				break;
			
			tempX++;
			tempY++;
		}
		
		//check x+, y-
		tempX = x;
		tempY = y;
		tempX++;
		tempY--;
		
		while(tempX < board.getColumns() && tempY >= 0)
		{
			
			if (board.isFree(tempX, tempY))
				toReturn.add(new OurPair<Integer, Integer>(tempX, tempY));
			else
				break;
			
			tempX++;
			tempY--;
		}
		
		//check x-, y+
		tempX = x;
		tempY = y;
		tempX--;
		tempY++;
		while(tempX >= 0 && tempY < board.getRows())
		{
			
			if (board.isFree(tempX, tempY))
				toReturn.add(new OurPair<Integer, Integer>(tempX, tempY));
			else
				break;
			
			tempX--;
			tempY++;
		}
		
		//check x-, y-
		tempX = x;
		tempY = y;
		tempX--;
		tempY--;
		while(tempX >= 0 && tempY >= 0)
		{
			
			if (board.isFree(tempX, tempY))
				toReturn.add(new OurPair<Integer, Integer>(tempX, tempY));
			else
				break;
			
			tempX--;
			tempY--;
		}
		
		return toReturn;
		
	}

}
