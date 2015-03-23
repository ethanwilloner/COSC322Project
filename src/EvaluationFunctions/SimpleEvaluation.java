package EvaluationFunctions;

import AmazonBoard.GameBoardRules;
import AbstractClasses.Evaluation;
import AmazonBoard.GameBoard;

public class SimpleEvaluation extends Evaluation{

	@Override
	public int evaluateBoard(GameBoard board, int side) {
		
		int whiteSum = 0;
		int blackSum = 0;
		
		whiteSum += GameBoardRules.getLegalMoves(board, 1).size();
		blackSum += GameBoardRules.getLegalMoves(board, 2).size();
		
		if (side == 1)
			return whiteSum - blackSum;
		else
			return blackSum - whiteSum;
	}

}
