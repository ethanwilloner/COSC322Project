package AmazonBoard;

import GUI.AmazonsGUI;

import com.rits.cloning.Cloner;

import java.util.HashSet;

/**
 * Board class that contains the instance of the gameBoard
 *
 * @author Yarko Senyuta
 */
public class GameBoard implements Cloneable {
	public static AmazonsGUI gui;
    /**
     * white queen code
     */
    public static final int WQUEEN = 1;
    /**
     * black queen code
     */
    public static final int BQUEEN = 2;
    /**
     * arrow code
     */
    public static final int ARROW = 3;
    /**
     * free space code
     */
    public static final int FREE = -1;

    // TimeLimit in milliseconds
    public static int TimeLimit = 25000;
    static Cloner clone = new Cloner();
    HashSet<Position> whitePositions;
    HashSet<Position> blackPositions;
    /**
     * store gameBoard as a 2D array
     */
    private int[][] gameBoard;
    /**
     * number of rows on gameBoard
     */
    private int rows;
    /**
     * number of columns on the gameBoard
     */
    private int columns;
    /**
     * constructor to initialize our gameBoard
     *
     * @param rows    the number of rows on the gameBoard
     * @param columns the number of columns on the gameBoard
     */
    public GameBoard(AmazonsGUI gui) {

    	this.gui = gui;
        //set rows and columns
        this.rows = 10;
        this.columns = 10;
        //instantiate 2D array
        gameBoard = new int[rows][columns];
        whitePositions = new HashSet<Position>();
        blackPositions = new HashSet<Position>();

        initialize();
    }

    public GameBoard() {
        //set rows and columns
        this.rows = 10;
        this.columns = 10;
        //instantiate 2D array
        gameBoard = new int[rows][columns];
        whitePositions = new HashSet<Position>();
        blackPositions = new HashSet<Position>();

        initialize();
    }

    /**
     * @return the rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * @param rows the rows to set
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * @return the columns
     */
    public int getColumns() {
        return columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(int columns) {
        this.columns = columns;
    }

    /**
     * initialize instance of the gameBoard
     */
    private void initialize() {

        //color the gameBoard free
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                gameBoard[i][j] = FREE;
            }
        }

        //set initial positions of the queens
        gameBoard[0][3] = WQUEEN;
        gameBoard[6][0] = WQUEEN;
        gameBoard[3][0] = WQUEEN;
        gameBoard[9][3] = WQUEEN;

        whitePositions.add(new Position(0, 3));
        whitePositions.add(new Position(6, 0));
        whitePositions.add(new Position(3, 0));
        whitePositions.add(new Position(9, 3));

        gameBoard[0][6] = BQUEEN;
        gameBoard[6][9] = BQUEEN;
        gameBoard[3][9] = BQUEEN;
        gameBoard[9][6] = BQUEEN;

