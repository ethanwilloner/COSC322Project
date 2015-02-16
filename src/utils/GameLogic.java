package utils;

import minimax.minimaxSearch;
import minimax.minimaxSearch.minimaxNode;
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
		
//		OurBoard board = new OurBoard();
//		
//		minimaxSearch minimax = new minimaxSearch();
//		
//		
//		minimaxNode move = minimax.minimax(board, 1, false, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
//		
//		System.out.println("Minimax value: " + move.getValue());
//		
//		board.makeMove(move.getMove());
//		
//		System.out.println(board);
		
		samplePlay();
		
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
	
	public static void samplePlay()
	{
		int side = 1;
		
		OurBoard board = new OurBoard();
		
		minimaxSearch minimax = new minimaxSearch();
		
		//while we are still playing
		while (OurEvaluation.evaluateBoard(board, side)[1] == 0)
		{
			minimaxNode node = minimax.minimax(board, 1, true, side, Integer.MIN_VALUE, Integer.MAX_VALUE);
			
			board.makeMove(node.getMove());
			
			System.out.println("move made: " + node.getMove());
			
			System.out.println("minimax score " + node.getValue());
			
			System.out.println("Current evaluation: "+ OurEvaluation.evaluateBoard(board, 1)[0] + "\t" + OurEvaluation.evaluateBoard(board, 1)[1]);
			
			System.out.println(board);
			
			side = (side==1)?2:1;
			
		}
		
		System.out.println(OurEvaluation.evaluateBoard(board, side));
	}
}
