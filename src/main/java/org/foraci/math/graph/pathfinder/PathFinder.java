package org.foraci.math.graph.pathfinder;

import java.util.LinkedList;

public abstract class PathFinder
{
	public static final int WEIGHT_INF = Integer.MAX_VALUE;

	/**
	 * Privately built graph is simply an array of <code>PathNode</code>s.
	 * This should never be exposed to clients.
	 */
	protected PathNode[] graph;
	/**
	 * Used to find the guessed path cost between two arbitrary
	 * <code>PathNode</code>s.
	 */
	protected PathCostEstimator pathCost;

	/**
	 * Helps initialize an instance of a subclass of this path finder.
	 */
	protected PathFinder(PathNode[] graph, PathCostEstimator pathCost)
	{
		this.graph = graph;
		this.pathCost = pathCost;
	}

	/**
	 * Sets the <code>PathCostEstimator</code> to guess the cost of the path
	 * between two arbitrary <code>PathNode</code>s.
	 */
	public void setPathCostEstimator(PathCostEstimator pathCost)
	{
		this.pathCost = pathCost;
	}

	/**
	 * Computes the best path for the internal graph of path nodes returning the
	 * cost.
	 * 
	 * @see #computeBestPath(int,int)
	 * @return the path cost.
	 * @throws NoPathFoundException
	 *             if a path can not be found.
	 */
	public abstract float computeBestPath(PathNode start, PathNode end)
		throws NoPathFoundException;

	public abstract float computeBestPath(int startId, int endId)
		throws NoPathFoundException;

	public abstract LinkedList getBestPath();
}
