package utils;

import minimax.minimaxSearch;
import ai.OurBoard;

public class GameLogic 
{
	
	public static void main(String[] args)
	{
		//initialize board
		
		//initialize gui
		
		//make connection
		//choose room
		//see whose turn it is
		
		OurBoard board = new OurBoard();
		
//		System.out.println(board);
//		
//		
//		Move m = new Move(new OurPair<Integer, Integer>(0, 3), new OurPair<Integer, Integer>(3, 3), new OurPair<Integer, Integer>(4, 3));
//		
//		System.out.println("Is this a legal move? " + GameRules.isLegalMove(board, m, 1));
//		
//		board.makeMove(m);
//		
//		System.out.println(board);
//		
//		board.undoMove(m);
//		
		System.out.println(board);
		
		
		minimaxSearch minimax = new minimaxSearch();
		
		System.out.println(minimax.minimax(board, 1, true, 2));
		
		System.out.print(board);
//		
		
		
		
	}
	
	public static void playGame()
	{
		//while game is not over
		
		
			// if not our turn
				// 
				// calculate potential moves
				// wait for message
				// check for end game status
					// alert team
				// set to our turn
		
			// else
				// check opponent move legality
					//if illegal
						// send error message
				// calculate move
				// check for end game status
					// alert team
				// send move
				// set to their move
		
	}
}
