package org.foraci.math.graph.jpathfinderapp;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import org.foraci.math.graph.pathfinder.NoPathFoundException;
import org.foraci.math.graph.pathfinder.PathCostEstimator;
import org.foraci.math.graph.pathfinder.PathFinder;
import org.foraci.math.graph.pathfinder.PathNode;
import org.foraci.math.graph.pathfinder.astar.AStarPathFinder;

public class MapGridPanel extends JPanel
{
	/* weighted default cost function classes */

	/**
	 * Default path cost estimator. Uses a (fast) linear distance routine to
	 * estimate cost.
	 */
	private final static PathCostEstimator WeightedPathCostEstimatorDefault = new PathCostEstimator()
		{
			public float cost(PathNode start, PathNode dest)
			{
				//get estimated cost; add a smidge so >0 if both nodes happen
				// to have zero weights, otherwise else no successors will be
                // chosen and no path will be found
				float avg = 0.5f * (start.getWeight() + dest.getWeight()) + 0.01f;
				int x0 = start.id() % NUM_GRID_CELLS_X, x1 = dest.id()
					% NUM_GRID_CELLS_X;
				int y0 = start.id() / NUM_GRID_CELLS_X, y1 = dest.id()
					/ NUM_GRID_CELLS_X;
				return (float) fastDistance(x1 - x0, y1 - y0) * avg;
                    
			}
		};

	/**
	 * Default successor cost estimator. <code>start</code> and
	 * <code>dest</code> are assumed to be neighbors.
	 */
	private final static PathCostEstimator WeightedSuccessorCostEstimatorDefault = new PathCostEstimator()
		{
			public float cost(PathNode start, PathNode dest)
			{
				float avg = 0.5f * (start.getWeight() + dest.getWeight());
				int x0 = start.id() % NUM_GRID_CELLS_X, x1 = dest.id()
					% NUM_GRID_CELLS_X;
				int y0 = start.id() / NUM_GRID_CELLS_X, y1 = dest.id()
					/ NUM_GRID_CELLS_X;
				if (x0 == x1 || y0 == y1)
					return 2 * avg;
				else
					return 3 * avg;
			}
		};

	/** package private states for current mode of MapGridPanel */
	static final int SW_NONE = 0, SW_START = 1, SW_DEST = 2;

	private static final int NUM_GRID_CELLS_X = 30, NUM_GRID_CELLS_Y = 30;
	private static final int NUM_WEIGHT_LEVELS = 5;
	private static final Color BLOCKED_COLOR = Color.RED;
	private static final Color[] WEIGHT_COLORS;

	static
	{
		//initialize the Colors to use for each grid cell's weight value
		WEIGHT_COLORS = new Color[NUM_WEIGHT_LEVELS];
		float f = 255.0f / (NUM_WEIGHT_LEVELS - 1);
		int grayLvl;
		for (int i = 0; i < NUM_WEIGHT_LEVELS; i++)
		{
			grayLvl = 255 ^ (int) (i * f); //same as 255 - ...
			WEIGHT_COLORS[i] = new Color(grayLvl, grayLvl, grayLvl);
		}
	}

	private int[][] grid, gridIds;
	private LinkedList path = null;
	private int wayPoint;
	private Image imgStart, imgDest;
	private Point startpos, destpos;
	private BufferedImage mbi = null;

