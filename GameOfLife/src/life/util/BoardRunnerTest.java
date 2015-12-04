package life.util;

import static org.junit.Assert.*;
import life.model.Grid;
import life.model.Rule;

import org.junit.Test;

public class BoardRunnerTest {

	@Test
	public void testSetGrid() {
		BoardRunner runner = new BoardRunner(new Grid(2,2), new Rule(new Integer[]{2}, new Integer[]{2,3}));
		
		Grid grid = new Grid(3,3);
		grid.getCell(1, 1).incrementState();
		runner.setGrid(grid);
		
		assertTrue(runner.getGrid().equals(grid));		
	}

	@Test
	public void testSetRule() {
		BoardRunner runner = new BoardRunner(new Grid(2,2), new Rule(new Integer[]{2}, new Integer[]{2,3}));
		
		Rule rule = new Rule(new Integer[]{2,3}, new Integer[]{5,6});
		runner.setRule(rule);
		assertTrue(runner.getRule().equals(rule));		
	}

	@Test
	public void testNextGenerationWithSquareTopology() {
		
		/*
		 * 0 0 0 0
		 * 0 1 1 0
		 * 0 1 0 0
		 * 0 1 0 0
		 */
		Grid grid = new Grid(4,4);
		grid.getCell(1,1).incrementState();
		grid.getCell(1,2).incrementState();
		grid.getCell(2,1).incrementState();
		grid.getCell(3,1).incrementState();

		/*
		 * 0 0 0 0
		 * 0 1 1 0
		 * 1 1 0 0
		 * 0 0 0 0
		 */
		Grid nextGen = new Grid(4,4);
		nextGen.getCell(1,1).incrementState();
		nextGen.getCell(1,2).incrementState();
		nextGen.getCell(2,0).incrementState();
		nextGen.getCell(2,1).incrementState();

		Rule rule = new Rule(new Integer[]{3}, new Integer[]{2,3});
		BoardRunner runner = new BoardRunner(grid, rule);
		
		runner.nextGeneration();
		
		assertTrue(runner.getGrid().equals(nextGen));
	}

	@Test
	public void testNextGenerationWithBandTopology() {
		
		/*
		 * 0 0 0 0 0
		 * 1 1 0 0 0
		 * 1 0 0 0 0
		 * 1 0 0 0 0
		 * 0 0 0 0 0
		 */
		Grid grid = new Grid(5,5,Grid.Topology.BAND);
		grid.getCell(0,1).incrementState();
		grid.getCell(1,1).incrementState();
		grid.getCell(0,2).incrementState();
		grid.getCell(0,3).incrementState();

		/*
		 * 0 0 0 0 0
		 * 1 1 0 0 0
		 * 1 0 0 0 1
		 * 0 0 0 0 0
		 * 0 0 0 0 0
		 */
		Grid nextGen = new Grid(5,5,Grid.Topology.BAND);
		nextGen.getCell(0,1).incrementState();
		nextGen.getCell(1,1).incrementState();
		nextGen.getCell(0,2).incrementState();
		nextGen.getCell(4,2).incrementState();

		Rule rule = new Rule(new Integer[]{3}, new Integer[]{2,3});
		BoardRunner runner = new BoardRunner(grid, rule);
		
		runner.nextGeneration();

		assertTrue(runner.getGrid().equals(nextGen));
	}

	@Test
	public void testNextGenerationWithMobiusTopology() {
		/*
		 * 0 0 0 0 0 0
		 * 1 1 0 0 0 0
		 * 1 0 0 0 0 0
		 * 1 0 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 */
		Grid grid = new Grid(6,6,Grid.Topology.MOBIUS);
		grid.getCell(0,1).incrementState();
		grid.getCell(1,1).incrementState();
		grid.getCell(0,2).incrementState();
		grid.getCell(0,3).incrementState();

		/*
		 * 0 0 0 0 0 0
		 * 1 1 0 0 0 0
		 * 1 0 0 0 0 0
		 * 0 0 0 0 0 1
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 */
		Grid nextGen = new Grid(6,6,Grid.Topology.MOBIUS);
		nextGen.getCell(0,1).incrementState();
		nextGen.getCell(1,1).incrementState();
		nextGen.getCell(0,2).incrementState();
		nextGen.getCell(5,3).incrementState();

		Rule rule = new Rule(new Integer[]{3}, new Integer[]{2,3});
		BoardRunner runner = new BoardRunner(grid, rule);
		
		runner.nextGeneration();

		assertTrue(runner.getGrid().equals(nextGen));
	}

