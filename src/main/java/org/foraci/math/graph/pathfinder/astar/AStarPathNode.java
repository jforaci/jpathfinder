package org.foraci.math.graph.pathfinder.astar;

import org.foraci.math.graph.pathfinder.PathNode;

/**
 * Represents a path node used for A* path-finding. It implements the
 * <code>Comaprable</code> interface so that these nodes may be ordered
 * according to their heuristic weight <code>f</code>.
 */
public final class AStarPathNode extends PathNode implements Comparable
{
	//protected int x,y,
	protected int id; //for relative location and id

	private float weight; //the weight (to travel onto or off of) this node
	private float f, g, h; //f = g (the cost to get to this node) +
	// h (the estimated cost from this node to destination)
	private AStarPathNode parent;
	private AStarPathNode[] neigh;
	private AStarPathNode[] succ;

	/**
	 * Constructs an instance of this path node with the specified (x,y) grid
	 * coordinate and identifier.
	 */
	AStarPathNode(float weight, int id)
	{
		//this.x=x;
		//this.y=y;
		this.weight = weight;
		this.id = id;
		f = g = h = 0;
		parent = null;
		neigh = succ = null;
	}

	/* public accessors */

	public int id()
	{
		return id;
	}

	/** Get x coordinate */
	//public int x() { return x; }
	/** Get y coordinate */
	//public int y() { return y; }
	/* package private accessors */

	float f()
	{
		return f;
	}

	void f(float val)
	{
		f = val;
	}

	float g()
	{
		return g;
	}

	void g(float val)
	{
		g = val;
	}

	float h()
	{
		return h;
	}

	void h(float val)
	{
		h = val;
	}

	/** The weight (to travel onto or off of) this node. */
	public float getWeight()
	{
		return weight;
	}

	/** Gets the array of direct neighbors for this node. */
	public PathNode[] getNeighbors()
	{
		return (PathNode[]) neigh.clone();
	}

	/** Sets the direct neighbors array for this node. */
	void setNeighbors(AStarPathNode[] neigh)
	{
		this.neigh = neigh;
	}

	/** Gets the successors for this node. */
	AStarPathNode[] getSuccessors()
	{
		return succ;
	}

	/** Sets the successors of this node. */
	void setSuccessors(AStarPathNode[] succ)
	{
		this.succ = succ;
	}

	/** Get the parent. */
	AStarPathNode getParent()
	{
		return parent;
	}

	/** Set the parent. */
	void setParent(AStarPathNode parent)
	{
		this.parent = parent;
	}

	/**
	 * Compares this node to other <code>AStarPathNode</code> s by comparing
	 * the heuristic weight values in their <code>f</code> member.
	 */
	public int compareTo(Object o) throws ClassCastException
	{
		if (o == null || !(o instanceof AStarPathNode))
			throw new ClassCastException();
		AStarPathNode other = (AStarPathNode) o;
		if (this == other || f == other.f)
			return 0;
		return (f < other.f) ? -1 : 1;
	}

	/** String representation for this node. (May change!) */
	public String toString()
	{
		return "(id=" + id + ",weight=" + weight + ")";
	}
}
