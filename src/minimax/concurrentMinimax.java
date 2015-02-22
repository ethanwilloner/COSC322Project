package minimax;

import static utils.GameLogic.debug;

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
import java.util.logging.Level;

import utils.GameRules;
import utils.Move;
import utils.OurEvaluation;
import ai.OurBoard;

public class concurrentMinimax
{
	private int maxThreads;
	private AtomicLong startTime = new AtomicLong();
	
	private int maxPlayer;
	private int minPlayer;
	
	private AtomicInteger localMaxDepth = new AtomicInteger();
	private AtomicBoolean isCutoff = new AtomicBoolean();

	/**
	 * constructor
	 * @param maxThreads number of threads to be used
	 */
	public concurrentMinimax (int maxThreads)
	{
		this.maxThreads = maxThreads;
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
		this.maxPlayer = side;
		this.minPlayer = (side%2)+1;
		
		// the best result of any search
		minimaxNode globalBest = new minimaxNode(Integer.MIN_VALUE, null);
		
		//the threads to be used
		List<MinimaxThread> searchThreads = new LinkedList<MinimaxThread>();
		
		//try each move
		Iterator<Move> iterator = (Iterator<Move>) GameRules.getLegalMoves(board, maxPlayer).iterator();
		while(iterator.hasNext())
		{
			//add thread
			searchThreads.add(new MinimaxThread(board.clone(), iterator.next()));
		}
		
		
		// if this search was terminated by a cutoff test
		isCutoff.set(false);
		// make thread pool
		ExecutorService executor;
		List<Future<minimaxNode>> results = new LinkedList<Future<minimaxNode>>();
		
		// begin iterative deepening search		
		do
		{
			//the best depth achieved by this search
			localMaxDepth.set(1);
			executor= Executors.newFixedThreadPool(maxThreads);
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
				// close the pool and wait for execution completion
				executor.shutdown();
				// we'll assume the timeout occurs naturally from the search cutoff test
				executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
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
			localMaxDepth.set(localMaxDepth.get()+1);;
		}
		// if we didn't make the target depth then we won't make a deeper target depth next iteration; end the search
		while (!isCutoff.get());
		
	
		debug.logp(Level.INFO, "MinimaxSearch", "minimaxDecision", "Search took:"+(System.currentTimeMillis()-startTime.get())+" maximum depth:"+ localMaxDepth.get() +" best value:"+globalBest.getValue());
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
				return new minimaxNode(OurEvaluation.evaluateBoard(board, maxPlayer)[0], parentAction);
			}
			//if we've gone too deep
			if (depth >= localMaxDepth.get())
			{
				return new minimaxNode(OurEvaluation.evaluateBoard(board, maxPlayer)[0], parentAction);
			}
			Iterator<Move> it = GameRules.getLegalMoves(board, minPlayer).iterator();

			// we can end the search here if there are no successors
			if (!it.hasNext())
			{
				return new minimaxNode(OurEvaluation.evaluateBoard(board, maxPlayer)[0], parentAction);
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
			}
			result.setValue(v);
			return result;
		}
		

		private int maxValue (int alpha, int beta, int depth)
		{
			
			// test for IDS cutoff and the cutoff function
			if (board.cutoffTest(depth, startTime.get()))
			{
				isCutoff.set(true);
				return OurEvaluation.evaluateBoard(board, maxPlayer)[0];				
			}
			if (depth >= localMaxDepth.get())
			{
				return OurEvaluation.evaluateBoard(board, maxPlayer)[0];
			}
			// actions for MAX player
			Iterator<Move> successors = GameRules.getLegalMoves(board, maxPlayer).iterator();
			// we can end the search here if there are no successors
			if (!successors.hasNext())
			{
				return OurEvaluation.evaluateBoard(board, maxPlayer)[0];
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
			}
			return v;
		}

		private int minValue (int alpha, int beta, int depth)
		{
			// test for IDS cutoff and the cutoff function
			if (board.cutoffTest(depth, startTime.get()))
			{
				isCutoff.set(true);
				return OurEvaluation.evaluateBoard(board, maxPlayer)[0];
			}
			if (depth >= localMaxDepth.get())
			{
				return OurEvaluation.evaluateBoard(board, maxPlayer)[0];
			}
			// actions for MIN player
			Iterator<Move> successors = GameRules.getLegalMoves(board, maxPlayer).iterator();
			// we can end the search here if there are no successors
			if (!successors.hasNext())
			{
				return OurEvaluation.evaluateBoard(board, maxPlayer)[0];
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
			}
			return v;
		}
	}
}