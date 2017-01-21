// GOL.java
/*
	Copyright (c) 2006, Toby Oxborrow
	All rights reserved.
	--
	Game of Life
	Runs as a stand alone app or applet.
*/

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.Random;

public final class GOLApplet2 extends JApplet implements Runnable {

	private final int xMax = 50;	// Maximum items across
	private final int yMax = 50;	// Maximum items down

	private boolean[][] curGrid;	// current grid
	private boolean[][] newGrid;	// "current" grid's new state

	Thread runner;

	boolean paintReal;

	public void init()
	{
//		System.out.println("init()");

		EmptyGrids();
		PopulateGrid();
		paintReal = true;

		Container contentArea = getContentPane();
		contentArea.setBackground(Color.white);

		BorderLayout layout = new BorderLayout();
		contentArea.setLayout(layout);

		GOLPanel lifeGrid = new GOLPanel();
		contentArea.add(lifeGrid, BorderLayout.CENTER);

		setContentPane(contentArea);
	}


	public void start()
	{
//		System.out.println("start()");
		if (runner == null) runner = new Thread(this);
		runner.start();
	}


	public void run()
	{
//		System.out.println("run()");
		while (runner == Thread.currentThread())
		{
			Play();

			try { Thread.sleep(800); }
			catch(InterruptedException e) {}
			paintReal = false;
			repaint();

			try { Thread.sleep(200); }
			catch(InterruptedException e) {}
			UpdateGrid();
			paintReal = true;
			repaint();
		}
	}


	// runs the rules of Game of Life and stores the new state in "newGrid"
	public void Play()
	{
//		System.out.println("Play()");
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
			for(int x = 0; x < xMax; x++) {
				curGrid[x][y] = newGrid[x][y];
			}
		}
	}


	public Color GetGridColour(int x, int y)
	{
		if(paintReal == true) {
			return (curGrid[x][y] == true) ? Color.black : Color.white;
		} else {
			if(curGrid[x][y] == true) {
				if(newGrid[x][y] == false) {
					// cell dies
					return Color.red;
				} else {
					// cell survived
					return Color.black;
				}
			} else {
				if(newGrid[x][y] == true) {
					// cell born
					return Color.green;
				} else {
					// no cell (remains dead)
					return Color.white;
				}
			}
		}
	}


	public void SetGridColour(int x, int y, boolean value)
	{
		newGrid[x][y] = value;
	}


	class GOLPanel extends JPanel
	{
		public void paintComponent(Graphics g)
		{
//			System.out.println("paintComponent()");
			Graphics2D g2 = (Graphics2D) g;

			for(int y = 0; y < yMax; y++) {
				for(int x = 0; x < xMax; x++) {
					g2.setPaint(GetGridColour(x, y));
					g2.fill(new Rectangle2D.Double(5 * x, 5 * y, 5, 5));
				}
			}
		}
	}

}
