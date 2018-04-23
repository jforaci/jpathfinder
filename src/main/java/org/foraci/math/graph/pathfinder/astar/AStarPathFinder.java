package org.foraci.math.graph.pathfinder.astar;

import java.util.*;

import org.foraci.math.graph.pathfinder.NoPathFoundException;
import org.foraci.math.graph.pathfinder.PathCostEstimator;
import org.foraci.math.graph.pathfinder.PathFinder;
import org.foraci.math.graph.pathfinder.PathNode;

public final class AStarPathFinder extends PathFinder
{
	private PriorityQueue<AStarPathNode> open;
	private PriorityQueue<AStarPathNode> closed;
	private PathNode curDest;
	private LinkedList<AStarPathNode> bestPath;
	private PathCostEstimator successorCost;

	/**
	 * Constructs an instance of this path finder.
	 */
	private AStarPathFinder(PathNode[] graph, PathCostEstimator pathCost,
		PathCostEstimator successorCost)
	{
		super(graph, pathCost);
		this.successorCost = successorCost;
		open = new PriorityQueue<>();
		closed = new PriorityQueue<>();
		curDest = null;
		bestPath = null;
	}

	/**
	 * Factory method to build an <code>AStarPathFinder</code> with a internal
	 * graph representing a 2D grid of nodes.
	 * 
	 * @param arrGraph
	 *            An array of <code>int</code> s representing graph nodes. A
	 *            value of <code>PathFinder.WEIGHT_INF</code> indicates a
	 *            non-passable area, all other values are considered to have a
	 *            weight of 1.0.
	 * @param arrIds
	 *            An array of <code>int<code> ids for each passable graph node
	 *   in <code>arrGraph</code>.
	 * @param pathCost A <code>PathCostEstimator</code> that can guess the cost to
	 *   a travel from a given PathNode to another given PathNode.
	 * @param successorCost A <code>PathCostEstimator</code> that can calculate
	 *   the cost to a travel from a given PathNode to its given successor
	 *   PathNode.
	 */
	public static AStarPathFinder buildGridPathFinder(int[][] arrGraph,
		int[][] arrIds, PathCostEstimator pathCost,
		PathCostEstimator successorCost)
	{
		int rows = arrGraph.length;
		int cols = arrGraph[0].length;
		int[] tmp = new int[rows * cols];
		int[] tmpids = new int[rows * cols];
		for (int j = 0; j < rows; j++)
			for (int i = 0; i < cols; i++)
			{
				tmp[i + j * cols] = arrGraph[j][i];
				tmpids[i + j * cols] = arrIds[j][i];
			}
		return AStarPathFinder.buildGridPathFinder(tmp, tmpids, rows, cols,
			pathCost, successorCost);
	}

	public static AStarPathFinder buildGridPathFinder(int[] arrGraph,
		int[] arrIds, int rows, int cols, PathCostEstimator pathCost,
		PathCostEstimator successorCost)
	{
		int i, j;
		int colsp = cols + 2, rowsp = rows + 2;
		int[] arrGraphPad = new int[(rows + 2) * (cols + 2)];
		AStarPathNode[] graph = new AStarPathNode[rows * cols];
		for (i = 0; i < cols + 2; i++)
			for (j = 0; j < rows + 2; j++)
			{
				if (i == 0 || i == cols + 1 || j == 0 || j == rows + 1)
					arrGraphPad[j * colsp + i] = Integer.MAX_VALUE; //edges are
																	// blocked
				else
					arrGraphPad[j * colsp + i] = arrGraph[(j - 1) * cols
						+ (i - 1)];
			}
		for (i = 0; i < cols; i++)
			for (j = 0; j < rows; j++)
				if (arrGraph[j * cols + i] != Integer.MAX_VALUE)
					graph[j * cols + i] = new AStarPathNode(1.0f, arrIds[j
						* cols + i]);
				else
					graph[j * cols + i] = null;
		connectNeighbors(arrGraph, rows, cols, colsp, arrGraphPad, graph);
		//create new pathfinder with default cost calculation functions
		AStarPathFinder pathFinder = new AStarPathFinder(graph, pathCost,
			successorCost);
		return pathFinder;
	}

