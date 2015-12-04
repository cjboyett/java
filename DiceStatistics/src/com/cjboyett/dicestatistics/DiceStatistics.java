package com.cjboyett.dicestatistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cjboyett.dicestatistics.math.FairnessTester;
import com.cjboyett.dicestatistics.math.PlayabilityTester;
import com.cjboyett.dicestatistics.model.DiceRolls;
import com.cjboyett.dicestatistics.view.ButtonPane;
import com.cjboyett.dicestatistics.view.DiceRollChart;
import com.cjboyett.dicestatistics.view.LineGraphWindow;

public class DiceStatistics extends Application
{
	private int SIDES = 20;

	private Stage stage;
	
	private DiceRollChart diceChart;
	private List<IntegerProperty> values;
	private List<DoubleProperty> adjustedValues, dValues;
	private DoubleProperty total;
	private DoubleProperty expectedValue, dTotal;
	
	private LineGraphWindow lineGraphWindow;
	
	private MenuItem undo;
	private RadioMenuItem dMenu, dMenu2;
	private RadioMenuItem d4menu, d6menu, d8menu, d10menu, d12menu, d20menu;
	private Stack<Integer> rolls;
	
	private File currentFile = null;
	private boolean fileChanged = false;
	private boolean adjustedValuesSet = false;
	
