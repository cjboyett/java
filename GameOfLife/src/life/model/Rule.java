package life.model;

import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author Casey
 *
 * Class for controlling how cells change between generations.
 */

// TODO Update to allow more elaborate rules, such as WireWorld or Langton's Loops
// Requires changing born/alive to change state instead

public class Rule
{
	private Set<Integer> birthRule, liveRule;
	
	/* Currently a rule takes an array of integers for births
	 * and an array of integers for staying alive.  These numbers
	 * dictate when a cell is born or dies.  For example, if 2 is
	 * in the birth array, then a dead cell surrounded by 2 live
	 * cells will be born in the next generation.  Similarly, if
	 * 2 is the live array, then a live cell surrounded by 2 live
	 * cells will still be alive in the next generation. 
	 */
	public Rule(Integer[] birth, Integer[] live)
	{
		birthRule = new TreeSet<>();
		liveRule = new TreeSet<>();
		
		for (Integer i : birth)
		{
			//if (i >=0 && i <=8)
			birthRule.add(i);
		}
		for (Integer i : live)
		{
			//if (i >=0 && i <=8)
			liveRule.add(i);
		}		
	}
	
	public boolean isCellBorn(Cell cell, Cell... neighbors)
	{
		if (cell.getState() != 0) return false;
		else
		{
			int liveNeighbors = 0;
			for (Cell c : neighbors) liveNeighbors += (c.getState() > 0 ? 1 : 0);
			return birthRule.contains(liveNeighbors);
		}
	}

	public boolean isCellAlive(Cell cell, Cell... neighbors)
	{
		if (cell.getState() == 0) return false;
		else
		{
			int liveNeighbors = 0;
			for (Cell c : neighbors) liveNeighbors += (c.getState() > 0 ? 1 : 0);
			return liveRule.contains(liveNeighbors);
		}
	}
	
	@Override
	public String toString()
	{
		String s = "B";
		for (Integer i : birthRule) s += i.toString();
		s += "/S";
		for (Integer i : liveRule) s += i.toString();
		return s;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Rule)
		{
			return (((Rule)o).toString().equals(toString()));
		}
		return false;
	}
}