        blackPositions.add(new Position(0, 6));
        blackPositions.add(new Position(6, 9));
        blackPositions.add(new Position(3, 9));
        blackPositions.add(new Position(9, 6));

    }

    /**
     * free a square at pos (x, y)
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void freeSquare(int x, int y) {
        gameBoard[x][y] = FREE;
    }

    public boolean isFree(int x, int y) {
        return (gameBoard[x][y] == FREE);
    }

    /**
     * set a specific piece at a specific location
     *
     * @param x     x-coordinate of location
     * @param y     y-coordinate of location
     * @param piece piece to be placed
     */
    public void placeMarker(int x, int y, int piece) {
        gameBoard[x][y] = piece;
    }

    /**
     * check if specific space is not free
     *
     * @param x x-coordinate of space
     * @param y y-coordinate of space
     * @return true if space is occupied, false if free
     */
    public boolean isMarked(int x, int y) {
        if (gameBoard[x][y] == FREE) {
            return false;
        }
        return true;
    }

    /**
     * get the code at a specific location
     *
     * @param x x-coordinate of space
     * @param y y-coordinate of space
     * @return the code of space at location
     */
    public int getPiece(int x, int y) {
        return gameBoard[x][y];
    }

    public void updateQueenPosition(int oldX, int oldY, int newX, int newY, int queenCode) {

        HashSet<Position> positions;

        if (queenCode == WQUEEN)
            positions = whitePositions;
        else
            positions = blackPositions;

        for (Position p : positions) {

            if (p.getX() == oldX && p.getY() == oldY) {
                p.setX(newX);
                p.setY(newY);
                break;

            }
        }
        if (queenCode == WQUEEN)
            whitePositions = positions;
        else
            blackPositions = positions;
    }

    /**
     * return the ArrayList of black positions
     *
     * @return black positions
     */
    public HashSet<Position> getBlackPositions() {
        return blackPositions;
    }

    /**
     * return the ArrayList of white positions
     *
     * @return
     */
    public HashSet<Position> getWhitePositions() {
        return whitePositions;
    }

    /**
     * make given move to the gameBoard
     *
     * @param action move to be made
     * @param side   which side is making the move
     * @return
     */
    public boolean makeMove(GameMove action) throws IllegalMoveException {
        //get code of queen
        int side = gameBoard[action.getInitialQ().getX()][action.getInitialQ().getY()];

        //check if this move is legal
        if (GameBoardRules.isLegalMove(this, action, side)) {
            //free old queen position
            placeMarker(action.getInitialQ().getX(), action.getInitialQ().getY(), FREE);

            //place new queen
            placeMarker(action.getFinalQ().getX(), action.getFinalQ().getY(), side);

            //place arrow
            placeMarker(action.getArrow().getX(), action.getArrow().getY(), ARROW);

            //update queen in hashset
            updateQueenPosition(action.getInitialQ().getX(), action.getInitialQ().getY(), action.getFinalQ().getX(), action.getFinalQ().getY(), side);
            return true;
        } else {
            throw new IllegalMoveException(action.toString());
        }
    }


    public void undoMove(GameMove gameMove) {
        //get rid of arrow
        gameBoard[gameMove.getArrow().getX()][gameMove.getArrow().getY()] = FREE;

        //figure out side
        int side = gameBoard[gameMove.getFinalQ().getX()][gameMove.getFinalQ().getY()];


        //place old queen position
        placeMarker(gameMove.getInitialQ().getX(), gameMove.getInitialQ().getY(), side);

        //free new queen
        placeMarker(gameMove.getFinalQ().getX(), gameMove.getFinalQ().getY(), FREE);


        //update queen in hashset
        updateQueenPosition(gameMove.getFinalQ().getX(), gameMove.getFinalQ().getY(), gameMove.getInitialQ().getX(), gameMove.getInitialQ().getY(), side);
    }


    /**
     * returns true if designated position is a queen
     *
     * @param x x value
     * @param y y value
     * @return true if queen is at the given position
     */
    public boolean isQueen(int x, int y) {
        Position thisSpace = new Position(x, y);

        if (blackPositions.contains(thisSpace) || whitePositions.contains(thisSpace))
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();

        for (int j = 9; j >= 0; j--) {
            stringBuffer.append("-----------------------------------------\n");
            stringBuffer.append("|");
            for (int i = 0; i < 10; i++) {
                stringBuffer.append(" ");
                stringBuffer.append(gameBoard[i][j]);
                stringBuffer.append(" |");
            }
            stringBuffer.append("\n");
        }

        stringBuffer.append("-----------------------------------------\n");

        return stringBuffer.toString().replaceAll("-1", " ").replaceAll("3", "X");
    }

    public boolean cutoffTest(int depth, long startTime) {
        //  watch memory usage
        if (Runtime.getRuntime().freeMemory() <= 5000000) {
            return true;
        }

        // watch the time
        long time = System.currentTimeMillis() - startTime;
        if (time >= TimeLimit) {
            return true;
        }

        return false;
    }

    @Override
    public GameBoard clone() {
        return clone.deepClone(this);
    }
    
    public void updateGUI(GameMove action) {
		// TODO Auto-generated method stub
		//make move to gui
		gui.makeMove(action.getInitialQ().getX(), action.getInitialQ().getY(), action.getFinalQ().getX(), action.getFinalQ().getY(), action.getArrow().getX(), action.getArrow().getY());
		
	}
    
}
