// GOLApplet3.java
/*
	Copyright (c) 2006, Toby Oxborrow
	All rights reserved.
	--
	Game of Life
	Runs as an applet.

*/

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.Random;

public final class GOLApplet3 extends JApplet implements Runnable, ActionListener {

	private final int xMax = 50;	// Maximum items across
	private final int yMax = 50;	// Maximum items down

	private boolean[][] curGrid;	// current grid
	private boolean[][] newGrid;	// "current" grid's new state

	Thread runner;

	boolean paintReal;

	Button startButton;
	Button stopButton;
	Button clearButton;
	Button randomButton;
	GOLPanel lifeGrid;

	public void init()
	{
//		System.out.println("init()");

		// -- Variable init -----------------------

		PopulateGrid();
		paintReal = true;

		// -- Interface init ----------------------

		Container contentArea = getContentPane();

		contentArea.setLayout(new BorderLayout());
		contentArea.setBackground(Color.white);

		Panel top = new Panel();
		Panel bottom = new Panel();

		top.setLayout(new FlowLayout());
		top.setLocation(0, 0);
		top.setSize(new Dimension(270, 270));

		lifeGrid = new GOLPanel();
		lifeGrid.setLocation(10, 10);
		lifeGrid.setSize(new Dimension(250, 250));
		top.add(lifeGrid);

		bottom.setLayout(new FlowLayout());

		startButton = new Button("Start");
		stopButton = new Button("Stop");
		clearButton = new Button("Clear");
		randomButton = new Button("Random");

		startButton.addActionListener(this);
		stopButton.addActionListener(this);
		clearButton.addActionListener(this);
		randomButton.addActionListener(this);

		bottom.add(startButton);
		bottom.add(stopButton);
		bottom.add(clearButton);
		bottom.add(randomButton);

		contentArea.add("Center", top);
		contentArea.add("South", bottom);
	}


	public void startThread()
	{
		System.out.println("startThread()");
		if (runner == null) runner = new Thread(this);
		runner.start();
	}


	public void stopThread()
	{
		System.out.println("stopThread()");
		runner.stop();
		runner = null;
	}


	public void run()
	{
		System.out.println("run()");
		while (runner == Thread.currentThread())
		{
			Play();

			try { Thread.sleep(800); }
			catch(InterruptedException e) {}
			RepaintGrid(false);

			try { Thread.sleep(200); }
			catch(InterruptedException e) {}
			UpdateGrid();
			RepaintGrid(true);
		}
	}


	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == startButton) {
			startThread();
		} else if(e.getSource() == stopButton) {
			stopThread();
		} else if(e.getSource() == randomButton) {
			stopThread();
		 	PopulateGrid();
		 	RepaintGrid(true);
		} else if(e.getSource() == clearButton) {
			stopThread();
			EmptyGrids();
			RepaintGrid(true);
		}
	}


	// runs the rules of Game of Life and stores the new state in "newGrid"
	public void Play()
	{
		System.out.println("Play()");
		/*
		1. Any live cell with fewer than two neighbours dies, as if by loneliness.
		2. Any live cell with more than three neighbours dies, as if by overcrowding.
		3. Any live cell with two or three neighbours lives, unchanged, to the next generation.
		4. Any dead cell with exactly three neighbours comes to life.
		*/
		for(int y = 0; y < yMax; y++) {
			for(int x = 0; x < xMax; x++) {
				int neighboughs = CountNeighboughs(x, y);
				if(curGrid[x][y] == true) {
					if((neighboughs == 2) || (neighboughs == 3))
						newGrid[x][y] = true;
					else
						newGrid[x][y] = false;
				} else {
					if(neighboughs == 3)
						newGrid[x][y] = true;
					else
						newGrid[x][y] = false;
				}
			}
		}
	}


	public int CountNeighboughs(int x, int y)
	{
		int n = 0;

		// -- Row above us ---------------------------
		// if we are on the first row, we can skip checking this row
		if(y > 0) {
			// above left
			if(x > 0)
				n += (curGrid[x-1][y-1] == true) ? 1 : 0;
			// above
			n += (curGrid[x][y-1] == true) ? 1 : 0;
			// above right
			if((x + 1) < xMax)
				n += (curGrid[x+1][y-1] == true) ? 1 : 0;
		}

		// -- Our row --------------------------------
		// left
		if(x > 0)
			n += (curGrid[x-1][y] == true) ? 1 : 0;
		// right
		if((x + 1) < xMax)
			n += (curGrid[x+1][y] == true) ? 1 : 0;

		// -- Row below us ---------------------------
		// if we are on the last row, we can skip checking this row
		if((y + 1) < yMax) {
			// below left
			if(x > 0)
				n += (curGrid[x-1][y+1] == true) ? 1 : 0;
			// below
			n += (curGrid[x][y+1] == true) ? 1 : 0;
			// below right
			if((x + 1) < xMax)
				n += (curGrid[x+1][y+1] == true) ? 1 : 0;
		}

		return n;
	}


	public void EmptyGrids()
	{
		curGrid = new boolean[xMax][yMax];
		newGrid = new boolean[xMax][yMax];

		// create empty grids
		for(int y = 0; y < yMax; y++) {
			for(int x = 0; x < xMax; x++) {
				curGrid[x][y] = false;
				newGrid[x][y] = false;
			}
		}
	}


	// fill "curGrid" with some random values
	public void PopulateGrid()
	{
//		System.out.println("PopulateGrid()");

		EmptyGrids();

		Random r = new Random();

		// this effects density
		int dr = r.nextInt(9) + 1;

		// dummy start values
		for(int y = 0; y < yMax; y++) {
			for(int x = 0; x < xMax; x++) {
				if(r.nextInt(10) < dr)
					curGrid[x][y] = true;
			}
		}
	}


	public void UpdateGrid() {
//		System.out.println("UpdateGrid()");
		for(int y = 0; y < yMax; y++) {
			for(int x = 0; x < xMax; x++)
				curGrid[x][y] = newGrid[x][y];
		}
	}


	public Color GetGridColour(int x, int y)
	{
		if(paintReal == true)
			return (curGrid[x][y] == true) ? Color.black : Color.white;
		else {
			if(curGrid[x][y] == true) {
				// cell dies, or continues to live
				if(newGrid[x][y] == false)
					return Color.red;
				else
					return Color.black;
			} else {
				// cell born, or remains dead
				if(newGrid[x][y] == true)
					return Color.green;
				else
					return Color.white;
			}
		}
	}


	public void SetGridColour(int x, int y, boolean value)
	{
		newGrid[x][y] = value;
	}


	public void RepaintGrid(boolean _paintReal)
	{
		paintReal = _paintReal;
		lifeGrid.repaint();
	}


	class GOLPanel extends JPanel
	{
		public void paintComponent(Graphics g)
		{
			System.out.println("paintComponent()");
			Graphics2D g2 = (Graphics2D) g;

			setLocation(10, 10);
			setSize(250, 250);

			for(int y = 0; y < yMax; y++) {
				for(int x = 0; x < xMax; x++) {
					g2.setPaint(GetGridColour(x, y));
					g2.fill(new Rectangle2D.Double(5 * x, 5 * y, 5, 5));
				}
			}
		}
	}

}
