package MiniMax;

import AbstractClasses.Evaluation;
import AbstractClasses.GameSearch;
import AmazonBoard.GameBoard;
import AmazonBoard.GameBoardRules;
import AmazonBoard.GameMove;
import AmazonBoard.IllegalMoveException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ConcurrentMiniMax extends GameSearch {
    private AtomicInteger maxThreads = new AtomicInteger();
    private AtomicLong startTime = new AtomicLong();

    private AtomicInteger maxPlayer = new AtomicInteger();
    private AtomicInteger minPlayer = new AtomicInteger();

    private AtomicInteger localMaxDepth = new AtomicInteger();
    private AtomicBoolean isCutoff = new AtomicBoolean();

    private AtomicInteger alpha = new AtomicInteger();
    private AtomicInteger beta = new AtomicInteger();

    private AtomicLong leafCount = new AtomicLong();

    /**
     * constructor
     *
     * @param maxThreads number of threads to be used
     */
    public ConcurrentMiniMax(int m, Evaluation eval) {
        this.maxThreads.set(m);
        this.eval = eval;
    }

    /**
     * get next gameMove
     *
     * @param board current gameBoard
     * @param side  which side we maximize
     * @return best gameMove to be found
     */
    @SuppressWarnings("unchecked")
    public GameMove minimaxDecision(GameBoard board, int side) throws IllegalMoveException {
        //start the clock
        startTime.set(System.currentTimeMillis());
        //set the sides
        this.maxPlayer.set(side);
        this.minPlayer.set((side % 2) + 1);

        // the best result of any search
        MiniMaxNode globalBest = new MiniMaxNode(Integer.MIN_VALUE, null);

        //the threads to be used
        List<MinimaxThread> searchThreads = new LinkedList<MinimaxThread>();

        //try each gameMove
        Iterator<GameMove> iterator = (Iterator<GameMove>) GameBoardRules.getLegalMoves(board, maxPlayer.get()).iterator();
        GameBoard tempBoard;
        GameMove tempGameMove;

        while (iterator.hasNext()) {
            //add thread
            tempBoard = board.clone();
            tempGameMove = iterator.next();
            searchThreads.add(new MinimaxThread(tempBoard, tempGameMove));
        }

        // if this search was terminated by a cutoff test
        isCutoff.set(false);
        // make thread pool
        ExecutorService executor;
        List<Future<MiniMaxNode>> results = new LinkedList<Future<MiniMaxNode>>();

        //the best depth achieved by this search
        localMaxDepth.set(1);

        // begin iterative deepening search
        do {
            //System.out.println("Scanning depth " + localMaxDepth.get());

            //reset alpha beta
            alpha.set(Integer.MIN_VALUE);
            beta.set(Integer.MAX_VALUE);

            executor = Executors.newFixedThreadPool(maxThreads.get());
            results.clear();

            // give each thread to pool
            for (MinimaxThread searchThread : searchThreads) {
                results.add(executor.submit(searchThread));
            }

            //the best locally
            MiniMaxNode localBest = new MiniMaxNode(Integer.MIN_VALUE, null);
            try {
                //reset leaf count
                leafCount.set(0);

                // close the pool and wait for execution completion
                executor.shutdown();
                // we'll assume the timeout occurs naturally from the search cutoff test
                executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);

                // find the best result for this search
                MiniMaxNode search;
                for (Future<MiniMaxNode> result : results) {
                    search = result.get();
                    localBest.max(search);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // IMPORTANT: only update the global max if we have truly achieved a
            // deeper search this iteration by not being cutoff.  This could produce
            // a false positive if the search was cutoff with mixed depths.
            if (!isCutoff.get() || globalBest.getGameMove() == null) {
                globalBest.max(localBest);
            }
            System.out.println("Depth " + localMaxDepth.get() + " terminated" + ((isCutoff.get()) ? " unsuccessfully " : " successfully ") + "with " + leafCount.get() + " leaf nodes");
            localMaxDepth.set(localMaxDepth.get() + 1);
            if(leafCount.get() == 0)
            {
                break;
            }
        }
        // if we didn't make the target depth then we won't make a deeper target depth next iteration; end the search
        while (!isCutoff.get());

        return globalBest.getGameMove();
    }

    @Override
    public GameMove getMove(GameBoard board, int side) throws IllegalMoveException {
        return minimaxDecision(board, side);
    }

    /**
     * A thread for search
     *
     * @author Yarko
     */
    private class MinimaxThread implements Callable<MiniMaxNode> {
        private GameBoard gameBoard;
        private GameMove parentAction;

        /**
         * constructor for thread
         *
         * @param state  initial gameBoard
         * @param action gameMove to be made
         */
        public MinimaxThread(GameBoard state, GameMove action) throws IllegalMoveException {
            this.gameBoard = state;
            parentAction = action;
            gameBoard.makeMove(action);
        }

        @Override
        public MiniMaxNode call() throws Exception {
            // note depth 2, results from depth 1 are being collected in minimaxDecision
            int depth = 2;

            // test the cutoff function
            if (gameBoard.cutoffTest(depth, startTime.get())) {
                isCutoff.set(true);
                return new MiniMaxNode(eval.evaluateBoard(gameBoard, maxPlayer.get()), parentAction);
            }
            //if we've gone too deep
            if (depth > localMaxDepth.get()) {
                //increment leaf count
                leafCount.set(leafCount.get() + 1);
                return new MiniMaxNode(eval.evaluateBoard(gameBoard, maxPlayer.get()), parentAction);
            }

            //gameMoves available from this node
            HashSet<GameMove> gameMoves = GameBoardRules.getLegalMoves(gameBoard, minPlayer.get());

            Iterator<GameMove> it = gameMoves.iterator();

            // we can end the search here if there are no successors
            if (!it.hasNext()) {
                return new MiniMaxNode(eval.evaluateBoard(gameBoard, maxPlayer.get()), parentAction);
            }

            GameMove action;
            MiniMaxNode result = new MiniMaxNode(Integer.MIN_VALUE, parentAction);

            int v = Integer.MAX_VALUE;
            int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;
            while (it.hasNext()) {
                action = it.next();
                gameBoard.makeMove(action);
                v = Math.min(v, maxValue(alpha, beta, depth + 1));
                // because we are using one state instance make sure to undo the action during back-tracking!
                gameBoard.undoMove(action);
                if (v <= alpha) {
                    break;
                }
                beta = Math.min(beta, v);

                //check if out of time
                if (isCutoff.get() == true) {
                    break;
                }
            }

            result.setValue(v);
            return result;
        }


        private int maxValue(int alpha, int beta, int depth) throws IllegalMoveException {

            // test for IDS cutoff and the cutoff function
            if (isCutoff.get() == true || gameBoard.cutoffTest(depth, startTime.get())) {
                //return infinity
                isCutoff.set(true);
                return eval.evaluateBoard(gameBoard, maxPlayer.get());
            }
            if (depth > localMaxDepth.get()) {
                //increment leaf count
                leafCount.set(leafCount.get() + 1);
                return eval.evaluateBoard(gameBoard, maxPlayer.get());
            }
            // actions for MAX player
            Iterator<GameMove> successors = GameBoardRules.getLegalMoves(gameBoard, maxPlayer.get()).iterator();
            // we can end the search here if there are no successors
            if (!successors.hasNext()) {
                return eval.evaluateBoard(gameBoard, maxPlayer.get());
            }

            GameMove action;
            int v = Integer.MIN_VALUE;
            // standard alpha-beta search
            while (successors.hasNext()) {
                action = successors.next();
                gameBoard.makeMove(action);
                v = Math.max(v, minValue(alpha, beta, depth + 1));
                // because we are using one state instance make sure to undo the action during back-tracking!
                gameBoard.undoMove(action);
                if (v >= beta) {
                    break;
                }
                alpha = Math.max(alpha, v);

                //check if out of time
                if (isCutoff.get() == true) {
                    break;
                }

            }
            return v;
        }

        private int minValue(int alpha, int beta, int depth) throws IllegalMoveException {
            // test for IDS cutoff and the cutoff function
            if (isCutoff.get() == true || gameBoard.cutoffTest(depth, startTime.get())) {
                //return -infinity
                isCutoff.set(true);
//				return Integer.MIN_VALUE;
                return eval.evaluateBoard(gameBoard, maxPlayer.get());
            }
            if (depth > localMaxDepth.get()) {
                //increment leaf count
                leafCount.set(leafCount.get() + 1);
                return eval.evaluateBoard(gameBoard, maxPlayer.get());
            }
            // actions for MIN player
            Iterator<GameMove> successors = GameBoardRules.getLegalMoves(gameBoard, minPlayer.get()).iterator();
            // we can end the search here if there are no successors
            if (!successors.hasNext()) {
                return eval.evaluateBoard(gameBoard, maxPlayer.get());
            }
            GameMove action;
            int v = Integer.MAX_VALUE;
            // standard alpha-beta search
            while (successors.hasNext()) {
                action = successors.next();
                gameBoard.makeMove(action);
                v = Math.min(v, maxValue(alpha, beta, depth + 1));
                // because we are using one state instance make sure to undo the action during back-tracking!
                gameBoard.undoMove(action);
                if (v <= alpha) {
                    break;
                }
                beta = Math.min(beta, v);

                //check if out of time
                if (isCutoff.get() == true) {
                    break;
                }
            }
            return v;
        }
    }
}