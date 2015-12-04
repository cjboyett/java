package life.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class GridTest {

	@Test
	public void testGetWidthAndGetHeight() {
		int width = 5;
		int height = 6;
		Grid grid = new Grid(width, height);
		assertTrue(grid.getWidth() == width);
		assertTrue(grid.getHeight() == height);
	}

	@Test
	public void testSetCell() {
		// New grids default to all 0s
		Grid grid = new Grid(3,3);
		
		Cell cell = new Cell(grid, 1);
		
		// Cell at 1,1 should now be a 1.
		grid.setCell(1, 1, cell);
		
		assertTrue(grid.getCell(1, 1).getState() == 1);
	}

	@Test
	public void testPadLeft() {
		Grid grid = new Grid(4,4);
		grid.getCell(1,1).incrementState();
		grid.getCell(2,1).incrementState();
		grid.getCell(3,2).incrementState();

		Grid grid2 = new Grid(6,4);
		grid2.getCell(3,1).incrementState();
		grid2.getCell(4,1).incrementState();
		grid2.getCell(5,2).incrementState();
		
		grid.padLeft(2);
		
		assertTrue(grid.equals(grid2));
}
	
	@Test
	public void testPadRight() {
		Grid grid = new Grid(4,4);
		grid.getCell(1,1).incrementState();
		grid.getCell(2,1).incrementState();
		grid.getCell(3,2).incrementState();

		Grid grid2 = new Grid(6,4);
		grid2.getCell(1,1).incrementState();
		grid2.getCell(2,1).incrementState();
		grid2.getCell(3,2).incrementState();
		
		grid.padRight(2);
		
		assertTrue(grid.equals(grid2));
}
	
	@Test
	public void testPadTop() {
		Grid grid = new Grid(4,4);
		grid.getCell(1,1).incrementState();
		grid.getCell(2,1).incrementState();
		grid.getCell(3,2).incrementState();

		Grid grid2 = new Grid(4,6);
		grid2.getCell(1,3).incrementState();
		grid2.getCell(2,3).incrementState();
		grid2.getCell(3,4).incrementState();
		
		grid.padTop(2);
		
		assertTrue(grid.equals(grid2));
}
	
	@Test
	public void testPadBottom() {
		Grid grid = new Grid(4,4);
		grid.getCell(1,1).incrementState();
		grid.getCell(2,1).incrementState();
		grid.getCell(3,2).incrementState();

		Grid grid2 = new Grid(4,6);
		grid2.getCell(1,1).incrementState();
		grid2.getCell(2,1).incrementState();
		grid2.getCell(3,2).incrementState();
		
		grid.padBottom(2);
		
		assertTrue(grid.equals(grid2));
}
	
	@Test
	public void testCropLeft() {
		Grid grid = new Grid(4,4);
		grid.getCell(1,1).incrementState();
		grid.getCell(2,1).incrementState();
		grid.getCell(3,2).incrementState();

		Grid grid2 = new Grid(6,4);
		grid2.getCell(3,1).incrementState();
		grid2.getCell(4,1).incrementState();
		grid2.getCell(5,2).incrementState();
		
		grid2.cropLeft(2);
		
		assertTrue(grid.equals(grid2));
}
	
	@Test
	public void testCropRight() {
		Grid grid = new Grid(4,4);
		grid.getCell(1,1).incrementState();
		grid.getCell(2,1).incrementState();
		grid.getCell(3,2).incrementState();

		Grid grid2 = new Grid(6,4);
		grid2.getCell(1,1).incrementState();
		grid2.getCell(2,1).incrementState();
		grid2.getCell(3,2).incrementState();
		
		grid2.cropRight(2);
		
		assertTrue(grid.equals(grid2));
}
	
	@Test
	public void testCropTop() {
		Grid grid = new Grid(4,4);
		grid.getCell(1,1).incrementState();
		grid.getCell(2,1).incrementState();
		grid.getCell(3,2).incrementState();

		Grid grid2 = new Grid(4,6);
		grid2.getCell(1,3).incrementState();
		grid2.getCell(2,3).incrementState();
		grid2.getCell(3,4).incrementState();
		
		grid2.cropTop(2);
		
		assertTrue(grid.equals(grid2));
}
	
	@Test
	public void testCropBottom() {
		Grid grid = new Grid(4,4);
		grid.getCell(1,1).incrementState();
		grid.getCell(2,1).incrementState();
		grid.getCell(3,2).incrementState();

		Grid grid2 = new Grid(4,6);
		grid2.getCell(1,1).incrementState();
		grid2.getCell(2,1).incrementState();
		grid2.getCell(3,2).incrementState();
		
		grid2.cropBottom(2);
		
		assertTrue(grid.equals(grid2));
}
	
	public void testGetNeighborsWithSquareTopology() {
		Grid grid = new Grid(3,3);
		
		// Corners have 3 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(0, 0)).length == 3);
		assertTrue(grid.getNeighbors(grid.getCell(2, 2)).length == 3);
		// Edges have 5 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(0, 1)).length == 5);
		assertTrue(grid.getNeighbors(grid.getCell(1, 0)).length == 5);
		// Centers have 8 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(1, 1)).length == 8);
	}

	@Test
	public void testGetNeighborsWithBandTopology() {
		Grid grid = new Grid(3,3,Grid.Topology.BAND);
		
		// Corners have 5 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(0, 0)).length == 5);
		assertTrue(grid.getNeighbors(grid.getCell(2, 2)).length == 5);
		// Left/right edges have 8 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(0, 1)).length == 8);
		// Top/bottom edges have 5 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(1, 0)).length == 5);
		// Centers have 8 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(1, 1)).length == 8);
	}
	
	@Test
	public void testGetNeighborsWithMobiusTopology() {
		Grid grid = new Grid(3,3,Grid.Topology.MOBIUS);
		
		// Corners have 5 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(0, 0)).length == 5);
		assertTrue(grid.getNeighbors(grid.getCell(2, 2)).length == 5);
		// Left/right edges have 8 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(0, 1)).length == 8);
		// Top/bottom edges have 5 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(1, 0)).length == 5);
		// Centers have 8 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(1, 1)).length == 8);
	}
	
	@Test
	public void testGetNeighborsWithTorusTopology() {
		Grid grid = new Grid(3,3,Grid.Topology.TORUS);
		
		// Corners have 8 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(0, 0)).length == 8);
		assertTrue(grid.getNeighbors(grid.getCell(2, 2)).length == 8);
		// Edges have 8 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(0, 1)).length == 8);
		assertTrue(grid.getNeighbors(grid.getCell(1, 0)).length == 8);
		// Centers have 8 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(1, 1)).length == 8);
	}
	
	@Test
	public void testGetNeighborsWithProjectiveTopology() {
		Grid grid = new Grid(3,3,Grid.Topology.PROJECTIVE);

		// Corners have 8 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(0, 0)).length == 8);
		assertTrue(grid.getNeighbors(grid.getCell(2, 2)).length == 8);
		// Edges have 5 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(0, 1)).length == 8);
		assertTrue(grid.getNeighbors(grid.getCell(1, 0)).length == 8);
		// Centers have 8 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(1, 1)).length == 8);
	}
	
	@Test
	public void testGetNeighborsWithKleinTopology() {
		Grid grid = new Grid(3,3,Grid.Topology.KLEIN);

		// Corners have 8 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(0, 0)).length == 8);
		assertTrue(grid.getNeighbors(grid.getCell(2, 2)).length == 8);
		// Edges have 8 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(0, 1)).length == 8);
		assertTrue(grid.getNeighbors(grid.getCell(1, 0)).length == 8);
		// Centers have 8 neighbors
		assertTrue(grid.getNeighbors(grid.getCell(1, 1)).length == 8);
	}
	
	@Test
	public void testEquals() {
		Grid grid1 = new Grid(4,4);
		Grid grid2 = new Grid(4,4);
		Grid grid3 = new Grid(4,4);
		Grid grid4 = new Grid(3,4);
		
		grid1.setCell(0, 0, new Cell(grid1, 1));
		grid1.setCell(0, 1, new Cell(grid1, 1));

		grid2.setCell(0, 0, new Cell(grid2, 1));
		grid2.setCell(0, 1, new Cell(grid2, 1));
		
		grid3.setCell(0, 0, new Cell(grid3, 1));
		grid3.setCell(1, 1, new Cell(grid3, 1));
		
		assertTrue(grid1.equals(grid2));
		assertFalse(grid1.equals(grid3));
		assertFalse(grid1.equals(grid4));
	}

}
