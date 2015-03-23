package MiniMax;

import AbstractClasses.GameSearch;
import AmazonBoard.GameBoard;
import AmazonBoard.GameBoardRules;
import AmazonBoard.GameMove;
import AmazonBoard.IllegalMoveException;


/**
 * a sequential MiniMax search
 *
 * @author Yarko Senyuta
 */
public class MiniMaxSearch extends GameSearch {

    private long startTime;
    private boolean isCutoff;
    private long leafCount;

    public MiniMaxNode minimax(GameBoard board, int depth, int maxDepth, boolean maximizingPlayer, int side, int alpha, int beta) throws IllegalMoveException {
        if (board.cutoffTest(depth, startTime)) {
            isCutoff = true;
            return new MiniMaxNode(eval.evaluateBoard(board, side), null);
        }

        //evaluation
        int evaluation = eval.evaluateBoard(board, side);

        //if we have run out of depth or one side has pretty much won
        if (depth > maxDepth || GameBoardRules.checkEndGame(board) != 0/*eval[1] != 0*/) {
            leafCount++;
            return new MiniMaxNode(evaluation, null);
        }

        int bestValue;
        GameMove bestGameMove = null;
        int val;
        MiniMaxNode node;

        if (maximizingPlayer) {
            bestValue = Integer.MIN_VALUE;
            //maximizing our moves
            for (GameMove m : GameBoardRules.getLegalMoves(board, side)) {
                board.makeMove(m);

                node = minimax(board, depth + 1, maxDepth, false, side /*(side==1)?2:1*/, alpha, beta);

                val = node.value;

                if (bestValue < val) {
                    bestValue = val;
                    bestGameMove = m;
                }

                //undo gameMove
                board.undoMove(m);

                //alpha check
                alpha = Math.max(alpha, bestValue);

                if (beta <= alpha) {
                    break;
                }

                //check if we can keep looking
                //check if out of time
                if (board.cutoffTest(depth, startTime) == true) {
                    isCutoff = true;
                    break;
                }

            }

            return new MiniMaxNode(bestValue, bestGameMove);

        } else {
            bestValue = Integer.MAX_VALUE;
            //minimizing other player's moves
            for (GameMove m : GameBoardRules.getLegalMoves(board, side)) {
                board.makeMove(m);
                node = minimax(board, depth + 1, maxDepth, true, side /*(side==1)?2:1*/, alpha, beta);

                val = node.value;

                if (bestValue > val) {
                    bestValue = val;
                    bestGameMove = m;
                }
                //undo gameMove
                board.undoMove(m);

                //alpha beta check
                beta = Math.min(beta, bestValue);

                if (beta <= alpha) {
                    break;
                }

                //check if we can keep looking
                //check if out of time
                if (board.cutoffTest(depth, startTime) == true) {
                    isCutoff = true;
                    break;
                }
            }

            return new MiniMaxNode(bestValue, bestGameMove);
        }

    }

    @Override
    public GameMove getMove(GameBoard board, int side) throws IllegalMoveException {

        //use iterative deepening
        int depth = 1;

        startTime = System.currentTimeMillis();
        isCutoff = false;

        GameMove bestGameMoveSoFar = null;

        int bestValSoFar = Integer.MIN_VALUE;

        //while we still have time, do iterative deepening
        while (!isCutoff) {
            leafCount = 0;
            System.out.println("Scanning depth: " + depth);
            MiniMaxNode node = minimax(board, 1, depth, true, side, Integer.MIN_VALUE, Integer.MAX_VALUE);
            //update best so far only if we aren't cut off or we don't have any best so far
            if (!isCutoff || bestGameMoveSoFar == null) {
                if (bestValSoFar < node.getValue()) {
                    bestGameMoveSoFar = node.getGameMove();
                    bestValSoFar = node.getValue();
                }
            }

            System.out.println("Depth " + depth + " terminated" + ((isCutoff) ? " unsuccessfully " : " successfully ") + "with " + leafCount + " leaf nodes");
            depth++;

            if(leafCount == 0)
            {
                break;
            }
        }
        return bestGameMoveSoFar;
    }
}
