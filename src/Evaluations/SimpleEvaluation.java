package Evaluations;

import utils.GameRules;
import AbstractClasses.Evaluation;
import ai.OurBoard;

public class SimpleEvaluation extends Evaluation{

	@Override
	public int evaluateBoard(OurBoard board, int side) {
		
		int whiteSum = 0;
		int blackSum = 0;
		
		whiteSum += GameRules.getLegalMoves(board, 1).size();
		blackSum += GameRules.getLegalMoves(board, 2).size();
		
		if (side == 1)
			return whiteSum - blackSum;
		else
			return blackSum - whiteSum;
	}

}
