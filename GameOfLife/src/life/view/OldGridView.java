package life.view;

import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import life.model.Cell;
import life.model.Grid;
import life.model.Rule;
import life.util.BoardRunner;

/**
 * 
 * @author Casey
 *
 * Deprecated class for drawing grids.
 */
public class OldGridView extends GridPane
{
	private Grid grid;
	private CellView cellViews[][];
	private EventHandler<MouseEvent> mouseEventHandler;
	private BoardRunner runner;
	
	private Timeline timeline;
	private double timelineRate;
	private boolean playing;
	private int currentState;
	private int[][] pattern;
	
	private final int totalRows = 96 * 4;
	private int totalCells = totalRows * totalRows;
	private final int centralCellsToMake = 500;
	private final int outerCellsToMake = 50;
//	private int zoomLevel = 2;
	private int xOffset, yOffset;
//	private boolean cellAdded[][] = new boolean[totalRows][totalRows];
	
	private DoubleProperty dxProperty, dyProperty, cellSizeProperty;
	
	public OldGridView(Grid grid, Rule rule)
	{
		super();
		this.grid = grid;
		this.runner = new BoardRunner(grid, rule);
		cellViews = new CellView[totalRows][totalRows];

		timelineRate = 1;
		playing = true;
		dxProperty = new SimpleDoubleProperty(0);
		dyProperty = new SimpleDoubleProperty(0);
//TODO Can show grid with #.5 and Math.ceil() for drawing
		cellSizeProperty = new SimpleDoubleProperty(8);
		
//		setStyle("-fx-background-color: #FFFFFF");

	}
	
