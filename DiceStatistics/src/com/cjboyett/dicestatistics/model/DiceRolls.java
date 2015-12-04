package com.cjboyett.dicestatistics.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.IntegerProperty;

public class DiceRolls implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<Integer> rolls;
	
	public DiceRolls(ArrayList<Integer> rolls)
	{
		this.rolls = rolls;
	}
	
	public DiceRolls(List<IntegerProperty> values)
	{
		rolls = new ArrayList<>();
		for (IntegerProperty ip : values) rolls.add(ip.getValue());
	}
	
	public ArrayList<Integer> getRolls()
	{
		return rolls;
	}
	
}