	/**
	 * Factory method to build an <code>AStarPathFinder</code> with a internal
	 * graph representing a 2D grid of <i>weighted </i> nodes.
	 * 
	 * @param arrGraph
	 *            An array of <code>int</code> s representing graph nodes. A
	 *            value of <code>PathFinder.WEIGHT_INF</code> indicates a
	 *            non-passable area, all other values are considered to have a
	 *            weight of 1.0.
	 * @param arrIds
	 *            An array of <code>int<code> ids for each passable graph node
	 *   in <code>arrGraph</code>.
	 * @param pathCost A <code>PathCostEstimator</code> that can guess the cost to
	 *   a travel from a given PathNode to another given PathNode.
	 * @param successorCost A <code>PathCostEstimator</code> that can calculate
	 *   the cost to a travel from a given PathNode to its given successor
	 *   PathNode.
	 */
	public static AStarPathFinder buildWeightedGridPathFinder(int[][] arrGraph,
		int[][] arrIds, PathCostEstimator pathCost,
		PathCostEstimator successorCost)
	{
		int rows = arrGraph.length;
		int cols = arrGraph[0].length;
		int[] tmp = new int[rows * cols];
		int[] tmpids = new int[rows * cols];
		for (int j = 0; j < rows; j++)
			for (int i = 0; i < cols; i++)
			{
				tmp[i + j * cols] = arrGraph[j][i];
				tmpids[i + j * cols] = arrIds[j][i];
			}
		return AStarPathFinder.buildWeightedGridPathFinder(tmp, tmpids, rows,
			cols, pathCost, successorCost);
	}

	public static AStarPathFinder buildWeightedGridPathFinder(int[] arrGraph,
		int[] arrIds, int rows, int cols, PathCostEstimator pathCost,
		PathCostEstimator successorCost)
	{
		int i, j;
		int colsp = cols + 2, rowsp = rows + 2;
		int[] arrGraphPad = new int[(rows + 2) * (cols + 2)];
		AStarPathNode[] graph = new AStarPathNode[rows * cols];
		for (i = 0; i < cols + 2; i++)
			for (j = 0; j < rows + 2; j++)
			{
				if (i == 0 || i == cols + 1 || j == 0 || j == rows + 1)
					arrGraphPad[j * colsp + i] = Integer.MAX_VALUE; //edges are
																	// blocked
				else
					arrGraphPad[j * colsp + i] = arrGraph[(j - 1) * cols
						+ (i - 1)];
			}
		int weight;
		for (i = 0; i < cols; i++)
			for (j = 0; j < rows; j++)
				if ((weight = arrGraph[j * cols + i]) != Integer.MAX_VALUE)
				{
					graph[j * cols + i] = new AStarPathNode((float) weight,
						arrIds[j * cols + i]);
				}
				else
					graph[j * cols + i] = null;
		connectNeighbors(arrGraph, rows, cols, colsp, arrGraphPad, graph);
		//create new pathfinder with cost calculation functions
		AStarPathFinder pathFinder = new AStarPathFinder(graph, pathCost,
			successorCost);
		return pathFinder;
	}