	public MapGridPanel()
	{
		super(false);
		setDoubleBuffered(false);
		//images
		imgStart = Toolkit.getDefaultToolkit().createImage(
			this.getClass().getResource("/imgs/start.gif"));
		imgStart.getWidth(this);
		imgDest = Toolkit.getDefaultToolkit().createImage(
			this.getClass().getResource("/imgs/end.gif"));
		imgDest.getWidth(this);
		//
		startpos = new Point(0, 0);
		destpos = new Point(NUM_GRID_CELLS_X - 1, NUM_GRID_CELLS_Y - 1);
		//alloc grid
		grid = new int[NUM_GRID_CELLS_Y][NUM_GRID_CELLS_X];
		gridIds = new int[NUM_GRID_CELLS_Y][NUM_GRID_CELLS_X];
		//make min cost 1, b/c 0 can lead to (correct) unintuative looking
		// results
		int index = 0;
		for (int j = 0; j < NUM_GRID_CELLS_Y; j++)
		{
			for (int i = 0; i < NUM_GRID_CELLS_X; i++)
			{
				gridIds[j][i] = index++;
			}
		}
		generateRandomFill();
		//add window component listener
		addComponentListener(new ComponentAdapter()
			{
				public void componentResized(ComponentEvent e)
				{
					mbi = null;
					MapGridPanel.this.repaint();
				}
			});
		//add mouse listener
		addMouseListener(new MouseInputAdapter()
			{
				public void mouseClicked(MouseEvent e)
				{
					final int stepX = MapGridPanel.this.getWidth()
						/ NUM_GRID_CELLS_X;
					final int stepY = MapGridPanel.this.getHeight()
						/ NUM_GRID_CELLS_Y;
					int cx = e.getX() / stepX;
					int cy = e.getY() / stepY;
					if (cx < 0 || cx >= NUM_GRID_CELLS_X)
						return;
					if (cy < 0 || cy >= NUM_GRID_CELLS_Y)
						return;
					//
					if (wayPoint == SW_NONE)
					{
						if (e.getButton() == MouseEvent.BUTTON3)
							grid[cy][cx] = (grid[cy][cx] == Integer.MAX_VALUE) ? 1
								: Integer.MAX_VALUE;
						else
						{
							if (grid[cy][cx] == Integer.MAX_VALUE)
								grid[cy][cx] = 1;
							else
								grid[cy][cx] = ++grid[cy][cx] % NUM_WEIGHT_LEVELS;
							if (grid[cy][cx] == 0)
								++grid[cy][cx];
						}
					}
					else if (wayPoint == SW_START)
					{
						startpos = new Point(cx, cy);
					}
					else if (wayPoint == SW_DEST)
					{
						destpos = new Point(cx, cy);
					}
					wayPoint = SW_NONE;
					mbi = null;
					MapGridPanel.this.repaint();
				}
			});
	}

	void generateRandomFill()
	{
		for (int j = 0; j < NUM_GRID_CELLS_Y; j++)
		{
			for (int i = 0; i < NUM_GRID_CELLS_X; i++)
			{
				if ((startpos.getX() == i && startpos.getY() == j)
						|| (destpos.getX() == i && destpos.getY() == j)) {
					grid[j][i] = 1;
					continue;
				}
				final double p;
				if (fastDistance(startpos.x - i, startpos.y - j) < 5
					|| fastDistance(destpos.x - i, destpos.y - j) < 5) {
					p = 1;
				} else {
					p = 0.55;
				}
				grid[j][i] = (Math.random() < p) ? 1 : Integer.MAX_VALUE; //NUM_WEIGHT_LEVELS-1;
			}
		}
		mbi = null;
	}
	
	private void buildMapImage()
	{
		mbi = //(BufferedImage)createImage(getWidth(), getHeight());
			getGraphicsConfiguration()
			.createCompatibleImage(getWidth(), getHeight());
		Graphics2D mg2d = (Graphics2D)mbi.getGraphics();
		int ypos;
		final int stepX = getWidth() / NUM_GRID_CELLS_X;
		final int stepY = getHeight() / NUM_GRID_CELLS_Y;

		mg2d.setColor(Color.WHITE);
		Rectangle2D rect = new Rectangle2D.Float(0, 0, 0, 0);
		for (int j = 0; j < NUM_GRID_CELLS_Y; j++)
		{
			ypos = j * stepY;
			for (int i = 0; i < NUM_GRID_CELLS_X; i++)
			{
				//rect = new
				// Rectangle2D.Float(i*StepX+1,ypos+1,StepX-1,StepY-1);
				rect.setRect(i * stepX + 1, ypos + 1, stepX - 1, stepY - 1);
				if (grid[j][i] >= 0 && grid[j][i] < NUM_WEIGHT_LEVELS)
				{
					mg2d.setColor(WEIGHT_COLORS[grid[j][i]]);
					mg2d.fill(rect);
				}
				else
				{//if (grid[j][i]>=NUM_WEIGHT_LEVELS) {
					mg2d.setColor(BLOCKED_COLOR);
					mg2d.fill(rect);
				}
			}
		}
	}