	public void init()
	{
		setOffsets();
		Stage primaryStage = (Stage)getScene().getWindow();		
		mouseEventHandler = new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				CellView source = (CellView)event.getSource();
				if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED))
				{
					if (event.isControlDown())
					{
						int x = (int)Math.floor((event.getScreenX()-primaryStage.getX()-dxProperty.get()) / cellSizeProperty.get());
						int y = (int)Math.floor((event.getScreenY()-primaryStage.getY()-dyProperty.get()) / cellSizeProperty.get());
						for (int i=0;i<pattern.length;i++)
						{
							for (int j=0;j<pattern[0].length;j++)
							{
								try
								{
									if (cellViews[x+j][y+i].getState() != pattern[i][j]) cellViews[x+j][y+i].incrementState();
								}
								catch (ArrayIndexOutOfBoundsException e){}
							}
						}
					}
					else
					{
						source.incrementState();
						currentState = source.getState();
					}
				}
				else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED))
				{
					int x = (int)Math.floor((event.getScreenX()-primaryStage.getX()-dxProperty.get()) / cellSizeProperty.get());
					int y = (int)Math.floor((event.getScreenY()-primaryStage.getY()-dyProperty.get()) / cellSizeProperty.get());
					if (cellViews[x][y].getState() != currentState) cellViews[x][y].incrementState();
				}
			}
		
		};

		getChildren().clear();
		for (int i=0;i<grid.getWidth();i++)
		{
			for (int j=0;j<grid.getHeight();j++)
			{
				CellView cellView = null;
				if ((i <= 1 || j <= 1) || (grid.getCell(i, j).getState() != 0))
				{
					cellView = new CellView(grid.getCell(i,j));
					cellView.widthProperty().bind(cellSizeProperty);
					cellView.heightProperty().bind(cellSizeProperty);
					cellView.addEventHandler(MouseEvent.ANY, mouseEventHandler);
					add(cellView, i, j);
				}
				cellViews[i+xOffset][j+yOffset] = cellView;
				totalCells--;
			}
		}
		timeline = new Timeline(
				new KeyFrame(new Duration(100), ((event) -> updateGrid())));
		timeline.setCycleCount(Timeline.INDEFINITE);
		
		getScene().setOnKeyPressed((keyEvent) -> 
		{
			if (keyEvent.getCode().equals(KeyCode.SPACE))
			{
				if (playing) timeline.pause();
				else timeline.play();
				playing = !playing;
			}
			else if (keyEvent.getCode().equals(KeyCode.MINUS))
			{
				if (keyEvent.isControlDown() && cellSizeProperty.get() > 2) zoomOut();
				else if (playing && timeline.getRate() > 0.03125) timeline.setRate(timeline.getRate()/2);
			}
			else if (keyEvent.getCode().equals(KeyCode.EQUALS))
			{
				if (keyEvent.isControlDown() && cellSizeProperty.get() < 64) zoomIn();
				else if (playing && timeline.getRate() < 16) timeline.setRate(timeline.getRate()*2);
			}
		});
		timeline.setRate(timelineRate);
		
		if (playing) timeline.play();
		((Stage)getScene().getWindow()).sizeToScene();
	}
	
	private void updateGrid()
	{
		runner.nextGeneration();
		if (totalCells > 0)
		{
			int cellsAdded = 0;
			cellsAdded += addLiveCellViews();
			if (cellsAdded < centralCellsToMake) cellsAdded += addCentralCellViews(cellsAdded);
			if (cellsAdded < outerCellsToMake) cellsAdded += addOuterCellViews(cellsAdded);
			
//			System.out.print(totalCells);
			totalCells -= cellsAdded;
//			System.out.println(" - " + cellsAdded + " = " + totalCells);
		}
	}

	private int addLiveCellViews()
	{
//		System.out.println("Live");
		int cellsAdded = 0;
		for (int i=0;i<grid.getWidth();i++)
		{
			for (int j=0;j<grid.getHeight();j++)
			{
				if (grid.getCell(i, j).getState() != 0)
				{
					CellView cellView = new CellView(grid.getCell(i,j));
					cellViews[i+xOffset][j+yOffset] = cellView;
					cellView.widthProperty().bind(cellSizeProperty);
					cellView.heightProperty().bind(cellSizeProperty);
					cellView.addEventHandler(MouseEvent.ANY, mouseEventHandler);
					add(cellView, i, j);
					cellsAdded++;
				}
			}
		}
		return cellsAdded;
	}
	
	private int addCentralCellViews(int cellsAdded)
	{
//		System.out.println("Central");
		for (int i=0;i<grid.getWidth();i++)
		{
			for (int j=0;j<grid.getHeight();j++)
			{
				if (cellViews[i+xOffset][j+yOffset] == null)
				{
					CellView cellView = new CellView(grid.getCell(i,j));
					cellViews[i+xOffset][j+yOffset] = cellView;
					cellView.widthProperty().bind(cellSizeProperty);
					cellView.heightProperty().bind(cellSizeProperty);
					cellView.addEventHandler(MouseEvent.ANY, mouseEventHandler);
					add(cellView, i, j);
					cellsAdded++;
				}
				if (cellsAdded >= centralCellsToMake)
				{
//					System.out.println("central dong");
					i = grid.getWidth();
					j = grid.getHeight();
				}
			}
		}
		return cellsAdded;
	}

	private int addOuterCellViews(int cellsAdded)
	{
//		System.out.println("Outer");
		for (int i=0;i<totalRows;i++)
		{
			for (int j=0;j<totalRows;j++)
			{
				if (cellViews[i][j] == null)
				{
					CellView cellView = new CellView(new Cell(grid, 0));//grid.getCell(i,j));
					cellViews[i][j] = cellView;
					cellView.widthProperty().bind(cellSizeProperty);
					cellView.heightProperty().bind(cellSizeProperty);
					cellView.addEventHandler(MouseEvent.ANY, mouseEventHandler);
					add(cellView, i, j);
					cellsAdded++;
				}
				if (cellsAdded >= outerCellsToMake)
				{
					i = totalRows;
					j = totalRows;
				}
			}
		}
		return cellsAdded;
	}

	public void reset()
	{
		for (int i=0;i<grid.getWidth();i++)
		{
			for (int j=0;j<grid.getHeight();j++)
			{
				if (cellViews[i][j] != null) 
				{
						while (cellViews[i][j].getState() != 0) cellViews[i][j].incrementState();
				}
			}
		}
	}
	
	public void randomize()
	{
		Random r = new Random();
		for (int i=0;i<grid.getWidth() * grid.getHeight() / 4;i++)
		{
			grid.getCell(r.nextInt(grid.getWidth()), r.nextInt(grid.getHeight())).incrementState();
		}
	}
	
	public void setRule(Rule rule)
	{
		runner.setRule(rule);
		((Stage)getScene().getWindow()).setTitle("Game of Life - " + rule);
	}

	public void setPattern(int[][] pattern)
	{
		this.pattern = pattern;
	}
	
	public void zoomOut()
	{
		int width = grid.getWidth();
		int height = grid.getHeight();
		grid.padLeft(width/2);
		grid.padRight(width/2);
		grid.padTop(height/2);
		grid.padBottom(height/2);
		cellSizeProperty.set(cellSizeProperty.get()/2);

		timelineRate = timeline.getRate();
		init();
		runner.setGrid(grid);
	}
	
	public void zoomIn()
	{
		int width = grid.getWidth();
		int height = grid.getHeight();
		grid.cropLeft(width/4);
		grid.cropRight(width/4);
		grid.cropTop(height/4);
		grid.cropBottom(height/4);
		cellSizeProperty.set(cellSizeProperty.get() * 2);

		timelineRate = timeline.getRate();
		init();
		runner.setGrid(grid);
	}
	
	public void setOffsets()
	{
		xOffset = (totalRows - grid.getWidth()) / 2;
		yOffset = (totalRows - grid.getHeight()) / 2;
	}
	
	public void bindDXProperty(DoubleBinding widthProperty)
	{
		dxProperty.bind(widthProperty);
	}

	public void bindDYProperty(DoubleBinding heightProperty)
	{
		dyProperty.bind(heightProperty);
	}

}