	@Test
	public void testNextGenerationWithTorusTopology() {
		
		/*
		 * 0 0 0 0 0 0
		 * 1 1 0 0 0 0
		 * 1 0 0 0 0 0
		 * 1 0 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 */
		Grid grid1 = new Grid(6,6,Grid.Topology.TORUS);
		grid1.getCell(0,1).incrementState();
		grid1.getCell(1,1).incrementState();
		grid1.getCell(0,2).incrementState();
		grid1.getCell(0,3).incrementState();

		/*
		 * 0 0 0 0 0 0
		 * 1 1 0 0 0 0
		 * 1 0 0 0 0 1
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 */
		Grid nextGen1 = new Grid(6,6,Grid.Topology.TORUS);
		nextGen1.getCell(0,1).incrementState();
		nextGen1.getCell(1,1).incrementState();
		nextGen1.getCell(0,2).incrementState();
		nextGen1.getCell(5,2).incrementState();

		/*
		 * 0 0 0 0 0 0
		 * 0 1 1 1 0 0
		 * 0 1 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 */
		Grid grid2 = new Grid(6,6,Grid.Topology.TORUS);
		grid2.getCell(1,0).incrementState();
		grid2.getCell(1,1).incrementState();
		grid2.getCell(2,0).incrementState();
		grid2.getCell(3,0).incrementState();

		/*
		 * 0 1 1 0 0 0
		 * 0 1 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 1 0 0 0
		 */
		Grid nextGen2 = new Grid(6,6,Grid.Topology.TORUS);
		nextGen2.getCell(1,0).incrementState();
		nextGen2.getCell(1,1).incrementState();
		nextGen2.getCell(2,0).incrementState();
		nextGen2.getCell(2,5).incrementState();

		Rule rule = new Rule(new Integer[]{3}, new Integer[]{2,3});
		BoardRunner runner1 = new BoardRunner(grid1, rule);
		BoardRunner runner2 = new BoardRunner(grid2, rule);
		
		runner1.nextGeneration();
		runner2.nextGeneration();
		
		assertTrue(runner1.getGrid().equals(nextGen1));
		assertTrue(runner2.getGrid().equals(nextGen2));
	}

	@Test
	public void testNextGenerationWithProjectiveTopology() {

		/*
		 * 0 0 0 0 0 0
		 * 1 1 0 0 0 0
		 * 1 0 0 0 0 0
		 * 1 0 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 */
		Grid grid1 = new Grid(6,6,Grid.Topology.PROJECTIVE);
		grid1.getCell(0,1).incrementState();
		grid1.getCell(1,1).incrementState();
		grid1.getCell(0,2).incrementState();
		grid1.getCell(0,3).incrementState();

		/*
		 * 0 0 0 0 0 0
		 * 1 1 0 0 0 0
		 * 1 0 0 0 0 0
		 * 0 0 0 0 0 1
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 */
		Grid nextGen1 = new Grid(6,6,Grid.Topology.PROJECTIVE);
		nextGen1.getCell(0,1).incrementState();
		nextGen1.getCell(1,1).incrementState();
		nextGen1.getCell(0,2).incrementState();
		nextGen1.getCell(5,3).incrementState();

		/*
		 * 0 1 1 1 0 0
		 * 0 1 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 */
		Grid grid2 = new Grid(6,6,Grid.Topology.PROJECTIVE);
		grid2.getCell(1,0).incrementState();
		grid2.getCell(1,1).incrementState();
		grid2.getCell(2,0).incrementState();
		grid2.getCell(3,0).incrementState();

		/*
		 * 0 1 1 0 0 0
		 * 0 1 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 1 0 0 0
		 */
		Grid nextGen2 = new Grid(6,6,Grid.Topology.PROJECTIVE);
		nextGen2.getCell(1,0).incrementState();
		nextGen2.getCell(1,1).incrementState();
		nextGen2.getCell(2,0).incrementState();
		nextGen2.getCell(2,5).incrementState();

		Rule rule = new Rule(new Integer[]{3}, new Integer[]{2,3});
		BoardRunner runner1 = new BoardRunner(grid1, rule);
		BoardRunner runner2 = new BoardRunner(grid2, rule);
		
		runner1.nextGeneration();
		runner2.nextGeneration();

		assertTrue(runner1.getGrid().equals(nextGen1));
		assertTrue(runner2.getGrid().equals(nextGen2));
	}

	@Test
	public void testNextGenerationWithKleinTopology() {
		
		/*
		 * 0 0 0 0 0 0
		 * 1 1 0 0 0 0
		 * 1 0 0 0 0 0
		 * 1 0 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 */
		Grid grid1 = new Grid(6,6,Grid.Topology.KLEIN);
		grid1.getCell(0,1).incrementState();
		grid1.getCell(1,1).incrementState();
		grid1.getCell(0,2).incrementState();
		grid1.getCell(0,3).incrementState();

		/*
		 * 0 0 0 0 0 0
		 * 1 1 0 0 0 0
		 * 1 0 0 0 0 0
		 * 0 0 0 0 0 1
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 */
		Grid nextGen1 = new Grid(6,6,Grid.Topology.KLEIN);
		nextGen1.getCell(0,1).incrementState();
		nextGen1.getCell(1,1).incrementState();
		nextGen1.getCell(0,2).incrementState();
		nextGen1.getCell(5,3).incrementState();

		/*
		 * 0 1 1 1 0 0
		 * 0 1 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 */
		Grid grid2 = new Grid(6,6,Grid.Topology.KLEIN);
		grid2.getCell(1,0).incrementState();
		grid2.getCell(1,1).incrementState();
		grid2.getCell(2,0).incrementState();
		grid2.getCell(3,0).incrementState();

		/*
		 * 0 1 1 0 0 0
		 * 0 1 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 0 0 0
		 * 0 0 0 1 0 0
		 */
		Grid nextGen2 = new Grid(6,6,Grid.Topology.KLEIN);
		nextGen2.getCell(1,0).incrementState();
		nextGen2.getCell(1,1).incrementState();
		nextGen2.getCell(2,0).incrementState();
		nextGen2.getCell(3,5).incrementState();

		Rule rule = new Rule(new Integer[]{3}, new Integer[]{2,3});
		BoardRunner runner1 = new BoardRunner(grid1, rule);
		BoardRunner runner2 = new BoardRunner(grid2, rule);
		
		runner1.nextGeneration();
		runner2.nextGeneration();

		assertTrue(runner1.getGrid().equals(nextGen1));
		assertTrue(runner2.getGrid().equals(nextGen2));
	}

}
