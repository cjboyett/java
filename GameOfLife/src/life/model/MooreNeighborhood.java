package life.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Casey
 * 
 * A range r Moore neighborhood is the (2r-1) x (2r-1) square of cells
 * surrounding a given cell.  It can also be thought of as all cells
 * within distance r using the L-infinity metric.   
 */

public class MooreNeighborhood extends Neighborhood
{
	private int range;

	public MooreNeighborhood(int range)
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

		if (i < 0 || j < 0 || i > width || j > height)
		{
			System.out.println(i + " " + " " + j + " " + width + " " + height);
			throw new IndexOutOfBoundsException();
		}
		else
		{
			List<Cell> temp = new ArrayList<>();
			for (int x=-range;x<=range;x++)
			{
				for (int y=-range;y<=range;y++)
				{
					if (x != 0 || y != 0)
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
		return (2*range + 1) * (2*range + 1) - 1;
	}
	
}