	/**
	 * Computes the distance from 0,0 to x,y with 3.5% error
	 */
	private static int fastDistance(int x, int y)
	{
		// first compute the absolute value of x,y
		x = Math.abs(x);
		y = Math.abs(y);
		// compute the minimum of x,y
		int mn = Math.min(x, y);
		// return the distance
		return (x + y - (mn >> 1) - (mn >> 2) + (mn >> 4));
	}

	public boolean imageUpdate(Image img, int infoflags, int x, int y,
		int width, int height)
	{
		if ((infoflags & ImageObserver.ERROR) != 0)
		{
			System.out.println("Error loading image!");
			//System.exit(-1);
		}
		if ((infoflags & ImageObserver.WIDTH) != 0
			&& (infoflags & ImageObserver.HEIGHT) != 0)
		{
			repaint();
		}
		if ((infoflags & ImageObserver.SOMEBITS) != 0)
		{
			repaint();
		}
		if ((infoflags & ImageObserver.ALLBITS) != 0)
		{
			System.out.println("done");
			repaint();
			return false;
		}
		return true;
	}

	void setWayPoint(int state)
	{
		wayPoint = state;
	}

	void resetPath()
	{
		path = null;
	}

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

	int getCols()
	{
		return NUM_GRID_CELLS_X;
	}

	int getRows()
	{
		return NUM_GRID_CELLS_Y;
	}
	
	private int cnt = 0;

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if (mbi == null)
			buildMapImage();
		
		Graphics2D g2d = (Graphics2D) g;
		Rectangle2D rect;
		final int stepX = getWidth() / NUM_GRID_CELLS_X;
		final int stepY = getHeight() / NUM_GRID_CELLS_Y;

		//blit grid
		boolean res = g2d.drawImage(mbi, 0, 0, null);
		//mbi=null;
		g2d.setColor(Color.BLACK);
		g2d.drawString(String.valueOf(cnt), 20, 50);
		if (res) cnt++; else cnt+=10;
		//draw path if exists
		g2d.setStroke(new BasicStroke(2));
		int item = 0;
		if (path != null && path.size() > 0)
		{
			Line2D ln;
			int x, y;
			x = ((PathNode) path.get(0)).id() % NUM_GRID_CELLS_X;
			y = ((PathNode) path.get(0)).id() / NUM_GRID_CELLS_X;
			int lastX = x * stepX + stepX / 2, lastY = y * stepY + stepY / 2;
			while (item < path.size())
			{
				PathNode node = (PathNode) path.get(item++);
				x = node.id() % NUM_GRID_CELLS_X;
				y = node.id() / NUM_GRID_CELLS_X;
				ln = new Line2D.Float(lastX, lastY, x = stepX * x + stepX / 2,
					y = stepY * y + stepY / 2);
				rect = new Rectangle2D.Float(x - 1, y - 1, 3, 3);
				g2d.setColor(Color.red);
				g2d.draw(ln);
				g2d.setColor(Color.blue);
				g2d.draw(rect);
				lastX = x;
				lastY = y;
			}
		}
		//draw start/dest points
		int x, y;
		x = (int) startpos.getX();
		y = (int) startpos.getY();
		g2d.drawImage(imgStart, x * stepX, y * stepY, stepX, stepY, null);
		x = (int) destpos.getX();
		y = (int) destpos.getY();
		g2d.drawImage(imgDest, x * stepX, y * stepY, stepX, stepY, null);
	}

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(450, 420);
    }
}