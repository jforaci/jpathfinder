package org.foraci.math.graph.jpathfinderapp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.foraci.math.graph.pathfinder.NoPathFoundException;

public class JPathFinderFrame extends JFrame
{
	private static final int WIN_WIDTH = 800, WIN_HEIGHT = 600;
	private enum CommandState { CS_FIND_PATH, CS_CLEAR_PATH };

	private JButton cmdRun, cmdSetStart, cmdSetDest;
	private JLabel lblResult;
	private CommandState cmdState;
	private MapGridPanel gridPanel;
	private JPathFinderPanel panel;
	private Container contentPane;
	private Toolkit tk;

	public JPathFinderFrame()
	{
		super("Path Finder");
		contentPane = getContentPane();
		//add panels
		contentPane.setLayout(new BorderLayout());
		gridPanel = new MapGridPanel();
		contentPane.add(gridPanel, BorderLayout.CENTER);
		panel = new JPathFinderPanel();
		panel.setLayout(new GridLayout(1, 4, 8, 8));
		contentPane.add(panel, BorderLayout.SOUTH);
		//set state of app
		cmdState = CommandState.CS_FIND_PATH;
		gridPanel.setWayPoint(MapGridPanel.State.SW_NONE);
		//add cmd button and register events
		cmdRun = new JButton("Find Path");
		cmdRun.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (cmdState == CommandState.CS_FIND_PATH)
					{
						try
						{
							long time = System.currentTimeMillis();
							float cost = gridPanel.findPath();
							time = (System.currentTimeMillis() - time);
							cmdState = CommandState.CS_CLEAR_PATH;
							cmdRun.setText("Reset");
							lblResult.setText("Cost=" + cost + " Time=" + time
								+ " ms");
						}
						catch (NoPathFoundException ev)
						{ //d'oh!
							JOptionPane.showMessageDialog(
								JPathFinderFrame.this, "No path found.",
								"Path Finder", JOptionPane.INFORMATION_MESSAGE);
						}
					}
					else
					{
						cmdState = CommandState.CS_FIND_PATH;
						cmdRun.setText("Find Path");
						gridPanel.resetPath();
						lblResult.setText("");
					}
					gridPanel.repaint();
				}
			});
		cmdSetStart = new JButton("Set Start");
		cmdSetStart.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					gridPanel.setWayPoint(MapGridPanel.State.SW_START);
				}
			});
		cmdSetDest = new JButton("Set Dest");
		cmdSetDest.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					gridPanel.setWayPoint(MapGridPanel.State.SW_DEST);
				}
			});
		//label
		lblResult = new JLabel("");
		//add to panel
		panel.add(cmdRun);
		panel.add(cmdSetStart);
		panel.add(cmdSetDest);
		panel.add(lblResult);
		//build menu
		JMenuBar menuBar = new JMenuBar();
		// file menu
		JMenu mnuFile = new JMenu("File");
		AbstractAction actFileExit = new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					System.exit(0);
				}
			};
		JMenuItem mnuFileExit = new JMenuItem(actFileExit);
		actFileExit.putValue(AbstractAction.NAME, "Exit");
		actFileExit.putValue(AbstractAction.SMALL_ICON,
			new ImageIcon("end.gif"));
		mnuFile.add(mnuFileExit);
		menuBar.add(mnuFile);
		// graph menu
		JMenu mnuGraph = new JMenu("Graph");
		AbstractAction actGraphGenerateRandom = new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					gridPanel.generateRandomFill();
					repaint();
				}
			};
		JMenuItem mnuGraphGenerateRandom = new JMenuItem(actGraphGenerateRandom);
		actGraphGenerateRandom.putValue(AbstractAction.NAME, "Random fill");
		mnuGraph.add(mnuGraphGenerateRandom);
		menuBar.add(mnuGraph);
		// about
		JMenu mnuHelp = new JMenu("Help");
		AbstractAction actAbout = new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					JOptionPane.showMessageDialog(JPathFinderFrame.this,
						"Pathfinder app\n  By: Joe Foraci",
						"About JPathFinder Demo",
						JOptionPane.INFORMATION_MESSAGE);
				}
			};
		actAbout.putValue(AbstractAction.NAME, "About...");
		JMenuItem mnuHelpAbout = new JMenuItem(actAbout);
		mnuHelp.add(mnuHelpAbout);
		menuBar.add(mnuHelp);
		// set menu bar
		setJMenuBar(menuBar);
		//get default TK
		tk = Toolkit.getDefaultToolkit();
		//position
		setLocation(((int) tk.getScreenSize().getWidth() - WIN_WIDTH) / 2,
			((int) tk.getScreenSize().getHeight() - WIN_HEIGHT) / 2);
		setSize(WIN_WIDTH, WIN_HEIGHT);
	}
}
