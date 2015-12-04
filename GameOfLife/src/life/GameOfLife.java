package life;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;
import life.model.Grid;
import life.model.Grid.Topology;
import life.model.MooreNeighborhood;
import life.model.Neighborhood;
import life.model.Pattern;
import life.model.Rule;
import life.model.VonNeumannNeighborhood;
import life.model.XNeighborhood;
import life.util.RLEReader;
import life.view.GridView;

/**
 * 
 * @ Author Casey
 * 
 * Main class of this application. 
 */

// TODO Migrate many functions other classes.  This has grown a little too large.
public class GameOfLife extends Application {

	private Grid grid;
	private Rule rule;
	private Neighborhood neighborhood;
	
	private Stage primaryStage;
	private GridView gridPane;
	private Label topologyLabel;
	
	// TODO Add information pane for info such as generation and population
	@Override
	public void start(Stage primaryStage)
	{
		this.primaryStage = primaryStage;

		int width = (int)(Screen.getPrimary().getVisualBounds().getWidth() * 0.9 / 8);
		width -= width % 16;
		int height = (int)(Screen.getPrimary().getVisualBounds().getHeight() * 0.9 / 8);
		height -= height % 16;
		
		rule = new Rule(new Integer[]{3}, new Integer[]{2,3});
		grid = new Grid(width,height);
		setNeighborhood(new MooreNeighborhood(1));

		gridPane = new GridView(grid, rule);
		MenuBar menuBar = makeMenuBar();
		Pane statusBar = makeStatusBar();
		BorderPane pane = new BorderPane();
		pane.setCenter(gridPane);
		pane.setTop(menuBar);
		pane.setBottom(statusBar);
		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
		gridPane.init();
		gridPane.randomize();
		gridPane.bindDXProperty(primaryStage.widthProperty().subtract(scene.widthProperty()));
		gridPane.bindDYProperty(primaryStage.heightProperty().subtract(scene.heightProperty()).add(menuBar.heightProperty()));
		primaryStage.sizeToScene();
		primaryStage.setTitle("Game of Life - " + rule);
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	// Contains a Dialog that requires JDK 8u40.  Comment out if needed.
	private MenuBar makeMenuBar()
	{
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		
		MenuItem resetMenuItem = new MenuItem("Reset");
		resetMenuItem.setOnAction((event) -> gridPane.reset());
		
		MenuItem exitMenuItem = new MenuItem("Exit");
		exitMenuItem.setOnAction((event) -> Platform.exit());
		
		MenuItem randomizeMenuItem = new MenuItem("Randomize");
		randomizeMenuItem.setOnAction((event) -> {
			gridPane.reset();
			gridPane.randomize();
		});
		
		fileMenu.getItems().addAll(resetMenuItem, randomizeMenuItem, exitMenuItem);
		
		Menu editMenu = new Menu("Edit");
		Menu setRuleMenu = new Menu("Set rule");

		// Sample rules to test.
		String[][] rules = {{"B1357/S1357", "Replicator"},
				{"B2/S", "Seeds"},
				{"B25/S4", ""},
				{"B3/S012345678", "Life without Death"},
				{"B3/S23", "Life"},
				{"B34/S34", "34 Life"},
				{"B35678/S5678", "Diamoeba"},
				{"B36/S125", "2x2"},
				{"B36/S23", "High Life"},
				{"B3678/S34678", "Day & Night"},
				{"B368/S245", "Morley"},
				{"B4678/S35678", "Anneal"}};
		for (String[] rule : rules)
		{
			if (isRuleValid(rule[0])) setRuleMenu.getItems().add(makeNewRuleMenu(rule[0], rule[1]));
		}
		
		// Requires JDK 8u40 to compile.  Comment out custom rule menu if needed.
		// Custom rule cannot currently handle rules requiring more than 9 neighbors.
		MenuItem customRuleItem = new MenuItem("Custom rule");
		customRuleItem.setOnAction((event) ->
		{
			TextInputDialog dialog = new TextInputDialog(rule.toString());
			dialog.setTitle("Custom rule");
			dialog.setHeaderText(null);
			dialog.setGraphic(null);
			dialog.setContentText("Format B#/S#");
			Optional<String> result = dialog.showAndWait();
			
			result.ifPresent(rule ->
			{
				if (isRuleValid(rule))
				{
					this.rule = parseRule(rule);
					gridPane.setRule(this.rule);
				}
				else
				{
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText(null);
					alert.setContentText(rule + " does not follow the pattern B#/S#.");
					alert.show();
				}
			});
		});
		setRuleMenu.getItems().add(customRuleItem);
		
		// Fun for a little chaos.  One of my favourite rules was found this way: B028/S235678
		MenuItem randomRuleItem = new MenuItem("Random rule");
		randomRuleItem.setOnAction((event) ->
		{
			int neighborhoodSize = neighborhood.neighborhoodSize();
			List<Integer> birth = new ArrayList<>();
			List<Integer> alive = new ArrayList<>();
			Random r = new Random();
			for (int i=0;i<=neighborhoodSize;i++)
			{
				if (r.nextDouble() > 0.5) birth.add(i);
				if (r.nextDouble() > 0.5) alive.add(i);
			}
			rule = new Rule(birth.toArray(new Integer[birth.size()]), alive.toArray(new Integer[alive.size()]));
			gridPane.setRule(rule);
		});
		setRuleMenu.getItems().add(randomRuleItem);
		
		Menu setPatternMenu = new Menu("Patterns");
		
		// A few sample patterns to test.
		String[][] patterns = {{"bo$2bo$3o!", "Glider"},
				{"24bo$22bobo$12b2o6b2o12b2o$11bo3bo4b2o12b2o$2o8bo5bo3b2o$2o8bo3bob2o4bobo$10bo5bo7bo$11bo3bo$12b2o!","Glider gun"},
				{"3b3o12b$3bo2b3o9b$4bobo11b$2o7bo8b$obo4bo2bo7b$o8b2o7b$b2o15b$bo2bo5bo" +
					"b2o4b$bo9b2obo3b$3bobo6b2o2bob$4b2obo4b2o3bo$8bo7bob$7b4o3bobob$7bob2o" +
					"3b4o$8bo3b2obo2b$13b2o3b$9bob3o4b$10bo2bo!", "Big glider"},
				{"3o10b$o9b2ob$bo6b3obo$3b2o2b2o4b$4bo8b$8bo4b$4b2o3bo3b$3bobob2o4b$3bobo2bob2ob$2bo4b2o4b$2b2o9b$2b2o!", "Canada Goose"},
				{"8b2o3b$7b2o4b$9bo3b$11b2o$10bo2b2$9bo2bo$b2o5b2o3b$2o5bo5b$2bo4bobo3b$4b2o2bo4b$4b2o!", "Crab"},
				{"5b2o5b$6bo5b$4bo7b$2obob4o3b$2obo5bobo$3bo2b3ob2o$3bo4bo3b$4b3obo3b$7bo4b$6bo5b$6b2o!", "Rats"},
				{"5b2o7bo4b$4bo8bobo3b$7bo5bobo3b$3bo3bo4b2obob2o$4o3bo7b2obo$5bo5bob2o" +
					"4b$14bo4b$11b3o5b$8bobo8b$6bo3bob2o5b$4b3obobob2o5b$3bo4b2o9b$4b4o11b$" +
					"7bo11b$6bo12b$6b2o!", "44P7.2"},
				{"10bo19b2o17b$9bobo17bo4b2o13b$2bo4b3obo5bo14bob3o12b$bobo2bo4b2o3bobo" +
					"7bobob4o3bo3b2o2b2obo$bobo2bob2o3bo3bo8b3obo8bo2b2o4bo$2ob2obobob2o8bo" +
					"10bo4b2ob2o4b2obo$bo4b2o4b2o3bob2o13bo7bo6b$bob2o2bo3bobob2o2bob2ob3o" +
					"4bobo9bo5b$2bo3b3obobo2bo4bo3b3o4bobo9bo5b$3b2o3bobo2bo5b2o13bo7bo6b$" +
					"5b2obob2obobo3bo11bo4b2ob2o4b2obo$5bob2obo2bob2o4bo4b3obo8bo2b2o4bo$" +
					"11b2o7b2o4bobob4o3bo3b2o2b2obo$32bob3o12b$29bo4b2o13b$30b2o!", "Wickstretcher 1"}};
		for (String[] pattern : patterns)
		{
			if (Pattern.isValidCode(pattern[0]))
			{
				setPatternMenu.getItems().add(makeNewPatternMenu(pattern[0], pattern[1]));
			}
		}
		
		MenuItem loadPatternFileMenuItem = new MenuItem("Load from file");
		loadPatternFileMenuItem.setOnAction((event) -> readRLEFile());
		setPatternMenu.getItems().add(loadPatternFileMenuItem);
		
		// Menu for changing the topology of the grid
		Menu setTopologyMenu = new Menu("Topology");
		for (Grid.Topology t : Grid.Topology.values()) setTopologyMenu.getItems().add(makeTopologyMenu(t));
		
		// Neighborhood menu needs some polishing.  It was added as an afterthought.
		Menu setNeighborhoodMenu = new Menu("Neighborhood");
		MenuItem[] mooreNeighborhoodMenuItem = new MenuItem[3];
		for (int i=1;i<=3;i++)
		{
			mooreNeighborhoodMenuItem[i-1] = new MenuItem("Moore " + i);
			setNeighborhoodMenu.getItems().add(mooreNeighborhoodMenuItem[i-1]);
		}
		mooreNeighborhoodMenuItem[0].setOnAction((event) -> setNeighborhood(new MooreNeighborhood(1)));
		mooreNeighborhoodMenuItem[1].setOnAction((event) -> setNeighborhood(new MooreNeighborhood(2)));
		mooreNeighborhoodMenuItem[2].setOnAction((event) -> setNeighborhood(new MooreNeighborhood(3)));

		MenuItem[] vonNeumannNeighborhoodMenuItem = new MenuItem[3];
		for (int i=1;i<=3;i++)
		{
			vonNeumannNeighborhoodMenuItem[i-1] = new MenuItem("Von Neumann " + i);
			setNeighborhoodMenu.getItems().add(vonNeumannNeighborhoodMenuItem[i-1]);
		}
		vonNeumannNeighborhoodMenuItem[0].setOnAction((event) -> setNeighborhood(new VonNeumannNeighborhood(1)));
		vonNeumannNeighborhoodMenuItem[1].setOnAction((event) -> setNeighborhood(new VonNeumannNeighborhood(2)));
		vonNeumannNeighborhoodMenuItem[2].setOnAction((event) -> setNeighborhood(new VonNeumannNeighborhood(3)));

		MenuItem[] xNeighborhoodMenuItem = new MenuItem[3];
		for (int i=1;i<=3;i++)
		{
			xNeighborhoodMenuItem[i-1] = new MenuItem("X " + i);
			setNeighborhoodMenu.getItems().add(xNeighborhoodMenuItem[i-1]);
		}
		xNeighborhoodMenuItem[0].setOnAction((event) -> setNeighborhood(new XNeighborhood(1)));
		xNeighborhoodMenuItem[1].setOnAction((event) -> setNeighborhood(new XNeighborhood(2)));
		xNeighborhoodMenuItem[2].setOnAction((event) -> setNeighborhood(new XNeighborhood(3)));
		
		editMenu.getItems().addAll(setRuleMenu, setPatternMenu, setTopologyMenu, setNeighborhoodMenu);

		menuBar.getMenus().addAll(fileMenu, editMenu);
		
		return menuBar;
	}
	
	private void setNeighborhood(Neighborhood neighborhood)
	{
		this.neighborhood = neighborhood;
		grid.setNeighborhood(neighborhood);
	}

	private MenuItem makeTopologyMenu(Topology t)
	{
		String s = t.toString().charAt(0) + t.toString().substring(1).toLowerCase();
		MenuItem menuItem = new MenuItem(s);
		menuItem.setOnAction((event) ->
		{
			grid.setTopology(t);
			topologyLabel.setText("Topology: " + s);
		});
		return menuItem;
	}

	private boolean isRuleValid(String rule)
	{
		return rule.matches("B\\d*/S\\d*");
	}
	
	// TODO Fix to allow more diverse neighborhoods
	private Rule parseRule(String rule)
	{
		Integer birthRule[];
		Integer deathRule[];
		String birthString = rule.substring(1,rule.indexOf("/"));
		birthRule = new Integer[birthString.length()];
		for (int i=0;i<birthString.length();i++) birthRule[i] = Integer.parseInt(Character.toString(birthString.charAt(i)));

		String deathString = rule.substring(rule.indexOf("S")+1);
		deathRule = new Integer[deathString.length()];
		for (int i=0;i<deathString.length();i++) deathRule[i] = Integer.parseInt(Character.toString(deathString.charAt(i)));
		return new Rule(birthRule, deathRule);
	}
	
	private void readRLEFile()
	{
		File file;
		FileChooser chooser = new FileChooser();
		if (getSavedFilePath() != null) chooser.setInitialDirectory(getSavedFilePath().getParentFile());
		chooser.getExtensionFilters().addAll(new ExtensionFilter("RLE file", "*.rle"));
		chooser.setTitle("Choose directory containing source files");
		file = chooser.showOpenDialog(primaryStage);
		if (file != null)
		{
			setSavedFilePath(file);
			int[][] pattern = RLEReader.parse(file);
			if (pattern != null) gridPane.setPattern(pattern);
		}
	}
	
	private File getSavedFilePath()
	{
		Preferences prefs = Preferences.userNodeForPackage(GameOfLife.class);
		String filePath = prefs.get("filePath", null);
		if (filePath != null) return new File(filePath);
		else return null;
	}
	
	private void setSavedFilePath(File file)
	{
		Preferences prefs = Preferences.userNodeForPackage(GameOfLife.class);
		if (file != null) prefs.put("filePath", file.getPath());
		else prefs.remove("filePath");
	}
	
	private MenuItem makeNewRuleMenu(String rule, String title)
	{
		MenuItem menuItem = new MenuItem(title + " (" + rule + ")");
		menuItem.setOnAction((event) ->
		{
			this.rule = parseRule(rule);
			gridPane.setRule(this.rule);	
		});
		return menuItem;
	}

	private MenuItem makeNewPatternMenu(String code, String title)
	{
		MenuItem menuItem = new MenuItem(title);
		menuItem.setOnAction((event) -> gridPane.setPattern(Pattern.decode(code)));
		return menuItem;
	}
	
	// The status bar contains various controls/information.
	// TODO Refactor to its own class.
	private Pane makeStatusBar()
	{
		AnchorPane statusBar = new AnchorPane();
		topologyLabel = new Label("Topology: " + grid.getTopology().toString().charAt(0) + grid.getTopology().toString().substring(1).toLowerCase());
		HBox topologyBox = new HBox(topologyLabel);
		topologyBox.setAlignment(Pos.CENTER);
		statusBar.getChildren().add(topologyBox);
		AnchorPane.setLeftAnchor(topologyBox, 0.0);
		AnchorPane.setBottomAnchor(topologyBox, 0.0);
		
		Slider speedSlider = new Slider(0, 2, 0.5);
		gridPane.bindSpeedProperty(speedSlider.valueProperty());
		HBox speedBox = new HBox(new Label("Speed: "), speedSlider);
		speedBox.setAlignment(Pos.CENTER);
		statusBar.getChildren().add(speedBox);
		AnchorPane.setRightAnchor(speedBox, 0.0);
		AnchorPane.setBottomAnchor(speedBox, 0.0);
		
		Slider zoomSlider = new Slider(0, 5, 2);
		zoomSlider.setMajorTickUnit(1.0);
		zoomSlider.setMinorTickCount(0);
		zoomSlider.setSnapToTicks(true);
		gridPane.bindZoomProperty(zoomSlider.valueProperty());
		HBox zoomBox = new HBox(new Label("Zoom: "), zoomSlider);
		zoomBox.setAlignment(Pos.CENTER);
		statusBar.getChildren().add(zoomBox);
		
		primaryStage.widthProperty().addListener((o, oldVal, newVal) ->
		{
			AnchorPane.setLeftAnchor(zoomBox, (newVal.doubleValue()-zoomBox.getWidth())/2);
		});
		
		return statusBar;
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}

}
