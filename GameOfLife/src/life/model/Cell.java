package life.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * 
 * @author Casey
 *
 * Class for holding information of individual cells in the grid.  Each cell is aware of its
 * parent grid and its position within that grid.  It is also aware of its current state.
 */

public class Cell
{
	private final Grid parent;
	public int x, y;
	
	private int numberOfStates;
	private IntegerProperty state;
	
	public Cell(Grid parent)
	{
		this(parent, 0, 2);
	}
	
	public Cell(Grid parent, int state)
	{
		this(parent, state, 2);
	}
	
	public Cell(Grid parent, int state, int numberOfStates)
	{
		this.parent = parent;
		this.numberOfStates = numberOfStates > 1 ? numberOfStates : 2;
		this.state = new SimpleIntegerProperty(state % this.numberOfStates);
	}
	
	public Grid getParent()
	{
		return parent;
	}
	
	public void setState(int state)
	{
		this.state.set(state % this.numberOfStates);
	}
	
	public int getState()
	{
		return state.get();
	}
	
	public IntegerProperty getStateProperty()
	{
		return state;
	}
	
	public void incrementState()
	{
		setState(state.get() + 1);
	}
	
	@Override
	public String toString()
	{
		return state.get() + "";
	}
}
