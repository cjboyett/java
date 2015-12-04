package life.util;

import life.model.Grid;

/**
 * 
 * @author Casey
 *
 * A class of utility functions.
 */
public class Utility
{
	// Used by GridView to more efficiently draw grid.
	public static int[][] difference(Grid newGrid, Grid oldGrid)
	{
		if (newGrid.getWidth() != oldGrid.getWidth() || newGrid.getHeight() != oldGrid.getHeight()) return null;
		int[][] differences = new int[newGrid.getWidth()][newGrid.getHeight()];
		
		for (int i=0;i<newGrid.getWidth();i++)
		{
			for (int j=0;j<newGrid.getHeight();j++)
			{
				differences[i][j] = newGrid.getCell(i, j).getState() - oldGrid.getCell(i, j).getState();
			}
		}
		
		return differences;
	}
}