	private static void connectNeighbors(int[] arrGraph, int rows, int cols, int colsp, int[] arrGraphPad, AStarPathNode[] graph) {
		int i, j;
		ArrayList<AStarPathNode> neighbors = new ArrayList<>(8); // max of 8 neighbors
		for (i = 0; i < cols; i++)
			for (j = 0; j < rows; j++) {
				neighbors.clear();
				if (arrGraph[j * cols + i] == Integer.MAX_VALUE) { //no node present here
					continue;
				}
				if (arrGraphPad[(j + 1) * colsp + i] != Integer.MAX_VALUE) //left
					neighbors.add(graph[j * cols + (i - 1)]);
				if (arrGraphPad[(j + 1) * colsp + (i + 2)] != Integer.MAX_VALUE) //right
					neighbors.add(graph[j * cols + (i + 1)]);
				if (arrGraphPad[j * colsp + (i + 1)] != Integer.MAX_VALUE) //top
					neighbors.add(graph[(j - 1) * cols + i]);
				if (arrGraphPad[(j + 2) * colsp + (i + 1)] != Integer.MAX_VALUE) //bottom
					neighbors.add(graph[(j + 1) * cols + i]);
				if (arrGraphPad[j * colsp + i] != Integer.MAX_VALUE) //top left
					neighbors.add(graph[(j - 1) * cols + (i - 1)]);
				if (arrGraphPad[j * colsp + (i + 2)] != Integer.MAX_VALUE) //top right
					neighbors.add(graph[(j - 1) * cols + (i + 1)]);
				if (arrGraphPad[(j + 2) * colsp + i] != Integer.MAX_VALUE) //bottom left
					neighbors.add(graph[(j + 1) * cols + (i - 1)]);
				if (arrGraphPad[(j + 2) * colsp + (i + 2)] != Integer.MAX_VALUE) //bottom right
					neighbors.add(graph[(j + 1) * cols + (i + 1)]);
				//create an array for neighbors and fill with vector contents
				AStarPathNode[] arrNeigh = new AStarPathNode[neighbors.size()];
				neighbors.toArray(arrNeigh);
				graph[j * cols + i].setNeighbors(arrNeigh);
			}
	}

	/**
	 * Compute the best path given ids of the of the nodes specified when
	 * creating an instance of this pathfinder.
	 * 
	 * @return the cost of the path found.
	 * @throws NoPathFoundException
	 *             if no path can be found from <code>(x0,y0)</code> to
	 *             <code>(x1,y1)</code>.
	 */
	public float computeBestPath(int startId, int destId)
		throws NoPathFoundException
	{
		PathNode pStart = null, pDest = null;

		for (PathNode pathNode : graph) {
			if (pathNode != null) {
				if (pathNode.id() == startId)
					pStart = pathNode;
				else if (pathNode.id() == destId)
					pDest = pathNode;
				if (pStart != null && pDest != null)
					break;
			}
		}
		return computeBestPath(pStart, pDest);
	}

	/**
	 * Compute the best path given a starting and destination node.
	 * 
	 * @param pStart
	 *            the starting node.
	 * @param pDest
	 *            the destination node.
	 * @return the cost of the path found.
	 * @throws NoPathFoundException
	 *             if no path can be found from <code>pStart</code> to
	 *             <code>pDest</code>.
	 */
	public float computeBestPath(PathNode pStart, PathNode pDest)
		throws NoPathFoundException
	{
		bestPath = null;
		if (pStart == null || pDest == null)
			throw new NoPathFoundException();
		open = new PriorityQueue<>();
		closed = new PriorityQueue<>();
		AStarPathNode start = (AStarPathNode) pStart;
		curDest = pDest; //setup global ptr to destination path node

		int index;
		int NumNeigh;
		AStarPathNode[] bnNeigh, bnSucc;
		float newCost;

		//start by adding start node to OPEN set
		start.g(0);
		start.h(pathCost.cost(start, curDest)); //est cost to dest
		start.f(start.g() + start.h());
		open.add(start);

		AStarPathNode bestNode, succ;
		do
		{
			//get best node (removes from OPEN set and placed in CLOSED)
			bestNode = getBestNode();
			//check if bestNode is destination
			if (bestNode == curDest)
			{
				buildPath(bestNode);
				return bestNode.g(); //now path may be retrieved by calling
									 // getBestPath() method
			}
			//neighbors
			bnNeigh = (AStarPathNode[]) bestNode.getNeighbors();
			NumNeigh = (bnNeigh != null) ? bnNeigh.length : 0;
			//successors
			bnSucc = new AStarPathNode[NumNeigh];
			bestNode.setSuccessors(bnSucc);
			for (int i = 0; i < NumNeigh; i++)
			{
				succ = bnNeigh[i];
				//get index of next free successor slot in bestNode
				// since 'succ' is always eventually added to 'bestNode',
				// thus 'i' is index into succ[] array
				index = i;
				newCost = bestNode.g() + successorCost.cost(bestNode, succ);
				if ((open.contains(succ)))
				{
					bnSucc[index] = succ;
					if (succ.g() > newCost)
					{ //a better path is found to succ
						succ.g(newCost);
						succ.f(succ.g() + succ.h());
						succ.setParent(bestNode);
					}
				}
				else if ((closed.contains(succ)))
				{
					bnSucc[index] = succ;
					if (succ.g() > newCost)
					{ //a better path is found to succ
						succ.g(newCost);
						succ.f(succ.g() + succ.h());
						succ.setParent(bestNode);
						downPropagate(succ);
					}
				}
				else
				{ //not in OPEN nor CLOSED set
					succ.g(newCost);
					succ.h(pathCost.cost(succ, curDest));
					succ.f(succ.g() + succ.h());
					succ.setParent(bestNode);
					open.add(succ);
					bnSucc[index] = succ;
				}
			}
		} while (!open.isEmpty());
		//no path can be found
		throw new NoPathFoundException();
	}

