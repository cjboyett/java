package com.cjboyett.dicestatistics.controller;

import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;

import com.cjboyett.dicestatistics.DiceStatistics;

public class DiceRollController
{
	@FXML
	private StackedBarChart<String, Integer> barChart;
	@FXML
	private CategoryAxis xAxis;
	
//	private int sides;
	private ObservableList<String> diceSides;
	private XYChart.Series<String, Integer> series;
	
	private DiceStatistics mainApp;
	
	public void setMainApp(DiceStatistics app)
	{
		mainApp = app;
	}
	
	public void setSides(int sides)
	{
//		this.sides = sides > 0 ? sides : 0;
		diceSides = FXCollections.observableArrayList();
		
		for (int i=1;i<=sides;i++) diceSides.add(i + "");
		xAxis.setCategories(diceSides);
		xAxis.setLabel("Die Roll");
		
		series = new XYChart.Series<>();
		for (int i=1;i<=sides;i++) series.getData().add(new XYChart.Data<>(i+"",0));
		
		barChart.getData().add(series);
	}
	
	public void bindValues(List<IntegerProperty> values)
	{
		int i=0;
		for (IntegerProperty ip : values)
		{
			series.getData().get(i).YValueProperty().bind(ip.asObject());
			Tooltip tooltip = new Tooltip();
			tooltip.textProperty().bind(ip.asString());
			Tooltip.install(series.getData().get(i).getNode(), tooltip);
			series.getData().get(i).getNode().setOnMouseClicked(event -> setValue(ip));
			i++;
		}
	}
	
	private void setValue(IntegerProperty ip)
	{
		ip.set(20);
		mainApp.updateTotal(20);
	}

}
