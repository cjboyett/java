package com.cjboyett.dicestatistics.view;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ButtonPane extends GridPane
{
	private Button[] buttons;
	
	public ButtonPane(int sides)
	{
		buttons = new Button[sides];
		
		for (int i=1;i<=sides;i++)
		{
			buttons[i-1] = new Button(i + "");
			buttons[i-1].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//			GridPane.setFillWidth(buttons[i-1], true);
//		buttons[i-1].setOnAction(roller(i-1));
			add(buttons[i-1], (i-1) % (sides / 2), (i-1) / (sides / 2));
			GridPane.setVgrow(buttons[i-1], Priority.ALWAYS);
			GridPane.setHgrow(buttons[i-1], Priority.ALWAYS);
		}
	}
	
	public Button[] getButtons()
	{
		return buttons;
	}
}
