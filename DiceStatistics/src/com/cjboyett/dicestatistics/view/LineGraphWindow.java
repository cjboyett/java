package com.cjboyett.dicestatistics.view;

import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.controlsfx.control.RangeSlider;

public class LineGraphWindow
{
	private LineChart<Number,Number> lineGraph;
	private NumberAxis xAxis, yAxis;
	private XYChart.Series<Number, Number> fairData, playData;
	private XYChart.Series<Number, Number> fairAvgData, playAvgData;
	private XYChart.Series<Number, Number> fairDiffData, playDiffData;
	private Stage stage;
	private Scene scene;
	private Pane pane;
	
	private RangeSlider xSlider, ySlider;
	
	private int count = 0;
	private final static int AVERAGE_NUMBER = 100;
	
	public LineGraphWindow()
	{
		stage = new Stage();
		pane = new AnchorPane();
		
		xAxis = new NumberAxis();
		yAxis = new NumberAxis();
		lineGraph = new LineChart<>(xAxis, yAxis);
		
//		fairData = new XYChart.Series<>();
//		fairData.setName("Fairness");
		playData = new XYChart.Series<>();
		playData.setName("Playability");

//		fairAvgData = new XYChart.Series<>();
//		fairAvgData.setName("Fairness average");
		playAvgData = new XYChart.Series<>();
		playAvgData.setName("Playability average");

//		fairDiffData = new XYChart.Series<>();
//		fairDiffData.setName("Fairness average");
		playDiffData = new XYChart.Series<>();
		playDiffData.setName("Playability average difference");

		lineGraph.getData().addAll(playAvgData, playDiffData);
		lineGraph.setCreateSymbols(false);

		xSlider = new RangeSlider(0, 1, 0, 1);
		ySlider = new RangeSlider(0, 1, 0, 1);
		ySlider.setOrientation(Orientation.VERTICAL);
		
		pane.getChildren().addAll(lineGraph, xSlider, ySlider);

//		AnchorPane.setBottomAnchor(xSlider, 0.0);
//		AnchorPane.setLeftAnchor(xSlider, 0.0);
//		AnchorPane.setRightAnchor(xSlider, 0.0);
				
		xAxis.setAutoRanging(false);
		xAxis.lowerBoundProperty().bind(xSlider.lowValueProperty());
		xAxis.upperBoundProperty().bind(xSlider.highValueProperty());
		xAxis.setMinorTickLength(0);
		xAxis.tickUnitProperty().bind(xSlider.highValueProperty().subtract(xSlider.lowValueProperty()).divide(10));

		yAxis.setAutoRanging(false);
		yAxis.lowerBoundProperty().bind(ySlider.lowValueProperty());
		yAxis.upperBoundProperty().bind(ySlider.highValueProperty());
		yAxis.setMinorTickLength(0);
		yAxis.tickUnitProperty().bind(ySlider.highValueProperty().subtract(ySlider.lowValueProperty()).divide(10));

		scene = new Scene(pane, 800, 600);
		stage.setScene(scene);
		stage.show();
		
		AnchorPane.setBottomAnchor(lineGraph, xSlider.getHeight());
		AnchorPane.setTopAnchor(lineGraph, 0.0);
		AnchorPane.setLeftAnchor(lineGraph, ySlider.getWidth());
		AnchorPane.setRightAnchor(lineGraph, 0.0);
		
		AnchorPane.setBottomAnchor(xSlider, 0.0);
		AnchorPane.setLeftAnchor(xSlider, ySlider.getWidth());
		AnchorPane.setRightAnchor(xSlider, 0.0);

		AnchorPane.setBottomAnchor(ySlider, xSlider.getHeight());
		AnchorPane.setLeftAnchor(ySlider, 0.0);
		AnchorPane.setTopAnchor(ySlider, 0.0);
	}
	
	public void addFairData(int rolls, double value)
	{
		fairData.getData().add(new XYChart.Data(rolls, value));
		if (fairData.getData().size() > 1)
		{
			int i = fairData.getData().size()-1;
			double x1 = fairData.getData().get(i).getXValue().doubleValue();
			double x2 = fairData.getData().get(i-1).getXValue().doubleValue();

			double y1 = fairData.getData().get(i).getYValue().doubleValue();
			double y2 = fairData.getData().get(i-1).getYValue().doubleValue();
			
			fairAvgData.getData().add(new XYChart.Data((x1+x2)/2, (y1+y2)/2));
		}

		if (value > ySlider.getMax())
		{
			ySlider.setMax(Math.ceil(value / 10) * 10);
			ySlider.setHighValue(ySlider.getMax());
		}
//		Tooltip tooltip = new Tooltip();
//		tooltip.textProperty().set("(" + rolls + ", " + value + ")");;
//		Tooltip.install(fairData.getData().get(rolls-1).getNode(), tooltip);
	}

	public void addPlayData(int rolls, double value)
	{
		if (rolls >= 100 && value > 25) count++;
		if (rolls >= 100 && rolls % 100 == 1) System.out.println(rolls + ": " + 100 * (1 - (double)count/(rolls-100)));
		playData.getData().add(new XYChart.Data(rolls, value));		

		if (playData.getData().size() >= AVERAGE_NUMBER)
		{
			int i = playData.getData().size()-1;
			double x = 0;
			double y = 0;
			
			for (int j=0;j<AVERAGE_NUMBER;j++)
			{
				x += playData.getData().get(i-j).getXValue().doubleValue();
				y += playData.getData().get(i-j).getYValue().doubleValue();
			}
//			double x1 = playData.getData().get(i).getXValue().doubleValue();
//			double x2 = playData.getData().get(i-1).getXValue().doubleValue();
//
//			double y1 = playData.getData().get(i).getYValue().doubleValue();
//			double y2 = playData.getData().get(i-1).getYValue().doubleValue();
			
			playAvgData.getData().add(new XYChart.Data(x / AVERAGE_NUMBER, y / AVERAGE_NUMBER));
		}
		
		if (playAvgData.getData().size() > 1)
		{
			int i = playAvgData.getData().size()-1;
			double x1, x2;
			double y1, y2;
			
			x1 = playAvgData.getData().get(i).getXValue().doubleValue();
			y1 = playAvgData.getData().get(i).getYValue().doubleValue();
			x2 = playAvgData.getData().get(i-1).getXValue().doubleValue();
			y2 = playAvgData.getData().get(i-1).getYValue().doubleValue();

			playDiffData.getData().add(new XYChart.Data((x1+x2) / 2, AVERAGE_NUMBER * Math.abs(y1-y2)));
		}

		if (value > ySlider.getMax())
		{
			ySlider.setMax(Math.ceil(value / 10) * 10);
			ySlider.setHighValue(ySlider.getMax());
		}

		if (rolls > xSlider.getMax())
		{
			xSlider.setMax(Math.ceil(rolls / 10) * 10);
			xSlider.setHighValue(xSlider.getMax());
		}

//		Tooltip tooltip = new Tooltip();
//		tooltip.textProperty().set("(" + rolls + ", " + value + ")");;
//		Tooltip.install(playData.getData().get(rolls-1).getNode(), tooltip);
	}
}
