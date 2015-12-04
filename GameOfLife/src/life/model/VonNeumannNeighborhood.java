package life.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Casey
 *
 * A range r Von Neumann neighborhood is all cells within distance r
 * of a cell using the L-1 metric.
 */

public class VonNeumannNeighborhood extends Neighborhood
{
	private int range;
	
	public VonNeumannNeighborhood(int range)
	{
		setRange(range);
	}

	public void setRange(int range)
	{
		this.range = range > 0 ? range : 1;
	}
	
	@Override
	public Cell[] getNeighbors(Cell cell)
	{
		int i = cell.x;
		int j = cell.y;
		
		Grid grid = cell.getParent();
		int width = grid.getWidth();
		int height = grid.getHeight();
		Grid.Topology topology = grid.getTopology();
		
		if (i < 0 || j < 0 || i > width || j > height) throw new IndexOutOfBoundsException();
		else
		{
			List<Cell> temp = new ArrayList<>();
			for (int x=-range;x<=range;x++)
			{
				for (int y=Math.abs(x)-range;y<=range-Math.abs(x);y++)
				{
					if ((x != 0 || y != 0) && (Math.abs(x) + Math.abs(y) <= range))
					{
						switch(topology)
						{
						case SQUARE:
						{
							try
							{
								temp.add(grid.getCell(i+x,j+y));
							}
							catch (IndexOutOfBoundsException e) {}
							break;
						}
						case BAND:
						{
							try
							{
								temp.add(grid.getCell((i+x+width)%width,j+y));
							}
							catch (IndexOutOfBoundsException e) {}
							break;
						}
						case MOBIUS:
						{
							try
							{
								int y2 = 0;
								if (i+x>=0 && i+x<width) y2 = j+y;
								else y2 = height-(j+y)-1;
																
								temp.add(grid.getCell((i+x+width)%width,y2));
							}
							catch (IndexOutOfBoundsException e) {}
							break;
						}
						case TORUS:
						{
							try
							{
								temp.add(grid.getCell((i+x+width)%width, (j+y+height)%height));
							}
							catch (IndexOutOfBoundsException e) {}
							break;
						}
						case PROJECTIVE:
						{
							try
							{
								int y2 = 0;
								if (i+x>=0 && i+x<width) y2 = (j+y+height)%height;
								else y2 = (2*height-(j+y)-1)%height;
																
								temp.add(grid.getCell((i+x+width)%width,y2));
							}
							catch (IndexOutOfBoundsException e) {}
							break;
						}
						case KLEIN:
						{
							try
							{
								int x2 = 0;
								if (j+y>=0 && j+y<height) x2 = (i+x+width)%width;
								else x2 = (2*width-(i+x)-1)%width;
								
								int y2 = 0;
								if (i+x>=0 && i+x<width) y2 = (j+y+height)%height;
								else y2 = (2*height-(j+y)-1)%height;
																
								temp.add(grid.getCell(x2,y2));
							}
							catch (IndexOutOfBoundsException e) {}
							break;
						}
						default:
							break;
						}
					}
				}
			}
			return temp.toArray(new Cell[temp.size()]);
		}
	}

	@Override
	public int neighborhoodSize()
	{
		if (range == 1) return 4;
		else return 2 * range * (range + 1);
	}
	
}
