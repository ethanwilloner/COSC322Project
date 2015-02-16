package minimax;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import javax.naming.directory.SearchResult;

import utils.GameRules;
import utils.Move;
import utils.OurEvaluation;
import ai.OurBoard;


public class concurrentMinimax
{
	private int maxThreads;
	private long startTime;
	
	private int maxPlayer;
	private int minPlayer;
	
	private volatile int cutoffDepth = 1;
	
	private AtomicInteger localMaxDepth = new AtomicInteger();
	private AtomicBoolean isCutoff = new AtomicBoolean();

	/**
	 * Constructor
	 * 
	 * @param maxThreads the max threads to use in the thread pool
	 */
	public concurrentMinimax (int maxThreads)
	{
		this.maxThreads = maxThreads;
	}
	
	/**
	 * @return the current depth at which a search will end during IDS
	 */
	public int getCutoffDepth ()
	{
		return cutoffDepth;
	}
	
	/**
	 * @param cutoffDepth the depth to end a search during IDS
	 */
	public void setCutoffDepth (int cutoffDepth)
	{
		this.cutoffDepth = cutoffDepth;
	}

	/**
	 * Searches the game tree and returns the best possible action according
	 * to the evaluation function and bounded by the cutoff function.
	 * 
	 * @param maxPlayer the MAX player
	 * @param minPlayer the MIN player
	 * @param state initial state
	 * @return an action
	 */
	@SuppressWarnings("unchecked")
	public Move minimaxDecision (OurBoard board, int side)
	{
		// record the start time of the search for the cutoff functions
		startTime = System.currentTimeMillis();
		this.maxPlayer = side;
		this.minPlayer = (side%2)+1;
		// cutoff depth for IDS
		// the best result of any search
		minimaxNode globalBest = new minimaxNode(Integer.MIN_VALUE, null);
		// construct the search sub trees, they will persist through each iteration
		List<MinimaxSearchThread> searchThreads = new LinkedList<MinimaxSearchThread>();
		
		Iterator<Move> iterator = (Iterator<Move>) GameRules.getLegalMoves(board, maxPlayer).iterator();
		while(iterator.hasNext())
		{
			searchThreads.add(new MinimaxSearchThread(board.clone(), iterator.next()));
		}
		// begin IDS
		do
		{
			// the best depth achieved by this search
			localMaxDepth.set(1);
			// if this search was terminated by a cutoff test
			isCutoff.set(false);
			// construct our thread pool
			ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
			List<Future<minimaxNode>> results = new LinkedList<Future<minimaxNode>>();
			// supply each worker thread with a subtree
			for (MinimaxSearchThread searchThread : searchThreads)
			{
				results.add(executor.submit(searchThread));
			}
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
			cutoffDepth++;
		}
		// if we didn't make the target depth then we won't make a deeper target depth next iteration; end the search
		while (!isCutoff.get() && localMaxDepth.get() >= cutoffDepth - 1);
		cutoffDepth -= 2;
		debug.logp(Level.INFO, "MinimaxSearch", "minimaxDecision", "Search took:"+(System.currentTimeMillis()-startTime)+" maximum depth:"+cutoffDepth+" best value:"+globalBest.v);
		return globalBest.getMove();
	}

	/**
	 * A Callable alpha-beta search to be executed on a thread.
	 * 
	 * @author Paul
	 */
	private class MinimaxSearchThread implements Callable<minimaxNode>
	{
		private OurBoard board;
		private Move parentAction;
		
		/**
		 * Constructor.
		 * 
		 * @param state the initial state
		 * @param action the action for the initial state, used for callback
		 */
		public MinimaxSearchThread (OurBoard state, Move action)
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
			localMaxDepth.set(Math.max(depth, localMaxDepth.get()));
			
			// test the cutoff function
			if (board.cutoffTest(depth, startTime))
			{
				isCutoff.set(true);
				return new minimaxNode(OurEvaluation.evaluateBoard(board, maxPlayer)[0], parentAction);
			}
			if (depth >= cutoffDepth)
			{
				return new minimaxNode(OurEvaluation.evaluateBoard(board, maxPlayer)[0], parentAction);
			}
			
			HashSet<Move> successors = GameRules.getLegalMoves(board, minPlayer);
			// we can end the search here if there are no successors
			if (successors.isEmpty())
			{
				return new minimaxNode(OurEvaluation.evaluateBoard(board, maxPlayer)[0], parentAction);
			}
			
			Iterator<Move> it = successors.iterator();
			
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
			localMaxDepth.set(Math.max(depth, localMaxDepth.get()));
			// test for IDS cutoff and the cutoff function
			if (board.cutoffTest(depth, startTime))
			{
				isCutoff.set(true);
				return OurEvaluation.evaluateBoard(board, maxPlayer)[0];				
			}
			if (depth >= cutoffDepth)
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
			localMaxDepth.set(Math.max(depth, localMaxDepth.get()));
			// test for IDS cutoff and the cutoff function
			if (board.cutoffTest(depth, startTime))
			{
				isCutoff.set(true);
				return OurEvaluation.evaluateBoard(board, maxPlayer)[0];
			}
			if (depth >= cutoffDepth)
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