	@Override
	public void start(Stage primaryStage)
	{
		stage = primaryStage;
		
		diceChart = new DiceRollChart(SIDES);
		
		values = new ArrayList<>();
		adjustedValues = new ArrayList<>();
		dValues = new ArrayList<>();
		total = new SimpleDoubleProperty(0);
		expectedValue = new SimpleDoubleProperty(0);
		dTotal = new SimpleDoubleProperty(0);
		
		lineGraphWindow = new LineGraphWindow();
		
		rolls = new Stack<>();
		
		ButtonPane pane = new ButtonPane(SIDES);
//		Button[] buttons = new Button[SIDES];

		for (int i=1;i<=SIDES;i++)
		{
			values.add(new SimpleIntegerProperty(0));
			adjustedValues.add(new SimpleDoubleProperty(0));
			dValues.add(new SimpleDoubleProperty(0));
			pane.getButtons()[i-1].setOnAction(roller(i-1));
		}

		diceChart.getController().setMainApp(this);
		
		diceChart.getController().bindValues(values);
		diceChart.getStatusBarController().setSides(SIDES);
		diceChart.getStatusBarController().setMainApp(this);
		diceChart.getStatusBarController().bindValues(total);
		
		diceChart.show();
		
		MenuBar menuBar = new MenuBar();
		Menu file = new Menu("File");
		
		MenuItem save = new MenuItem("Save rolls");
		save.setOnAction((event) -> saveRolls());
		
		MenuItem load = new MenuItem("Load rolls");
		load.setOnAction((event) -> loadRolls());
		
		Menu exportAs = new Menu("Export as...");

		MenuItem exportAsExcel = new MenuItem("Excel");
		exportAsExcel.setOnAction((event) -> exportToExcel());
		MenuItem exportAsCSV = new MenuItem("CSV");
		exportAsCSV.setOnAction((event) -> exportToCSV());
		
		exportAs.getItems().addAll(exportAsExcel, exportAsCSV);
		
		undo = new MenuItem("Undo last roll");
		undo.setOnAction((event) -> undoLastRoll());
		undo.setDisable(true);
		
		MenuItem reset = new MenuItem("Reset");
		reset.setOnAction((event) -> reset());

		MenuItem close = new MenuItem("Close");
		close.setOnAction((event) -> Platform.exit());

		file.getItems().addAll(save, load, exportAs, undo, reset, close);
		
		Menu diceMenu = new Menu("Set die");
		
		d4menu = new RadioMenuItem("d4");
		d6menu = new RadioMenuItem("d6");
		d8menu = new RadioMenuItem("d8");
		d10menu = new RadioMenuItem("d10");
		d12menu = new RadioMenuItem("d12");
		d20menu = new RadioMenuItem("d20");
		ToggleGroup diceGroup = new ToggleGroup();
		
		d4menu.setToggleGroup(diceGroup);
		d4menu.setOnAction((event) -> {SIDES = 4; reset();});

		d6menu.setToggleGroup(diceGroup);
		d6menu.setOnAction((event) -> {SIDES = 6; reset();});

		d8menu.setToggleGroup(diceGroup);
		d8menu.setOnAction((event) -> {SIDES = 8; reset();});

		d10menu.setToggleGroup(diceGroup);
		d10menu.setOnAction((event) -> {SIDES = 10; reset();});

		d12menu.setToggleGroup(diceGroup);
		d12menu.setOnAction((event) -> {SIDES = 12; reset();});

		d20menu.setToggleGroup(diceGroup);
		d20menu.setOnAction((event) -> {SIDES = 20; reset();});
		d20menu.setSelected(true);
		
		diceMenu.getItems().addAll(d4menu, d6menu, d8menu, d10menu, d12menu, d20menu);
		
		Menu metricMenu = new Menu("Set metric");
		
		dMenu = new RadioMenuItem("Fairness");
		dMenu2 = new RadioMenuItem("Playability");
		ToggleGroup metricGroup = new ToggleGroup();
		
		dMenu.setToggleGroup(metricGroup);
		dMenu2.setToggleGroup(metricGroup);
		dMenu.setSelected(true);
		
		metricMenu.getItems().addAll(dMenu, dMenu2);
		
		Menu testMenu = new Menu("Test");
		MenuItem multiTestMenuItem = new MenuItem("Analyze multiple rolls");
		testMenu.getItems().add(multiTestMenuItem);
		
		multiTestMenuItem.setOnAction((event) -> MultiTester.run(getFiles()));
		
		menuBar.getMenus().addAll(file, diceMenu, metricMenu, testMenu);
		
		diceChart.getPane().setTop(menuBar);

		Scene scene = new Scene(pane, 40 * SIDES / 2, 80);

		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest((event) -> Platform.exit());
		primaryStage.setTitle("Rolls");
		
		primaryStage.show();
		
//		Tester tester = new Tester(SIDES, 50 * SIDES, 250000);
//		tester.test();
//
//		Tester2 tester2 = new Tester2(SIDES, 5 * SIDES, 250000);
//		tester2.test();

		Random r = new Random();
//		BiasedDie die = new BiasedDie(24,13,16,22,21,27,17,25,22,21,14,28,21,22,17,32,15,20,11,12);
//		BiasedDie die = new BiasedDie(30,21,25,25,25,33,20,31,26,29,18,33,31,26,21,33,17,25,15,16);
//		BiasedDie die = new BiasedDie(33,26,33,27,29,38,24,36,31,33,22,38,35,29,24,41,27,33,20,21);
///		BiasedDie die = new BiasedDie(33,26,33,27,29,38,24,36,31,33,22,38,35,29,24,41,27,33,20,21);
//		BiasedDie die = new BiasedDie(39,36,41,34,37,47,34,47,45,44,33,55,44,40,30,57,43,39,24,31);
//		BiasedDie die = new BiasedDie(44,43,47,39,42,56,37,52,52,48,39,62,49,40,35,63,49,43,26,34);
//		BiasedDie die = new BiasedDie(50,50,54,43,45,57,38,54,55,56,45,68,55,44,38,73,57,48,31,39);
//		BiasedDie die = new BiasedDie(10,10,10,10,10,9,11,10,10,10,11,10,10,9,10,10,10,10,10,10);
		
		for (int i=0;i<10;i++)
		{
//			int blah = die.roll();
//			System.out.println(blah + "\n");
			int blah = r.nextInt(20);
//			int blah = r.nextInt(3 * SIDES + 1) % SIDES;
			values.get(blah).setValue(values.get(blah).getValue() + 1);
			total.set(total.getValue() + 1);

//			lineGraphWindow.addFairData(total.intValue(), FairnessTester.test(values));
			lineGraphWindow.addPlayData(total.intValue(), 30.0 * PlayabilityTester.test(values) / (1.23 * Math.sqrt(total.get())));
		}
		
//		for (int i=0;i<500000;i++)
//		{
//			int blah = r.nextInt(20 * SIDES + 1) % SIDES;
//			values.get(blah).setValue(values.get(blah).getValue() + 1);
//			total.set(total.getValue() + 1);
//			updateStatistics();
//		}
	}
	
	private List<File> getFiles()
	{
		List<File> files;
		FileChooser chooser = new FileChooser();
		if (getPersonFilePath() != null) chooser.setInitialDirectory(getPersonFilePath().getParentFile());
		chooser.getExtensionFilters().add(new ExtensionFilter("D20 Rolls", "*.d20"));
		chooser.setTitle("Save your rolls");
		files = chooser.showOpenMultipleDialog(stage);
		return files;
	}

