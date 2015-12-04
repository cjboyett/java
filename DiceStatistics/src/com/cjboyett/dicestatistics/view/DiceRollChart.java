package com.cjboyett.dicestatistics.view;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.cjboyett.dicestatistics.controller.DiceRollController;
import com.cjboyett.dicestatistics.controller.StatusBarController;

public class DiceRollChart
{
	private Stage stage;
	private Scene scene;
	private BorderPane pane;
		
	private StatusBarController statusBarController;
	
	private DiceRollController controller;
	
	private int sides;
	
	public DiceRollChart(int sides)
	{
		this.sides = sides;

		stage = new Stage();
		stage.setTitle("Dice Roll Chart");
		
		pane = new BorderPane();
		
		scene = new Scene(pane);
		stage.setScene(scene);
		stage.setOnCloseRequest((event) -> Platform.exit());
		
		loadChart();
		loadStatusBar();
	}
	
	public void loadChart()
	{
		try
		{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("DiceRollChart.fxml"));
			AnchorPane chartPane = (AnchorPane)loader.load();
			pane.setCenter(chartPane);
			controller = (DiceRollController)loader.getController();
			controller.setSides(sides);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void loadStatusBar()
	{
		try
		{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("StatusBar.fxml"));
			pane.setBottom((AnchorPane)loader.load());
			statusBarController = (StatusBarController)loader.getController();
//			controller = (DiceRollController)loader.getController();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}		
	}
	
	public void setSides(int sides)
	{
		this.sides = sides;
	}
	
	public void show()
	{
		stage.show();
	}
	
	public void setTitle(String title)
	{
		stage.setTitle(title);
	}
	
	public DiceRollController getController()
	{
		return controller;
	}

	public StatusBarController getStatusBarController()
	{
		return statusBarController;
	}
	
	public BorderPane getPane()
	{
		return pane;
	}
	
}
