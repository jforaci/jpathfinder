package org.foraci.math.graph.jpathfinderapp;

import javax.swing.*;

public class JPathFinderDemo
{
	public static void main(String[] args)
	{
		/*
		 * AStarPathFinder pathFinder = AStarPathFinder.BuildPathFinder( new
		 * int[] {0,1,1,0, 0,0,1,0, 1,0,0,0, 1,0,0,1},4,4 );
		 * pathFinder.computeBestPath(0,0,3,0, 4); LinkedList bestPath =
		 * pathFinder.getBestPath(); while(bestPath.size()>0) {
		 * System.out.println((AStarPathNode)bestPath.removeFirst()); }
		 */

		//
		/*
		 * System.out.println(UIManager.getSystemLookAndFeelClassName());
		 * UIManager.LookAndFeelInfo[] lafs =
		 * UIManager.getInstalledLookAndFeels(); for(int i=0; i <lafs.length;
		 * i++) System.out.println(lafs[i].getClassName());
		 */
		try
		{
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
		}
		//
		JPathFinderFrame app = new JPathFinderFrame();
		app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		app.pack();
		app.setVisible(true);
	}
}
