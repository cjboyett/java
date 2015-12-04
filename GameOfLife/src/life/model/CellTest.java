package life.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class CellTest {

	@Test
	public void testSetState() {
		Cell cell = new Cell(null);
		int state = 1;
		cell.setState(state);
		assertTrue(cell.getState() == state);
	}

	@Test
	public void testIncrementState() {
		Cell cell = new Cell(null, 0,3);
		cell.incrementState();
		assertTrue(cell.getState() == 1);

		cell.incrementState();
		assertTrue(cell.getState() == 2);

		cell.incrementState();
		assertTrue(cell.getState() == 0);
}

	@Test
	public void testToString() {
		Cell cell = new Cell(null);
		int state = 1;
		cell.setState(state);
		assertTrue(cell.toString().equals(state + ""));
	}

}
