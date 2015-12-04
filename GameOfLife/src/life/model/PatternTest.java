package life.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class PatternTest {

	@Test
	public void testDecode() {
		String gliderCode = "bo$2bo$3o!";
		int[][] gliderGridFromCode = Pattern.decode(gliderCode);

		/*
		 * 010
		 * 001
		 * 111
		 */
		int[][] gliderGrid = new int[3][3];
		gliderGrid[0][1] = 1;
		gliderGrid[1][2] = 1;
		gliderGrid[2][0] = 1;
		gliderGrid[2][1] = 1;
		gliderGrid[2][2] = 1;
				
		for (int i=0;i<3;i++)
		{
			for (int j=0;j<3;j++)
			{
				assertTrue(gliderGridFromCode[i][j] == gliderGrid[i][j]);
			}
		}
	}

	@Test
	public void testIsValidCode() {
		String gliderCode = "bo$2bo$3o!";
		String gliderFailCode1 = "bo$2bo$3o";
		String gliderFailCode2 = "po$2po$3o!";
		String garbageCode = "garbage";
		
		assertTrue(Pattern.isValidCode(gliderCode));
		assertFalse(Pattern.isValidCode(gliderFailCode1));
		assertFalse(Pattern.isValidCode(gliderFailCode2));
		assertFalse(Pattern.isValidCode(garbageCode));
	}

}
