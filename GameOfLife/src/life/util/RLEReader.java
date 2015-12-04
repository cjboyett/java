package life.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import life.model.Pattern;

/**
 * 
 * @author Casey
 *
 * A class for reading .rle files to load patterns.
 */

// TODO Update to read size and rule.
public class RLEReader
{
	public static int[][] parse(File rlefile)
	{
		int[][] pattern = null;
		try
		{
			ArrayList<String> list = new ArrayList<>();
			Files.lines(rlefile.toPath()).filter(s -> s.length() >= 0 && s.charAt(0) != '#' && s.charAt(0) != 'x').forEach(s -> list.add(s));
			String code = "";
			for (String s : list) code += s;
			if (Pattern.isValidCode(code)) pattern = Pattern.decode(code);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return pattern;
	}
}
