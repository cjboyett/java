package com.cjboyett.dicestatistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class Tester2
{
	private int sides, runs, rolls;
	private double mean, deviation, max, min;
	private double[] values;
	
	private final Random r;
	
	public Tester2(int sides, int rolls, int runs)
	{
		this.sides = sides;
		this.runs = runs;
		this.rolls = rolls;
		values = new double[runs];
		r = new Random();
	}
	
	public void test()
	{
		for (int i=0;i<runs;i++) simulate(i);
		double temp = 0;
		Arrays.sort(values);
		for (int i=0;i<runs;i++) temp += values[i];
		mean = temp / runs;

		temp = 0;
		for (int i=0;i<runs;i++) temp += Math.pow(values[i] - mean, 2);
		deviation = Math.sqrt(temp / (runs - 1));
		
		min = values[0];
		max = values[0];
		
		for (int i=0;i<runs;i++)
		{
			if (values[i] < min) min = values[i];
			if (values[i] > max) max = values[i];
		}
		
//		System.out.println(Arrays.toString(values));
		System.out.println("With " + runs + " runs of " + rolls + " rolls on a d" + sides +", we get\n"
				+ "Mean: " + mean + "\n"
				+ "Standard deviation: " + deviation + "\n"
				+ "Minimum: " + min + "\n"
				+ "Maximum: " + max + "\n"
				+ "90% at " + (1.22 / Math.sqrt(rolls) * values[(int)(0.90 * runs)]) + "\n"
				+ "95% at " + (1.36 / Math.sqrt(rolls) * values[(int)(0.95 * runs)]) + "\n");
		
		makeBarChart();
	}
	
	private void simulate(int i)
	{
		int results[] = new int[sides];
		for (int j=0;j<rolls;j++)
		{
			int roll = r.nextInt(sides);
			for (int k=0;k<=roll;k++) results[k]++;
		}
		values[i] = calculateStat(results);
	}
	
	private double calculateStat(int[] results)
	{
		double d = 0;
		double expectedValue = 0;
		for (int i=0;i<sides;i++)
		{
			expectedValue = rolls * (double)(sides - i) / sides;
//			System.out.println(expectedValue);
			d = Math.max(d, Math.abs(results[i] - expectedValue));
//			System.out.println(d);
		}
		return d;
	}

	private void makeBarChart()
	{
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();
		BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
//		chart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		xAxis.setLabel("d-value");
		yAxis.setLabel("Times");
		
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		
		Map<Double, Integer> valuesCount = new HashMap<>();
		for (int i=0;i<values.length;i++)
		{
			if (!valuesCount.containsKey(values[i])) valuesCount.put(values[i], 0);
			valuesCount.put(values[i], valuesCount.get(values[i]) + 1);
		}
		
		List<Double> sortedKeys = new ArrayList<>(valuesCount.keySet());
		Collections.sort(sortedKeys);
		for (Double d : sortedKeys)
		{
			series.getData().add(new XYChart.Data<String, Number>(d.toString(), valuesCount.get(d)));
		}
				
		Stage stage = new Stage();
		Scene scene = new Scene(chart, 800, 600);
		chart.getData().add(series);
		stage.setScene(scene);
		stage.show();
	}
	
}
