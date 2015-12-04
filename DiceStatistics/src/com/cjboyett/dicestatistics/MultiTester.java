package com.cjboyett.dicestatistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import com.cjboyett.dicestatistics.model.DiceRolls;
import com.cjboyett.dicestatistics.math.FairnessTester;
import com.cjboyett.dicestatistics.math.PlayabilityTester;

public class MultiTester
{
	public static void run(List<File> files)
	{
		if (files != null && !files.isEmpty())
		{
			try
			{
				List<DiceRolls> rolls = new ArrayList<>();
				for (File file : files)
				{
					FileInputStream fileIn = new FileInputStream(file);
					ObjectInputStream objIn = new ObjectInputStream(fileIn);
					rolls.add((DiceRolls)objIn.readObject());
					objIn.close();
				}
				
				int sides = rolls.get(0).getRolls().size();
				
				for (DiceRolls r : rolls)
				{
					if (r.getRolls().size() != sides) return;
				}
				
				List<IntegerProperty> values = new ArrayList<>();
				
				for (int i=0;i<sides;i++) values.add(new SimpleIntegerProperty(0));
				
				for (DiceRolls r : rolls)
				{
					for (int i=0;i<sides;i++)
					{
						values.get(i).setValue(values.get(i).getValue() + r.getRolls().get(i));
					}
				}
				
				System.out.println(FairnessTester.test(values));
				System.out.println(PlayabilityTester.test(values));

				
				FileOutputStream fileOut = new FileOutputStream(new File("temp.d20"));
				ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
				objOut.writeObject(new DiceRolls(values));
				objOut.close();
				fileOut.close();
			}
			catch (IOException | ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}
}
