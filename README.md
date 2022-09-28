## Intro
JPathfinder uses the [A* (or A Star)](https://en.wikipedia.org/wiki/A*_search_algorithm) heuristic to implement an arbitrary pathfinding library in Java with demo. JPathFinder isn't specific to A*, but has that algorithm as the implemenentation.

Given a graph consisting of nodes (`PathNode`), you create a `PathFinder` to find the path with the least "cost" to get from any given node to any other given node. The cost is returned as a floating-point number and calculated by a given `PathCostEstimator`.

The A* implementation is called `AStarPathFinder`. Here you can see the demo application showing a randomly generated map, and the shortest path being overlayed, with its cost and the time to compute it.

![JPathFinder Demo](https://user-images.githubusercontent.com/38170229/192827030-4549e797-e282-4ecb-b8ca-5ea93c26707d.png)

## Quick start
Creating a graph from a regular, two-dimensional grid of weights is a common use case so there is a builder for `AStarPathFinder` that creates the graph for you based on an array of integer weights:
```Java
float findPath() throws NoPathFoundException
{
  PathFinder pathfinder = AStarPathFinder.buildWeightedGridPathFinder(grid,
    gridIds, WeightedPathCostEstimatorDefault,
    WeightedSuccessorCostEstimatorDefault);
  int startid = gridIds[(int) startpos.getY()][(int) startpos.getX()];
  int destid = gridIds[(int) destpos.getY()][(int) destpos.getX()];
  float cost = pathfinder.computeBestPath(startid, destid);
  path = pathfinder.getBestPath();
  return cost;
}
````
Here, all we do is pass the array of weights representing the `grid`. We also include `gridIds` which just associates a unique identifier to each grid cell so that you may specify that ID when asking the pathfinder to find a path from one node to another. Notice the cost estimators passed, in A*'s case there are two. One for _h(n)_ for the cost estimation to the destination, and a successor cost, _g(n)_ unique to A*, which is the cost to move from a given node to one of its successors. The `cost` is returned and the exact path can be retrieved by calling `getBestPath()`. A `NoPathFoundException` will be thrown if there is no path found.

## To-do
- Tests!
- Implement [Dijkstra's pathfinding algorithm](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm).
- Add non-grid and procedurally-generated maps to the JPathfinder demo app.
