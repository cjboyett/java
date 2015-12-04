package life.model;

/**
 * 
 * @author Casey
 *
 * Neighborhoods determine which cells to test around a given cell.
 */
public abstract class Neighborhood
{
	abstract public Cell[] getNeighbors(Cell cell);
	abstract public int neighborhoodSize();
}
