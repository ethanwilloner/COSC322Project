package minimax;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import utils.GameRules;
import utils.Move;
import AbstractClasses.Evaluation;
import AbstractClasses.GameSearch;
import ai.OurBoard;

public class concurrentMinimax extends GameSearch
{
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
	 * @param maxThreads number of threads to be used
	 */
	public concurrentMinimax (int m, Evaluation eval)
	{
		this.maxThreads.set(m);
		this.eval = eval;
	}
	

	/**
	 * get next move
	 * @param board current board
	 * @param side which side we maximize
	 * @return best move to be found
	 */
	@SuppressWarnings("unchecked")
	public Move minimaxDecision (OurBoard board, int side)
	{
		//start the clock
		startTime.set(System.currentTimeMillis());
		//set the sides
		this.maxPlayer.set(side);
		this.minPlayer.set((side%2)+1);
		
		// the best result of any search
		minimaxNode globalBest = new minimaxNode(Integer.MIN_VALUE, null);
		
		//the threads to be used
		List<MinimaxThread> searchThreads = new LinkedList<MinimaxThread>();
		
		//try each move
		Iterator<Move> iterator = (Iterator<Move>) GameRules.getLegalMoves(board, maxPlayer.get()).iterator();
		OurBoard tempBoard;
		Move tempMove;
		
		while(iterator.hasNext())
		{
			//add thread
			tempBoard = board.clone();
			tempMove = iterator.next();
			//tempBoard.makeMove(tempMove);
			
			searchThreads.add(new MinimaxThread(tempBoard, tempMove));
		}
		
		
		// if this search was terminated by a cutoff test
		isCutoff.set(false);
		// make thread pool
		ExecutorService executor;
		List<Future<minimaxNode>> results = new LinkedList<Future<minimaxNode>>();

		//the best depth achieved by this search
        localMaxDepth.set(1);
		
		// begin iterative deepening search		
		do
		{
			System.out.println("Scanning depth " + localMaxDepth.get());
			
			//reset alpha beta
			alpha.set(Integer.MIN_VALUE);
			beta.set(Integer.MAX_VALUE);
			
			executor= Executors.newFixedThreadPool(maxThreads.get());
			results.clear();
			
			// give each thread to pool
			for (MinimaxThread searchThread : searchThreads)
			{
				results.add(executor.submit(searchThread));
			}
			
			//the best locally
			minimaxNode localBest = new minimaxNode(Integer.MIN_VALUE, null);
			try
			{
				//reset leaf count
				leafCount.set(0);
				
				// close the pool and wait for execution completion
				executor.shutdown();
				// we'll assume the timeout occurs naturally from the search cutoff test
				executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
				
//				if (isCutoff.get())
//					break;
				
				// find the best result for this search
				minimaxNode search;
				for (Future<minimaxNode> result : results)
				{
					search = result.get();
					localBest.max(search);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			// IMPORTANT: only update the global max if we have truly achieved a
			// deeper search this iteration by not being cutoff.  This could produce
			// a false positive if the search was cutoff with mixed depths.
			if (!isCutoff.get() || globalBest.getMove() == null)
			{
				globalBest.max(localBest);
			}
			System.out.println("Depth " + localMaxDepth.get() + " terminated" + ((isCutoff.get())?" unsuccessfully ":" successfully ") + "with " + leafCount.get() + " leaf nodes");
			localMaxDepth.set(localMaxDepth.get()+1);
		}
		// if we didn't make the target depth then we won't make a deeper target depth next iteration; end the search
		while (!isCutoff.get());
		
		System.out.println("Parallel got to depth: " + (localMaxDepth.get()-1));
	
		return globalBest.getMove();
	}

	/**
	 * A thread for search
	 * @author Yarko
	 *
	 */
	private class MinimaxThread implements Callable<minimaxNode>
	{
		private OurBoard board;
		private Move parentAction;
		
		/**
		 * constructor for thread
		 * @param state initial board
		 * @param action move to be made
		 */
		public MinimaxThread(OurBoard state, Move action)
		{
			this.board = state;
			parentAction = action;
			board.makeMove(action);
		}

		@Override
		public minimaxNode call () throws Exception
		{
			// note depth 2, results from depth 1 are being collected in minimaxDecision
			int depth = 2;
			
			
			// test the cutoff function
			if (board.cutoffTest(depth, startTime.get()))
			{
				isCutoff.set(true);
				return new minimaxNode(eval.evaluateBoard(board, maxPlayer.get()), parentAction);
			}
			//if we've gone too deep
			if (depth > localMaxDepth.get())
			{
				//increment leaf count
				leafCount.set(leafCount.get() + 1);
				return new minimaxNode(eval.evaluateBoard(board, maxPlayer.get()), parentAction);
			}
			
			//moves available from this node
			HashSet<Move> moves = GameRules.getLegalMoves(board, minPlayer.get());
			
			Iterator<Move> it = moves.iterator();

			// we can end the search here if there are no successors
			if (!it.hasNext())
			{
				return new minimaxNode(eval.evaluateBoard(board, maxPlayer.get()), parentAction);
			}
			
			Move action;
			minimaxNode result = new minimaxNode(Integer.MIN_VALUE, parentAction);
			
			int v = Integer.MAX_VALUE;
			int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;
			while (it.hasNext())
			{
				action = it.next();
				board.makeMove(action);
				v = Math.min(v, maxValue(alpha, beta, depth + 1));
				// because we are using one state instance make sure to undo the action during back-tracking!
				board.undoMove(action);
				if (v <= alpha)
				{
					break;
				}
				beta = Math.min(beta, v);
				
				//check if out of time
				if (isCutoff.get() == true)
					break;
				
			}
			
			result.setValue(v);
			return result;
		}
		

		private int maxValue (int alpha, int beta, int depth)
		{
			
			// test for IDS cutoff and the cutoff function
			if (isCutoff.get() == true || board.cutoffTest(depth, startTime.get()))
			{
				//return infinity
				isCutoff.set(true);
//				return Integer.MAX_VALUE; 
				return eval.evaluateBoard(board, maxPlayer.get());				
			}
			if (depth > localMaxDepth.get())
			{
				//increment leaf count
				leafCount.set(leafCount.get() + 1);
				return eval.evaluateBoard(board, maxPlayer.get());
			}
			// actions for MAX player
			Iterator<Move> successors = GameRules.getLegalMoves(board, maxPlayer.get()).iterator();
			// we can end the search here if there are no successors
			if (!successors.hasNext())
			{
				return eval.evaluateBoard(board, maxPlayer.get());
			}
			
			Move action;
			int v = Integer.MIN_VALUE;
			// standard alpha-beta search
			while (successors.hasNext())
			{
				action = successors.next();
				board.makeMove(action);
				v = Math.max(v, minValue(alpha, beta, depth + 1));
				// because we are using one state instance make sure to undo the action during back-tracking!
				board.undoMove(action);
				if (v >= beta)
				{
					break;
				}
				alpha = Math.max(alpha, v);
				
				//check if out of time
				if (isCutoff.get() == true)
					break;
				
			}
			return v;
		}

		private int minValue (int alpha, int beta, int depth)
		{
			// test for IDS cutoff and the cutoff function
			if (isCutoff.get() == true || board.cutoffTest(depth, startTime.get()))
			{
				//return -infinity
				isCutoff.set(true);
//				return Integer.MIN_VALUE;
				return eval.evaluateBoard(board, maxPlayer.get());
			}
			if (depth > localMaxDepth.get())
			{
				//increment leaf count
				leafCount.set(leafCount.get() + 1);
				return eval.evaluateBoard(board, maxPlayer.get());
			}
			// actions for MIN player
			Iterator<Move> successors = GameRules.getLegalMoves(board, minPlayer.get()).iterator();
			// we can end the search here if there are no successors
			if (!successors.hasNext())
			{
				return eval.evaluateBoard(board, maxPlayer.get());
			}
			Move action;
			int v = Integer.MAX_VALUE;
			// standard alpha-beta search
			while (successors.hasNext())
			{
				action = successors.next();
				board.makeMove(action);
				v = Math.min(v, maxValue(alpha, beta, depth + 1));
				// because we are using one state instance make sure to undo the action during back-tracking!
				board.undoMove(action);
				if (v <= alpha)
				{
					break;
				}
				beta = Math.min(beta, v);
				
				//check if out of time
				if (isCutoff.get() == true)
					break;
				
			}
			return v;
		}
	}

	@Override
	public Move getMove(OurBoard board, int side) {
		return minimaxDecision(board, side);
	}
}