	private EventHandler<ActionEvent> roller(int i)
	{
		return new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				values.get(i).setValue(values.get(i).getValue()+1);
				updateTotal(1);
				updateStatistics();
				
				rolls.push(i);
				undo.setDisable(false);
				
				lineGraphWindow.addFairData(total.intValue(), FairnessTester.test(values));
				lineGraphWindow.addPlayData(total.intValue(), 30.0 * PlayabilityTester.test(values) / (1.23 * Math.sqrt(total.get())));
			}
		};
	}

	private void updateStatistics()
	{
		if (!adjustedValuesSet && total.getValue() >= 1)
		{
			updateTotal(0);
			for (int i=1;i<=SIDES;i++)
			{
				adjustedValues.get(i-1).bind(values.get(i-1).subtract(total.divide(new SimpleDoubleProperty(SIDES))));
				dValues.get(i-1).bind(adjustedValues.get(i-1).multiply(adjustedValues.get(i-1)));
			}
			expectedValue.bind(total.divide(new SimpleDoubleProperty(SIDES)));
			adjustedValuesSet = true;
		}
		
		double dSum = 0;
		
		for (int i=0;i<SIDES;i++)
		{
			dSum += dValues.get(i).getValue();
		}
		
		dTotal.set(dSum / expectedValue.getValue());
	}
	
	public void updateTotal(int dx)
	{
		total.set(total.get() + dx);
	}
	
	public double getTotal()
	{
		return total.getValue();
	}
	
	public double getDTotal()
	{
		if (dMenu.isSelected()) return dTotal.getValue();
		else return calculateDTotal2();
	}
	
	private double calculateDTotal2()
	{
		int results[] = new int[SIDES];
		for (int i=0;i<SIDES;i++)
		{
			int value = values.get(i).getValue();
			for (int k=0;k<=i;k++) results[k] += value;
		}
		return calculateStat(results);
	}
	
	private double calculateStat(int[] results)
	{
		double d = 0;
		double expectedValue = 0;
		for (int i=0;i<SIDES;i++)
		{
			expectedValue = total.getValue() * (double)(SIDES - i) / (double)SIDES;
//			System.out.println(expectedValue);
			d = Math.max(d, Math.abs(results[i] - expectedValue));
//			System.out.println(d);
		}
		return d;
	}
	
	public boolean isFairnessSelected()
	{
		return dMenu.isSelected();
	}

	private File getFile(boolean save)
	{
		File file;
		FileChooser chooser = new FileChooser();
		if (getPersonFilePath() != null) chooser.setInitialDirectory(getPersonFilePath().getParentFile());
		chooser.getExtensionFilters().add(new ExtensionFilter("D20 Rolls", "*.d20"));
		if (save)
		{
			chooser.setTitle("Save your rolls");
			file = chooser.showSaveDialog(stage);
		}
		else
		{
			chooser.setTitle("Load your rolls");
			file = chooser.showOpenDialog(stage);
		}
		if (file != null)
		{
			fileChanged = true;
			return file;
		}
		else
		{
			fileChanged = false;
			return currentFile;
		}
	}
	
	public void saveRolls()
	{
		currentFile = getFile(true);
		
		if (fileChanged)
		{
			ArrayList<Integer> rolls = new ArrayList<>();
			for (int i=0;i<SIDES;i++) rolls.add(values.get(i).getValue());
			
			try
			{
				if (currentFile != null)
				{
					setPersonFilePath(currentFile);
					diceChart.setTitle("Dice Roll Chart - " + currentFile.getName().substring(0, currentFile.getName().indexOf(".d20")));
				}
				
				FileOutputStream fileOut = new FileOutputStream(currentFile);
				ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
				objOut.writeObject(new DiceRolls(rolls));
				objOut.close();
				fileOut.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void loadRolls()
	{
		currentFile = getFile(false);
		
		if (fileChanged)
		{
			DiceRolls oldRolls = null;
			try
			{
				if (currentFile != null)
				{
					setPersonFilePath(currentFile);
				}
				
				FileInputStream fileIn = new FileInputStream(currentFile);
				ObjectInputStream objIn = new ObjectInputStream(fileIn);
				oldRolls = (DiceRolls)objIn.readObject();
				
				SIDES = oldRolls.getRolls().size();
				reset();
				diceChart.setTitle("Dice Roll Chart - " + currentFile.getName().substring(0, currentFile.getName().indexOf(".d20")));
				
				values = new ArrayList<>();
							
				for (int i=0;i<SIDES;i++)
				{
					values.add(new SimpleIntegerProperty(0));
					int j = oldRolls.getRolls().get(i);
					values.get(i).setValue(j);
					total.setValue(total.getValue() + j);
				}
				diceChart.getController().bindValues(values);
				updateStatistics();
								
				switch(SIDES)
				{
				case 4:
					d4menu.setSelected(true);
					break;
				case 6:
					d6menu.setSelected(true);
					break;
				case 8:
					d8menu.setSelected(true);
					break;
				case 10:
					d10menu.setSelected(true);
					break;
				case 12:
					d12menu.setSelected(true);
					break;
				case 20:
					d20menu.setSelected(true);
					break;
				default:
					break;
				}
				
				objIn.close();
				fileIn.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void exportToExcel()
	{
/*
		try
		{
			File file;
			FileChooser chooser = new FileChooser();
			if (getPersonFilePath() != null) chooser.setInitialDirectory(getPersonFilePath().getParentFile());
			if (fileType.equals("Excel")) chooser.getExtensionFilters().add(new ExtensionFilter("Excel Spreadsheet", "*.xls"));
			else if (fileType.equals("CSV")) chooser.getExtensionFilters().add(new ExtensionFilter("CSV", "*.csv"));
			else chooser.getExtensionFilters().add(new ExtensionFilter("All", "*.*"));
			chooser.setTitle("Save your rolls");
			file = chooser.showSaveDialog(stage);
	
			WritableWorkbook workbook = Workbook.createWorkbook(file);
			WritableSheet sheet = workbook.createSheet("Dice rolls", 0);
			
			sheet.addCell(new Label(0, 0, "Face"));
			sheet.addCell(new Label(1, 0, "Frequency"));
			
			for (int i=1; i<=SIDES; i++)
			{
				sheet.addCell(new Number(0, i, i));
				sheet.addCell(new Number(1, i, values.get(i-1).getValue()));
			}
			
			sheet.addCell(new Label(3, 0, "Total rolls"));
			sheet.addCell(new Formula(4, 0, "SUM(B2:B" + (SIDES+1) + ")"));
			
			sheet.addCell(new Label(3, 1, "Fairness"));
			for (int i=1;i<=SIDES;i++)
			{
				sheet.addCell(new Formula(9, i, "B" + (i+1) + "-E1/" + SIDES + ""));
				sheet.addCell(new Formula(10, i, "J" + (i+1) + "*J" + (i+1) + ""));
			}
			sheet.addCell(new Formula(4, 1, "SUM(K2:K" + (SIDES+1) + ")/(E1/" + SIDES + ")"));
			
			sheet.addCell(new Label(3, 2, "Playability"));
			for (int i=1;i<=SIDES;i++)
			{
				sheet.addCell(new Formula(6, i, "SUM(B" + (i+1) + ":B" + (SIDES+1) + ")"));
				sheet.addCell(new Formula(7, i, "ABS(G" + (i+1) + " - E1 * " + (SIDES-i+1) + "/" + SIDES + ")"));
			}
			sheet.addCell(new Formula(4, 2, "MAX(H2:H" + (SIDES+1) + ")"));
			
//			WritableCellFormat format = new WritableCellFormat();
//			format.setLocked(true);
//			((WritableCell)sheet.getCell(7, 1)).setCellFormat(format);
//			((WritableCell)sheet.getCell(7, 2)).setCellFormat(format);
//			((WritableCell)sheet.getCell(7, 3)).setCellFormat(format);
//			((WritableCell)sheet.getCell(7, 4)).setCellFormat(format);
			
			workbook.write();
			workbook.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
*/		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();

		Row[] rows = new Row[SIDES + 1];
		for (int i=0; i<=SIDES; i++) rows[i] = sheet.createRow(i);
		
		for (Row r : rows)
		{
			for (int i=0; i<=10; i++) r.createCell(i);
		}
		
		rows[0].getCell(0).setCellValue("Face");
		rows[0].getCell(1).setCellValue("Frequency");
		
		for (int i=1; i<=SIDES; i++)
		{
			rows[i].getCell(0).setCellValue(i);
			rows[i].getCell(1).setCellValue(values.get(i-1).getValue());
		}

		rows[0].getCell(3).setCellValue("Total");
		rows[0].getCell(4).setCellFormula("SUM(B2:B" + (SIDES+1) + ")");
		
		rows[1].getCell(3).setCellValue("Fairness");
		for (int i=1;i<=SIDES;i++)
		{
			rows[i].getCell(9).setCellFormula("B" + (i+1) + "-E1/" + SIDES + "");
			rows[i].getCell(10).setCellFormula("J" + (i+1) + "*J" + (i+1) + "");
		}
		rows[1].getCell(4).setCellFormula("SUM(K2:K" + (SIDES+1) + ")/(E1/" + SIDES + ")");

		rows[2].getCell(3).setCellValue("Playability");
		for (int i=1;i<=SIDES;i++)
		{
			rows[i].getCell(6).setCellFormula("SUM(B" + (i+1) + ":B" + (SIDES+1) + ")");
			rows[i].getCell(7).setCellFormula("ABS(G" + (i+1) + " - E1 * " + (SIDES-i+1) + "/" + SIDES + ")");
		}
		rows[2].getCell(4).setCellFormula("MAX(H2:H" + (SIDES+1) + ")");

		try
		{
			File file;
			FileChooser chooser = new FileChooser();
			if (getPersonFilePath() != null) chooser.setInitialDirectory(getPersonFilePath().getParentFile());
			chooser.getExtensionFilters().add(new ExtensionFilter("Excel Spreadsheet", "*.xlsx"));
//			else if (fileType.equals("CSV")) chooser.getExtensionFilters().add(new ExtensionFilter("CSV", "*.csv"));
			chooser.setTitle("Save your rolls");
			file = chooser.showSaveDialog(stage);
			FileOutputStream fout = new FileOutputStream(file);
			workbook.write(fout);
			workbook.close();
			fout.flush();
			fout.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void exportToCSV()
	{
		try
		{
			File file;
			FileChooser chooser = new FileChooser();
			if (getPersonFilePath() != null) chooser.setInitialDirectory(getPersonFilePath().getParentFile());
			chooser.getExtensionFilters().add(new ExtensionFilter("CSV", "*.csv"));
			chooser.setTitle("Save your rolls");
			file = chooser.showSaveDialog(stage);
			FileOutputStream fout = new FileOutputStream(file);
			fout.write("\"Face\",\"Frequency\"\n".getBytes());
			for (int i=1;i<=SIDES;i++)
			{
				String toFile = "" + i + "," + values.get(i-1).get() + "\n";
				fout.write(toFile.getBytes());
			}
			fout.flush();
			fout.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public File getPersonFilePath()
	{
		Preferences prefs = Preferences.userNodeForPackage(DiceStatistics.class);
		String filePath = prefs.get("filePath", null);
		if (filePath != null) return new File(filePath);
		else return null;
	}
	
	public void setPersonFilePath(File file)
	{
		Preferences prefs = Preferences.userNodeForPackage(DiceStatistics.class);
		if (file != null) prefs.put("filePath", file.getPath());
		else prefs.remove("filePath");
	}
	
	private void reset()
	{
		diceChart.setSides(SIDES);
		diceChart.loadChart();
		diceChart.getStatusBarController().setSides(SIDES);
		adjustedValuesSet = false;
									
		ButtonPane pane = new ButtonPane(SIDES);
		for (int i=0;i<SIDES;i++) pane.getButtons()[i].setOnAction(roller(i));
		stage.setScene(new Scene(pane, 40 * SIDES / 2, 80));

		values = new ArrayList<>();
		total.setValue(0);
		for (int i=0;i<SIDES;i++)
		{
			values.add(new SimpleIntegerProperty(0));
		}
		diceChart.getController().bindValues(values);
		diceChart.getStatusBarController().bindValues(total);
		diceChart.setTitle("Dice Roll Chart");
		updateStatistics();
		
		rolls = new Stack<>();
	}
	
	private void undoLastRoll()
	{
		if (!rolls.empty())
		{
			int roll = rolls.pop();
			values.get(roll).setValue(values.get(roll).getValue()-1);
			total.setValue(total.getValue()-1);
			updateStatistics();
			if (rolls.empty()) undo.setDisable(true);
		}
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}

}
