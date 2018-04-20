package org.foraci.math.graph.jpathfinderapp;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class JPathFinderPanel extends JPanel
{
	public JPathFinderPanel()
	{
		super(null);
	}

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		Rectangle2D rect = new Rectangle2D.Float(0f, 0f,
			(float) getWidth() - 1, (float) getHeight() - 1);

		//g2d.setColor(Color.black);
		//g2d.draw(rect);
	}
}