	/**
	 * Get the best path that was found from a call to
	 * <code>AStarPathFinder.computeBestPath()</code> or <code>null</code>
	 * if a <code>NoPathFoundException</code> exception was thrown.
	 * 
	 * @return Returns the <code>LinkedList</code> of
	 *         <code>AStarPathNode</code>s.
	 */
	public LinkedList getBestPath()
	{
		return bestPath;
	}

	/**
	 * Sets the <code>PathCostEstimator</code> to determine the cost to travel
	 * from a node to it's successor. The nodes passed are assumed to be direct
	 * neighbors.
	 */
	public void setSuccessorCostEstimator(PathCostEstimator successorCost)
	{
		this.successorCost = successorCost;
	}

	private void downPropagate(AStarPathNode parent)
	{
		//stack for Depth-First traversal of parents' successors
		Deque<AStarPathNode> stack = new ArrayDeque<>();
		AStarPathNode succ;
		int index;
		float newCost;
		AStarPathNode[] pSuccessors = parent.getSuccessors();
		int NumSuccessors = (pSuccessors != null) ? pSuccessors.length : 0;

		//insert parents' immediate successors into stack only if they have g
		// propagated to them
		index = 0;
		while (index < NumSuccessors)
		{
			succ = pSuccessors[index];
			newCost = parent.g() + successorCost.cost(parent, succ);
			if (succ.getParent() == parent || newCost < succ.g())
			{
				succ.g(newCost);
				succ.f(succ.g() + succ.h());
				succ.setParent(parent);
				stack.push(succ);
			}
			index++;
		}
		//
		while (!stack.isEmpty())
		{
			parent = stack.pop();
			pSuccessors = parent.getSuccessors();
			NumSuccessors = (pSuccessors != null) ? pSuccessors.length : 0;
			//
			index = 0;
			while (index < NumSuccessors)
			{
				succ = pSuccessors[index];
				newCost = parent.g() + successorCost.cost(parent, succ);
				if (succ.getParent() == parent || newCost < succ.g())
				{
					succ.g(newCost);
					succ.f(succ.g() + succ.h());
					succ.setParent(parent);
					stack.push(succ);
				}
				index++;
			}
		}
	}

	/**
	 * Move backwards through path whose end is specified by
	 * <code>destination</code> and build a list from the back to front.
	 */
	private void buildPath(AStarPathNode destination)
	{
		if (destination == null)
			return;
		bestPath = new LinkedList<>();
		do
		{
			bestPath.addFirst(destination);
			destination = destination.getParent();
		} while (destination != null);
	}

	private AStarPathNode getBestNode()
	{
		if (open.isEmpty())
			return null;
		AStarPathNode best = open.remove();
		closed.add(best);
		return best;
	}
}
