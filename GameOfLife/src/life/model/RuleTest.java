package life.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class RuleTest {

	@Test
	public void testIsCellBorn() {
		
		/*
		 *  A cell is born when it is currently in a 0
		 *  state and exactly 2 of its neighbors are in
		 *  a 1 state.
		 */
		Rule rule = new Rule(new Integer[]{2}, new Integer[]{2,3});
		
		/*
		 *  0 0 0 0
		 *  0 1 1 0
		 *  0 0 0 0
		 *  0 0 0 0
		 */
		
		Grid grid = new Grid(4,4);
		grid.getCell(1,1).incrementState();
		grid.getCell(1,2).incrementState();
		
		assertTrue(rule.isCellBorn(grid.getCell(0, 1), grid.getNeighbors(grid.getCell(0, 1))));
		assertTrue(rule.isCellBorn(grid.getCell(2, 1), grid.getNeighbors(grid.getCell(2, 1))));
		// Cell 1,1 is already alive
		assertFalse(rule.isCellBorn(grid.getCell(1, 1), grid.getNeighbors(grid.getCell(1, 1))));
		// Cell 3,1 does not have the correct neighbors
		assertFalse(rule.isCellBorn(grid.getCell(3, 1), grid.getNeighbors(grid.getCell(3, 1))));
	}

	@Test
	public void testIsCellAlive() {
		/*
		 *  A cell is alive when it is currently in a 1
		 *  state and exactly 2  or 3 of its neighbors
		 *  are in a 1 state.
		 */
		Rule rule = new Rule(new Integer[]{2}, new Integer[]{2,3});
		
		/*
		 *  0 0 0 0
		 *  0 1 1 0
		 *  0 1 0 0
		 *  0 1 0 0
		 */
		
		Grid grid = new Grid(4,4);
		grid.getCell(1,1).incrementState();
		grid.getCell(1,2).incrementState();
		grid.getCell(2,1).incrementState();
		grid.getCell(3,1).incrementState();
		
		assertTrue(rule.isCellAlive(grid.getCell(1,1), grid.getNeighbors(grid.getCell(1, 1))));
		assertTrue(rule.isCellAlive(grid.getCell(2,1), grid.getNeighbors(grid.getCell(2, 1))));
		// Cell 2,2 is currently in a 0 state
		assertFalse(rule.isCellAlive(grid.getCell(2,2), grid.getNeighbors(grid.getCell(2, 2))));
		// Cell 3,1 does not have the correct neighbors
		assertFalse(rule.isCellAlive(grid.getCell(3,1), grid.getNeighbors(grid.getCell(3, 1))));
	}

	@Test
	public void testToString() {
		Rule rule = new Rule(new Integer[]{2}, new Integer[]{2,3});
		String string = "B2/S23";
		assertTrue(rule.toString().equals(string));
	}
	
	@Test
	public void testEquals() {
		Rule rule1 = new Rule(new Integer[]{2}, new Integer[]{2,3});
		Rule rule2 = new Rule(new Integer[]{2}, new Integer[]{2,3});
		Rule rule3 = new Rule(new Integer[]{2,3}, new Integer[]{2,3});
		
		assertTrue(rule1.equals(rule2));
		assertFalse(rule1.equals(rule3));
	}

}
