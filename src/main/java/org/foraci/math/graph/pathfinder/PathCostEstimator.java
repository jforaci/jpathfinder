package org.foraci.math.graph.pathfinder;

public interface PathCostEstimator
{
	float cost(PathNode start, PathNode dest);
}
