package com.cjboyett.dicestatistics.controller;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

import com.cjboyett.dicestatistics.DiceStatistics;

public class StatusBarController
{
	@FXML
	private ProgressIndicator pi1;
	@FXML
	private ProgressIndicator pi2;
	@FXML
	private ProgressIndicator pi3;
	@FXML
	private ProgressIndicator pi4;
	
	@FXML
	private Button analysisButton;
	
	@FXML
	private Label label;
	
	private DiceStatistics mainApp;
	private int sides;
	private Map<Integer,Number> maxValues, idealValues;
	private Map<Integer,Number> maxValues2, idealValues2;
	
	final static private double SIG90 = 1.22;
	final static private double SIG95 = 1.36;
	
//	private final double[] confidence = {7.63272964757, 8.56703542127, 9.20044069539, 9.69828221073, 10.1170130639, 10.4832938941, 10.8120101038, 11.1123897394, 11.3905824312, 11.6509100321, 11.8965364897, 12.1298544505, 12.3527217792, 12.5666132056, 12.7727213829, 12.9720264497, 13.165345248, 13.353366985, 13.5366796139, 13.7157897063, 13.8911376679, 14.0631095579, 14.2320463912, 14.3982515465, 14.5619967314, 14.7235268325, 14.8830638969, 15.0408104283, 15.1969521386, 15.3516602626, 15.5050935204, 15.6573997919, 15.8087175576, 15.9591771456, 16.1089018193, 16.2580087334, 16.4066097787, 16.5548123367, 16.7027199571, 16.8504329724, 16.9980490597, 17.1456637593, 17.2933709585, 17.441263347, 17.5894328507, 17.7379710491, 17.8869695819, 18.0365205498, 18.1867169138, 18.3376528968, 18.4894243943, 18.6421293964, 18.7958684273, 18.950745007, 19.1068661404, 19.2643428408, 19.4232906938, 19.5838304684, 19.7460887861, 19.9101988556, 20.0763012858, 20.2445449896, 20.4150881951, 20.5880995821, 20.7637595649, 20.9422617497, 21.1238145957, 21.3086433208, 21.4969920952, 21.689126583, 21.8853368997, 22.085941074, 22.2912891233, 22.5017678801, 22.7178067442, 22.9398845868, 23.1685380955, 23.4043719423, 23.6480712767, 23.9004172184, 24.1623062609, 24.434774844, 24.7190308456, 25.0164944847, 25.3288522425, 25.6581291341, 26.0067874092, 26.3778642536, 26.7751686628, 27.2035710294, 27.6694435778, 28.1813574669, 28.7512409097, 29.3964226865, 30.1435272056, 31.0367029026, 32.1577203728, 33.6874250713, 36.1908691293};

	@FXML
	public void initialize()
	{
		maxValues = new HashMap<>();
		idealValues = new HashMap<>();
		maxValues2 = new HashMap<>();
		idealValues2 = new HashMap<>();
		
		maxValues.put(4, 8);
		maxValues.put(6, 11);
		maxValues.put(8, 14);
		maxValues.put(10, 17);
		maxValues.put(12, 20);
		maxValues.put(20, 30);
		
		idealValues.put(4, 7.5);
		idealValues.put(6, 8);
		idealValues.put(8, 10.5);
		idealValues.put(10, 13);
		idealValues.put(12, 15.5);
		idealValues.put(20, 25);
		
		maxValues2.put(4, 1.45);
		maxValues2.put(6, 1.5);
		maxValues2.put(8, 1.56);
		maxValues2.put(10, 1.6);
		maxValues2.put(12, 1.61);
		maxValues2.put(20, 1.67);
		
		idealValues2.put(4, 1.1);
		idealValues2.put(6, 1.2);
		idealValues2.put(8, 1.25);
		idealValues2.put(10, 1.27);
		idealValues2.put(12, 1.3);
		idealValues2.put(20, 1.35);
	}
	
	public void setMainApp(DiceStatistics mainApp)
	{
		this.mainApp = mainApp;
	}
	
	public void setSides(int sides)
	{
		this.sides = sides;
	}
		
	public void bindValues(DoubleProperty total)
	{
		pi1.progressProperty().unbind();
		pi1.progressProperty().setValue(0);
		pi2.progressProperty().unbind();
		pi2.progressProperty().setValue(0);
		pi3.progressProperty().unbind();
		pi3.progressProperty().setValue(0);
		pi4.progressProperty().unbind();
		pi4.progressProperty().setValue(0);
		
		pi1.progressProperty().bind(total.divide(sides * 5));
		pi1.progressProperty().addListener((o, oldVal, newVal) -> 
		{
			if (!pi2.progressProperty().isBound() && pi1.getProgress() >= 1)
			{
				pi2.progressProperty().bind(total.subtract(sides * 5).divide(sides * 5));
				analysisButton.setDisable(false);
			}
			if (pi1.getProgress() < 1) analysisButton.setDisable(true);
		});
		pi2.progressProperty().addListener((o, oldVal, newVal) -> 
		{
			if (!pi3.progressProperty().isBound() && pi2.getProgress() >= 1) pi3.progressProperty().bind(total.subtract(sides * 10).divide(sides * 15));
			if (pi2.progressProperty().isBound() && pi2.isIndeterminate())
			{
				pi2.progressProperty().unbind();
				pi2.progressProperty().setValue(0);
			}
		});
		pi3.progressProperty().addListener((o, oldVal, newVal) -> 
		{
			if (!pi4.progressProperty().isBound() && pi3.getProgress() >= 1) pi4.progressProperty().bind(total.subtract(sides * 25).divide(sides * 25));
			if (pi3.progressProperty().isBound() && pi3.isIndeterminate())
			{
				pi3.progressProperty().unbind();
				pi3.progressProperty().setValue(0);
			}
		});
		pi4.progressProperty().addListener((o, oldVal, newVal) -> 
		{
			if (pi4.progressProperty().isBound() && pi4.isIndeterminate())
			{
				pi4.progressProperty().unbind();
				pi4.progressProperty().setValue(0);
			}
		});

//		if (pi1.getProgress() < 1) analysisButton.setDisable(true);
	}
	
	@FXML
	private void analyzeDie()
	{
		double d = Math.round(mainApp.getDTotal() * 10000) / 10000.0;
		double total = mainApp.getTotal();
		if (mainApp.isFairnessSelected()) label.setText("For fairness, your dice score is " + d + ":  Less than " + maxValues.get(sides) + " is good.  Less than " + idealValues.get(sides) + " is ideal.");
		else label.setText("For playability, your dice score is " + d + ":  Less than " + Math.round(maxValues2.get(sides).doubleValue() * Math.sqrt(total) / SIG95) + " is good.  Less than " + Math.round(idealValues2.get(sides).doubleValue() * Math.sqrt(total) / SIG90) + " is ideal.");
	}

}
