package org.foraci.math.graph.pathfinder;

public abstract class PathNode
{
	public abstract int id();

	public abstract float getWeight();

	/**
	 * Get the direct neighbors of this node. <br/>Note: This should be treated
	 * as a read-only property and should <i>never </i> be modified. Otherwise,
	 * any associated class' behavior will be undefined.
	 */
	protected abstract PathNode[] getNeighbors();
}
