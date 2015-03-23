package EvaluationFunctions;

import AbstractClasses.Evaluation;
import AmazonBoard.GameBoard;
import AmazonBoard.GameBoardRules;
import AmazonBoard.Position;

import java.util.HashSet;

public class MullerTegosEvaluation extends Evaluation {

    /**
     * evaluation function that computes a value for whose in a better spot;
     * a negative value places black ahead;
     * a positive value places white ahead;
     * a negative max is a black win;
     * a positive max is a white win;
     *
     * @param board the board evaluated
     * @return
     */
    public static int[] evaluateBoard(GameBoard board, int side, boolean output) {
        HashSet<Position> whitePositions = board.getWhitePositions();
        HashSet<Position> blackPositions = board.getBlackPositions();


        //mark up this board with minimum moves to each tile for black and for white (represented in the last dimension)
        //USE STATIC VAR SO DON"T HAVE TO ALLOCATE MEM DURING RUN
        int[][][] temporaryGameBoard = new int[10][10][2];

        //keep track if we need to check for end of game
        boolean isEnd = true;

        //fill board with max values
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                temporaryGameBoard[x][y][0] = Integer.MAX_VALUE;
                temporaryGameBoard[x][y][1] = Integer.MAX_VALUE;

            }
        }

        //evaluate board for each queen (white)
        //while all queens haven't explored the places they can
        for (Position queen : whitePositions) {
            temporaryGameBoard[queen.getX()][queen.getY()][0] = 0;

            paintBoardWithQueen(board, temporaryGameBoard, queen, 1, 1);
        }
        for (Position queen : blackPositions) {
            temporaryGameBoard[queen.getX()][queen.getY()][1] = 0;

            paintBoardWithQueen(board, temporaryGameBoard, queen, 2, 1);

        }

        int white = 0;
        int black = 0;

        int whiteOnly = 0;
        int blackOnly = 0;

        int whiteTemp = 0;
        int blackTemp = 0;

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                whiteTemp = temporaryGameBoard[x][y][0];
                blackTemp = temporaryGameBoard[x][y][1];

                //is this space not free? or if both black and white can reach in same number of moves,
                if (!board.isFree(x, y) || whiteTemp == blackTemp) {
                    continue;
                }

                //if white's value is less than black's or blacks never reached this tile,
                if (whiteTemp < blackTemp || blackTemp == Integer.MAX_VALUE) {
                    white++;

                    //if black cannot reach this tile, it's whiteOnly
                    if (blackTemp == Integer.MAX_VALUE && whiteTemp != Integer.MAX_VALUE) {
                        whiteOnly++;
                    }
                }
                //if blacks got there quicker or white never got there
                else if (blackTemp < whiteTemp || whiteTemp == Integer.MAX_VALUE) {
                    black++;

                    //if white cannot reach this tile, it's ghetto
                    if (whiteTemp == Integer.MAX_VALUE && blackTemp != Integer.MAX_VALUE) {
                        blackOnly++;
                    }
                }
            }
        }

        //what we return
        int[] rtn = new int[2];

        //if an end-game is reached
        if (blackOnly == black && whiteOnly == white && isEnd) {
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
        if (side == 2) {
            rtn[0] = -rtn[0];
            rtn[1] = -rtn[1];
        }

//        if (output)
//            //print output
//            System.out.println(toString(temporaryGameBoard));


        return rtn;
    }

    private static void paintBoardWithQueen(GameBoard board, int[][][] tempBoard, Position queen, int side, int depth) {
        //get positions the queen can move now
        HashSet<Position> moves = GameBoardRules.getLegalQueenMoves(board, queen, side);


        int old;

        //for every move available to queen, see how expensive it is
        for (Position move : moves) {
            //prune search

            //what was the old evaluation of square?
            old = tempBoard[move.getX()][move.getY()][side - 1];

            //can we improve this locale?
            if (old > depth) {
                //update evaluation
                tempBoard[move.getX()][move.getY()][side - 1] = depth;

                //we can keep going further

                paintBoardWithQueen(board, tempBoard, move, side, depth + 1);
            }
        }
    }

    public static String toString(int[][][] board) {
        StringBuffer stringBuffer = new StringBuffer();

        for (int j = 9; j >= 0; j--) {
            stringBuffer.append("-------------------------------------------------------------\n");
            stringBuffer.append("|");
            for (int i = 0; i < 10; i++) {
                stringBuffer.append(" " + board[i][j][0] + " " + board[i][j][1] + " |");
            }
            stringBuffer.append("\n");
        }

        stringBuffer.append("-------------------------------------------------------------\n");

        return stringBuffer.toString().replaceAll("-1", " ").replaceAll("2147483647", "X");
    }

    @Override
    public int evaluateBoard(GameBoard board, int side) {
        return evaluateBoard(board, side, false)[0];
    }
}
