package com.cjboyett.dicestatistics.view;

import javafx.beans.property.IntegerProperty;

public class SetValueWindow
{
	private int value;
	private IntegerProperty ip;
	
	public SetValueWindow(IntegerProperty ip)
	{
		this.ip = ip;
		value = ip.get();
	}
	
	private void setValue()
	{
		ip.set(value);
	}
}
