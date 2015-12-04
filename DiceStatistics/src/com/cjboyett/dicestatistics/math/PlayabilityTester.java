package com.cjboyett.dicestatistics.math;

import java.util.List;

import javafx.beans.property.IntegerProperty;

public class PlayabilityTester
{
	public static double test(List<IntegerProperty> values)
	{
		double d = 0;
		double expectedValue = 0;
		int sides = values.size();
		int rolls = 0;
		for (IntegerProperty ip : values) rolls += ip.getValue();
		int total = rolls;
		for (int i=0;i<sides;i++)
		{
//			System.out.println(i + " " + values.get(i).getValue());
			expectedValue = rolls * (double)(sides - i) / (double)sides;
			d = Math.max(d, Math.abs(total - expectedValue));
//			System.out.println(expectedValue + " " + total + " " + d);
			total -= values.get(i).getValue();
		}
		return d;
	}
}
