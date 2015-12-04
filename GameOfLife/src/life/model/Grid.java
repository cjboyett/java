package life.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Casey
 *
 * Grid class holds a 2D array of cells.  It also keeps track of the neighbors of each cell
 * depending on the current topology and neighborhood style. 
 */

public class Grid
{
	private Cell[][] cells;
	private int width, height;
	private Topology topology;
	private Neighborhood neighborhood;
	
	/* By keeping track of the neighbor cells we cut back on computation
	 * since they only need to be computed once per grid orientation.
	 */
	private Map<Cell, Cell[]> neighborsMap;
	
	public Grid(int width, int height)
	{
		this(width, height, Topology.SQUARE);
	}
	
	public Grid(int width, int height, Topology topology)
	{
		this.width = width > 0 ? width : 1;
		this.height = height > 0 ? height : 1;
		cells = new Cell[width][height];
		for (int i=0;i<this.width;i++)
		{
			for (int j=0;j<this.height;j++)
			{
				cells[i][j] = new Cell(this);//temp.add(new Cell());
				cells[i][j].x = i;
				cells[i][j].y = j;
			}
		}
		
		setTopology(topology);
		setNeighborhood(new MooreNeighborhood(1));
		neighborsMap = new HashMap<Cell, Cell[]>();
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public Topology getTopology()
	{
		return topology;
	}

	public void setTopology(Topology topology)
	{
		this.topology = topology;
		if (neighborsMap != null) neighborsMap.clear();
	}

	public void setNeighborhood(Neighborhood neighborhood)
	{
		this.neighborhood = neighborhood;
		if (neighborsMap != null) neighborsMap.clear();
	}
	
	public void setCell(int i, int j, Cell cell) throws ArrayIndexOutOfBoundsException
	{
		cells[i][j] = cell;
	}
	
	public Cell getCell(int i, int j) throws ArrayIndexOutOfBoundsException
	{
		return cells[i][j];
	}
	
	public Cell[] getNeighbors(Cell cell)
	{
		if (!neighborsMap.containsKey(cell)) neighborsMap.put(cell, neighborhood.getNeighbors(cell));
		return neighborsMap.get(cell);
	}
	
	// Used to grow the grid.  Most useful when doing large chagnes.
	public void pad(int top, int right, int bottom, int left)
	{
		if (top < 0 || right < 0 || bottom < 0 || left < 0) return;
		Cell[][] temp = new Cell[width + right + left][height + top + bottom];
		
		for (int i=0;i<width + right + left;i++)
		{
			for (int j=0;j<height + top + bottom;j++)
			{
				if (i >= left && i < width + left && j >= top && j < height + top) temp[i][j] = cells[i-left][j-top];
				else temp[i][j] = new Cell(this);
				temp[i][j].x = i;
				temp[i][j].y = j;
			}
		}
		
		cells = temp;
		width += right + left;
		height += top + bottom;
		neighborsMap.clear();
	}
	
	public void padLeft(int n)
	{
		pad(0,0,0,n);
	}

	public void padRight(int n)
	{
		pad(0,n,0,0);
	}

	public void padTop(int n)
	{
		pad(n,0,0,0);
	}

	public void padBottom(int n)
	{
		pad(0,0,n,0);
	}
	
	// Used to shrink the grid.  Most useful when doing large changes.
	public void crop(int top, int right, int bottom, int left)
	{
		if (top < 0 || right < 0 || bottom < 0 || left < 0) return;
		Cell[][] temp = new Cell[width - right - left][height - top - bottom];
		
		for (int i=0;i<width - right - left;i++)
		{
			for (int j=0;j<height - top - bottom;j++)
			{
				temp[i][j] = cells[i+left][j+top];
				temp[i][j].x = i;
				temp[i][j].y = j;
			}
		}
		
		cells = temp;
		width -= (right + left);
		height -= (top + bottom);
		neighborsMap.clear();
	}
	
	public void cropLeft(int n)
	{
		crop(0,0,0,n);
	}

	public void cropRight(int n)
	{
		crop(0,n,0,0);
	}

	public void cropTop(int n)
	{
		crop(n,0,0,0);
	}

	public void cropBottom(int n)
	{
		crop(0,0,n,0);
	}
	
/*	private List<List<Cell>> copyGridToList()
	{
		List<List<Cell>> cellsList = new ArrayList<>();
		for (int i=0;i<getWidth();i++)
		{
			cellsList.add(new ArrayList<Cell>());
			for (int j=0;j<getHeight();j++)
			{
				cellsList.get(i).add(cells[i][j]);
			}
		}
		return cellsList;
	}
	
	private void setGridFromList(List<List<Cell>> cellsList)
	{
		width = cellsList.size();
		height = cellsList.get(0).size();
		cells = new Cell[width][height];
		for (int i=0;i<width;i++)
		{
			for (int j=0;j<height;j++)
			{
				cells[i][j] = cellsList.get(i).get(j);
			}
		}
	}
*/	

	@Override
	public String toString()
	{
		String s = "";
		for (int j=0;j<height;j++)
		{
			for (int i=0;i<width;i++)
			{
				s += getCell(i,j);
				if (i < width-1) s+= " ";
			}
			if (j < height-1) s+= "\n";
		}
		return s;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Grid)
		{
			if (((Grid)o).getWidth() != getWidth()) return false;
			else if (((Grid)o).getHeight() != getHeight()) return false;
			else if (((Grid)o).getTopology() != getTopology()) return false;
			else
			{
				for (int i=0;i<width;i++)
				{
					for (int j=0;j<height;j++)
					{
						if (((Grid)o).getCell(i,j).getState() != getCell(i,j).getState()) return false;
					}
				}
			}
			return true;
		}
		return false;
	}
	
	// A list of various topologies available for the grid.
	public enum Topology
	{
		SQUARE,
		BAND,
		MOBIUS,
		TORUS,
		PROJECTIVE,
		KLEIN
	}
	
}
