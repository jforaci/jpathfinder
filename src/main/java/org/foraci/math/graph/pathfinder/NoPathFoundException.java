package org.foraci.math.graph.pathfinder;

public class NoPathFoundException extends RuntimeException
{
	public NoPathFoundException()
	{
		super();
	}

	public NoPathFoundException(String msg)
	{
		super(msg);
	}
}
