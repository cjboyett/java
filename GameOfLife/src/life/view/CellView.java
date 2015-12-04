package life.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import life.model.Cell;

/**
 * 
 * @author Casey
 *
 * View used by OldGridView draw grid.  While convenient it is slow
 * on large grids.
 */

public class CellView extends Rectangle
{
	private Cell cell;
	private static final Color backgroundColors[] = {Color.WHITE, Color.BLACK};
	
	public CellView(Cell cell)
	{
		setSize(7);
		this.cell = cell;
		cell.getStateProperty().addListener((o, oldVal, newVal) -> setBackground());
		setFill(backgroundColors[cell.getState()]);
	}
	
	public void setSize(double size)
	{
		setWidth(size);
		setHeight(size);
	}
	
	public double getSize()
	{
		return getWidth();
	}
	
	public void incrementState()
	{
		cell.incrementState();
	}
	
	public int getState()
	{
		return cell.getState();
	}
	
	private void setBackground()
	{
		setFill(backgroundColors[cell.getState()]);
	}

	// Not used due to performance issues
	public void showGrid(boolean show)
	{
		if (show)
		{
			setStroke(Color.LIGHTGREY);
			this.setStrokeType(StrokeType.INSIDE);
			this.setStrokeWidth(0.15);
		}
		else
		{
			this.setStrokeWidth(0);
		}
	}
	
}
