package life.view;

import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import life.model.Grid;
import life.model.Rule;
import life.util.BoardRunner;

/**
 * 
 * @author Casey
 *
 * View that handles most aspects of drawing and controlling a Grid.
 */

public class GridView extends Group
{
	private Grid grid;
	private BoardRunner runner;
	
	private WritableImage gridImage;
	private PixelWriter pixelWriter;
	private Canvas canvas;
	private GraphicsContext gc;
	private int cellSize;
	
	private Timeline timeline;
	private int currentState;
	private int[][] pattern;
	private int zoomLevel;

	private EventHandler<MouseEvent> mouseEventHandler;
	private boolean mousePressed;
	
	private DoubleProperty dxProperty, dyProperty;

	public GridView(Grid grid, Rule rule)
	{
		setGrid(grid);
		runner = new BoardRunner(grid, rule);
		
		dxProperty = new SimpleDoubleProperty(0);
		dyProperty = new SimpleDoubleProperty(0);
		cellSize = 8;
		zoomLevel = 2;
		
		gridImage = new WritableImage(cellSize * grid.getWidth(), cellSize * grid.getHeight());
		pixelWriter = gridImage.getPixelWriter();
		
		canvas = new Canvas(cellSize * grid.getWidth(), cellSize * grid.getHeight());
		gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.TRANSPARENT);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		getChildren().add(canvas);
		
		timeline = new Timeline(
				new KeyFrame(new Duration(150), ((event) -> updateGrid())));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}
	
	// init function is used to make sure the parent stage/window have already been created and shown.
	// TODO Add highlight function for pattern
	public void init()
	{
		drawGrid();
		gc.drawImage(gridImage, 0, 0);
		
		Stage primaryStage = (Stage)getScene().getWindow();
		mousePressed = false;
		mouseEventHandler = new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				canvas.requestFocus();
				int x = (int)Math.floor((event.getScreenX()-primaryStage.getX()-dxProperty.get()) / cellSize);
				int y = (int)Math.floor((event.getScreenY()-primaryStage.getY()-dyProperty.get()) / cellSize);
				
				if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED))
				{
					if (event.isControlDown())
					{
						for (int i=0;i<pattern.length;i++)
						{
							for (int j=0;j<pattern[0].length;j++)
							{
								try
								{
									if (grid.getCell(x+j,y+i).getState() != pattern[i][j])
									{
										grid.getCell(x+j,y+i).incrementState();
										drawCell(x+j,y+i);
									}
								}
								catch (ArrayIndexOutOfBoundsException e){}
							}
						}
						gc.drawImage(gridImage, 0, 0);
					}
					else if (!mousePressed)
					{
						mousePressed = true;
						grid.getCell(x, y).incrementState();
						currentState = grid.getCell(x, y).getState();
						drawCell(x,y);
						gc.drawImage(gridImage, 0, 0);
					}
				}
				else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED))
				{
					if (grid.getCell(x, y).getState() != currentState)
					{
						grid.getCell(x, y).incrementState();
						drawCell(x,y);
						gc.drawImage(gridImage, 0, 0);
					}
				}
				else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) mousePressed = false;
			}
		};
		
		canvas.addEventHandler(MouseEvent.ANY, mouseEventHandler);
		
		canvas.setOnKeyPressed((event) ->
		{
			if (event.getCode().equals(KeyCode.RIGHT))
			{
				updateGrid();
			}
		});
	}
	
	private void updateGrid()
	{
		canvas.requestFocus();
		runner.nextGeneration();
		int[][] differences = runner.differences();
		for (int i=0;i<grid.getWidth();i++)
		{
			for (int j=0;j<grid.getHeight();j++)
			{
				if (differences[i][j] != 0)	drawCell(i,j);
			}
		}
		gc.drawImage(gridImage, 0, 0);
	}

	public void reset()
	{
		for (int i=0;i<grid.getWidth();i++)
		{
			for (int j=0;j<grid.getHeight();j++)
			{
				if (grid.getCell(i, j).getState() != 0)
				{
					grid.getCell(i, j).incrementState();
					drawCell(i,j);
				}
			}
		}
		gc.drawImage(gridImage, 0, 0);
//		init();
	}
	
	// TODO Change to allow different levels of randomness.
	public void randomize()
	{
		Random r = new Random();
		for (int i=0;i<grid.getWidth() * grid.getHeight() / 4;i++)
		{
			int x = r.nextInt(grid.getWidth());
			int y = r.nextInt(grid.getHeight());
			grid.getCell(x,y).incrementState();
			drawCell(x,y);
		}
		gc.drawImage(gridImage, 0, 0);
	}
	
	public void setGrid(Grid grid)
	{
		this.grid = grid;
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
		timeline.pause();
		int width = grid.getWidth();
		int height = grid.getHeight();
		grid.pad(height/2, width/2, height/2, width/2);
		cellSize /= 2;

		runner.setGrid(grid);
		drawGrid();
		gc.drawImage(gridImage, 0, 0);
		timeline.play();
	}
	
	public void zoomIn()
	{
		timeline.pause();
		int width = grid.getWidth();
		int height = grid.getHeight();
		grid.crop(height/4, width/4, height/4, width/4);
		cellSize *= 2;
		
		runner.setGrid(grid);
		drawGrid();
		gc.drawImage(gridImage, 0, 0);
		timeline.play();
	}

	// Zooming currently only allows rigid control at fixed levels.
	private void zoom(double newLevel)
	{
		if (Math.abs(newLevel - Math.round(newLevel)) == 0)
		{
			if (newLevel > zoomLevel)
			{
				for (int i=zoomLevel;i<newLevel;i++) zoomOut();
			}
			else
			{
				for (int i=zoomLevel;i>newLevel;i--) zoomIn();				
			}
			zoomLevel = (int)newLevel;
		}
	}
	
	private void drawCell(int i, int j)
	{
		if (grid.getCell(i, j).getState() == 1)
		{
			for (int x=i*cellSize;x<(i+1)*cellSize;x++)
			{
				for (int y=j*cellSize;y<(j+1)*cellSize;y++)
				{
					pixelWriter.setColor(x, y, Color.BLACK);
				}						
			}
		}
		else
		{
			for (int x=i*cellSize;x<(i+1)*cellSize;x++)
			{
				for (int y=j*cellSize;y<(j+1)*cellSize;y++)
				{
					pixelWriter.setColor(x, y, Color.WHITE);
				}
				
			}
		}
	}

	private void drawGrid()
	{
		for (int i=0;i<grid.getWidth();i++)
		{
			for (int j=0;j<grid.getHeight();j++) drawCell(i,j);
		}
	}

	// dxProperty and dyProperty are used by the mouse handler to correctly position
	// the mouse within the view.
	public void bindDXProperty(DoubleBinding widthProperty)
	{
		dxProperty.bind(widthProperty);
	}

	public void bindDYProperty(DoubleBinding heightProperty)
	{
		dyProperty.bind(heightProperty);
	}

	// Binds with the slider in the main view to control the speed of animation.
	// TODO Change method of pausing.
	public void bindSpeedProperty(DoubleProperty valueProperty)
	{
		timeline.rateProperty().bind(valueProperty);
	}

	// Binds with slider in the main view to control zoom level.
	// TODO Change zooming to allow more flexibility.
	public void bindZoomProperty(DoubleProperty valueProperty)
	{
		valueProperty.addListener((o, oldVal, newVal) -> zoom(newVal.doubleValue()));
	}
	
}
