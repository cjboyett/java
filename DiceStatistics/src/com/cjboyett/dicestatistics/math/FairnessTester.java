package com.cjboyett.dicestatistics.math;

import java.util.List;

import javafx.beans.property.IntegerProperty;

public class FairnessTester
{
	public static double test(List<IntegerProperty> values)
	{
		double d = 0;
		int rolls = 0;
		for (IntegerProperty ip : values) rolls += ip.get();
		double expectedValue = (double)rolls/(double)values.size();
		for (int i=0;i<values.size();i++) d += Math.pow(values.get(i).get() - expectedValue, 2) / expectedValue;
		return d;
	}
}
