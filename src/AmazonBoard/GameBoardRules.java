package AmazonBoard;

import java.util.HashSet;

/**
 * A class for the rules of the game
 * 
 * @author Yarko Senyuta
 *
 */
public class GameBoardRules
{
	/**
	 * check if given gameMove is legal for the given side
	 * @param gameMove gameMove to check
	 * @param side 1 for white queen, 2 for black queen
	 * @return true if gameMove is legal, false otherwise
	 */
	public static boolean isLegalMove(GameBoard board, GameMove gameMove, int side)
	{
		HashSet<GameMove> gameMoves = getLegalMoves(board, side);
		return (gameMoves.contains(gameMove));
			
	}
	
	/**
	 * get a set of legal moves for the given side with the given board
	 * @param board game board instance
	 * @param side 1 for white queen, 2 for black queen
	 * @return a set of legal moves
	 */
	public static HashSet<GameMove> getLegalMoves(GameBoard board, int side)
	{
		HashSet<GameMove> toReturn = new HashSet<GameMove>();
		
		
		HashSet<OurPair> queens; 
		//get queens
		if (side == 1)
			queens = board.getWhitePositions();
		else
			queens = board.getBlackPositions();
		
		
		HashSet<OurPair> queenToTiles;
		HashSet<OurPair> arrowToTiles;
		
		//for every given queen,
		for (OurPair queen : queens)
		{
			//get where the queen can move
			queenToTiles = getMoveCross(board, queen);
			
			//remove queen from board
			board.freeSquare(queen.getX(), queen.getY());
			
			//for every place the queen can move, get where arrow can be thrown
			for (OurPair newQueen : queenToTiles)
			{
				//add queen to future tile
				board.placeMarker(newQueen.getX(), newQueen.getY(), side);
				
				arrowToTiles = getMoveCross(board, newQueen);
				
				//remove queen from future tile
				board.placeMarker(newQueen.getX(), newQueen.getY(), board.FREE);
				
				//add to moves
				for (OurPair arrow : arrowToTiles)
				{
					toReturn.add(new GameMove(new OurPair(queen.getX(), queen.getY()), newQueen, arrow));
				}
			}
			
			board.placeMarker(queen.getX(), queen.getY(), side);
		}
		
		return toReturn;
	}
	
	/**
	 * get the tiles available to move by given tile (up, down, left, right, and all the diagonals)
	 * @param board the board instance
	 * @param tileToMove the tile we are interested in
	 * @return tiles where the given one is allowed to move to
	 */
	private static HashSet<OurPair> getMoveCross(GameBoard board, OurPair tileToMove)
	{
		HashSet<OurPair> toReturn = new HashSet<OurPair>();
		
		int x = tileToMove.getX();
		int y = tileToMove.getY();
		
		//first check all the forward moves on x
		for (int tempX = x+1; tempX < board.getColumns(); tempX++)
		{
			//is this a viable place to move tile, add it to hashset
			if (board.isFree(tempX, y))
			{
				toReturn.add(new OurPair(tempX, y));
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
				toReturn.add(new OurPair(tempX, y));
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
				toReturn.add(new OurPair(x, tempY));
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
				toReturn.add(new OurPair(x, tempY));
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
				toReturn.add(new OurPair(tempX, tempY));
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
				toReturn.add(new OurPair(tempX, tempY));
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
				toReturn.add(new OurPair(tempX, tempY));
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
				toReturn.add(new OurPair(tempX, tempY));
			else
				break;
			
			tempX--;
			tempY--;
		}
		
		return toReturn;
		
	}
	/**
	 * end game check
	 * @param board the game board
	 * @return 0 if game still continues, 1 if white wins, 2 if black wins
	 */
	public static int checkEndGame(GameBoard board){
		int rtn = 0;
		
		if(getLegalMoves(board,1).size() == 0){
			rtn = 2; //black wins
		}
		else if(getLegalMoves(board,2).size()==0){
			rtn = 1; //white wins
		}
		
		return rtn;
	}
	
	/**
	 * get a set of legal moves for the given queen (no arrow shot) with the given board
	 * @param board game board instance
	 * @param side 1 for white queen, 2 for black queen
	 * @return a set of legal moves
	 */
	public static HashSet<OurPair> getLegalQueenMoves(GameBoard board, OurPair queen, int side)
	{
		//get where the queen can move
		return getMoveCross(board, queen);
		
	}

}
