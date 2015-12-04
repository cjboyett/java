package com.cjboyett.dicestatistics;

import java.util.Random;

public class BiasedDie
{
	private int[] weights;
	private int totalWeight;
	private Random r;
	
	public BiasedDie(int... weights)
	{
		this.weights = weights;
		totalWeight = 0;
		for (int w : weights)
		{
			if (w > 0) totalWeight += w;
		}
		System.out.println(totalWeight);
		r = new Random();
	}
	
	public int roll()
	{
		int x = r.nextInt(totalWeight)+1;
		int index = 0;
		while (x > weights[index])
		{
//			System.out.println(x + " " + index + " " + weights[index]);
			x -= weights[index++];
		}
		return index;
	}

}
