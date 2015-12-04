package life.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Casey
 *
 * A pattern is a specific configuration of cells.  They are encoded
 * using the rle format.
 */

public class Pattern
{
	public static int[][] decode(String code)
	{
		int[][] pattern;
		code = code.replace("!", "");
		code = code.replace("bo", "b1o");
		code = code.replace("ob", "o1b");
		String[] rows = code.split("\\$");
		int width = 0;
		List<Integer[]> decodedRows = new ArrayList<>();

		for (String row : rows)
		{
			Integer[] decodedRow = decodeRow(row);
			decodedRows.add(decodedRow);
			if (row.charAt(row.length()-1) != 'o' && row.charAt(row.length()-1) != 'b')
			{
				String[] numerals = row.split("b")[row.split("b").length-1].split("o");
				for (int i=0;i<Integer.parseInt(numerals[numerals.length-1])-1;i++) decodedRows.add(new Integer[]{});
			}
			width = Math.max(width, decodedRow.length);
		}
		
		pattern = new int[decodedRows.size()][width];
		for (int i=0;i<decodedRows.size();i++)
		{
			Integer[] decodedRow = decodedRows.get(i);
			for (int j=0;j<decodedRow.length;j++) pattern[i][j] = decodedRow[j];
		}
				
		return pattern;
	}
	
	private static Integer[] decodeRow(String row)
	{
		List<Integer[]> counts = new ArrayList<>();
		
		String bits[] = row.split("b");
		for (int i=0;i<bits.length;i++)
		{
			if (bits[i].matches("\\d+"))
			{
				counts.add(new Integer[]{Integer.parseInt(bits[i]), 0});
			}
			else if (bits[i].matches("o\\d+"))
			{
				counts.add(new Integer[]{1, 1});
				counts.add(new Integer[]{Integer.parseInt(bits[i].substring(1)), 0});
			}
			else if (bits[i].matches("\\d+o"))
			{
				counts.add(new Integer[]{Integer.parseInt(bits[i].substring(0,bits[i].length()-1)), 1});				
			}
			else if (bits[i].matches("\\d+o\\d+"))
			{
				counts.add(new Integer[]{Integer.parseInt(bits[i].substring(0,bits[i].indexOf("o"))), 1});				
				counts.add(new Integer[]{Integer.parseInt(bits[i].substring(bits[i].indexOf("o")+1)), 0});				
			}
			else
			{
				counts.add(new Integer[]{1, 0});
			}
		}

		List<Integer> toArray = new ArrayList<>();
		for (Integer[] ints : counts)
		{
			for (int i=0;i<ints[0];i++) toArray.add(ints[1]);
		}
		return toArray.toArray(new Integer[toArray.size()]);
	}

	public static boolean isValidCode(String code)
	{
		return code.matches("(\\d*(b*o*)+\\$*)*\\d*(b*o*)+!");
	}
}
