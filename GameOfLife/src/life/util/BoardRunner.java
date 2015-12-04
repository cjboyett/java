package life.util;

import life.model.Grid;
import life.model.Rule;

/**
 * 
 * @author Casey
 *
 * Class for taking a grid, applying a given rule to the grid, and returning
 * the next generation of the grid.
 */
public class BoardRunner
{
	private Rule rule;
	private Grid grid, nextGen;
	int[][] differences;
	
	public BoardRunner(Grid grid, Rule rule)
	{
		this.grid = grid;
		this.rule = rule;
		nextGen = new Grid(grid.getWidth(), grid.getHeight());
	}
	
	public void setGrid(Grid grid)
	{
		this.grid = grid;
		nextGen = new Grid(grid.getWidth(), grid.getHeight());
	}
	
	public Grid getGrid()
	{
		return grid;
	}
	
	public void setRule(Rule rule)
	{
		this.rule = rule;
	}
	
	public Rule getRule()
	{
		return rule;
	}
	
	// Two grids are kept since we cannot change the current grid in place.
	public void nextGeneration()
	{		
		for (int i=0;i<grid.getWidth();i++)
		{
			for (int j=0;j<grid.getHeight();j++)
			{
				nextGen.getCell(i, j).setState(grid.getCell(i, j).getState());
			}
		}

		for (int i=0;i<grid.getWidth();i++)
		{
			for (int j=0;j<grid.getHeight();j++)
			{
				if (grid.getCell(i, j).getState() == 0 && rule.isCellBorn(grid.getCell(i, j), grid.getNeighbors(grid.getCell(i, j)))) nextGen.getCell(i, j).incrementState();
				else if (grid.getCell(i, j).getState() != 0 && !rule.isCellAlive(grid.getCell(i, j), grid.getNeighbors(grid.getCell(i, j)))) nextGen.getCell(i, j).incrementState();
			}
		}
		
		differences = Utility.difference(nextGen, grid);
		
		for (int i=0;i<grid.getWidth();i++)
		{
			for (int j=0;j<grid.getHeight();j++)
			{
				grid.getCell(i, j).setState(nextGen.getCell(i, j).getState());
			}
		}
	}

	// A convenience method for the GridView for drawing the grid.
	public int[][] differences()
	{
		return differences;
	}

}
