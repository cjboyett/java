package application;
	
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Main extends Application
{
	private Stage stage;
	private Group rootContent;
	
	private double mousex = -1, mousey = -1, dx = 0, dy = 0;
	
	private final static int WIDTH = 80;
	private final static int HEIGHT = 80;
	private int count = 0;

	private static WritableImage[] sprite;
	private Canvas canvas;
	private GraphicsContext gc;
	
	private boolean inPopup, inCanvas;

	@Override
	public void start(Stage stage)
	{
		sprite = new WritableImage[12];
		for (int i=0;i<12;i++) sprite[i] = new WritableImage(WIDTH, HEIGHT);

		for (int i=0;i<80;i++)
		{
			for (int j=0;j<80;j++)
			{
				sprite[0].getPixelWriter().setArgb(i, j, SpriteData.getPixels1()[i][j]);
				sprite[1].getPixelWriter().setArgb(i, j, SpriteData.getPixels2()[i][j]);
				sprite[2].getPixelWriter().setArgb(i, j, SpriteData.getPixels3()[i][j]);
				sprite[3].getPixelWriter().setArgb(i, j, SpriteData.getPixels4()[i][j]);
				sprite[4].getPixelWriter().setArgb(i, j, SpriteData.getPixels5()[i][j]);
				sprite[5].getPixelWriter().setArgb(i, j, SpriteData.getPixels6()[i][j]);
				sprite[6].getPixelWriter().setArgb(i, j, SpriteData.getPixels7()[i][j]);
				sprite[7].getPixelWriter().setArgb(i, j, SpriteData.getPixels8()[i][j]);
				sprite[8].getPixelWriter().setArgb(i, j, SpriteData.getPixels9()[i][j]);
				sprite[9].getPixelWriter().setArgb(i, j, SpriteData.getPixels10()[i][j]);
				sprite[10].getPixelWriter().setArgb(i, j, SpriteData.getPixels11()[i][j]);
				sprite[11].getPixelWriter().setArgb(i, j, SpriteData.getPixels12()[i][j]);
			}
		}
		
		this.stage = stage;
		rootContent = new Group();
		Scene scene = new Scene(rootContent);
		scene.setFill(Color.TRANSPARENT);
				
		canvas = new Canvas(WIDTH, HEIGHT);
		gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.TRANSPARENT);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.drawImage(sprite[0], 0, 0);
		rootContent.getChildren().add(canvas);
		
		PopupMenu popup = new PopupMenu();
		
		Timeline timeline = new Timeline(
				new KeyFrame(new Duration(100), new EventHandler<ActionEvent>()
				{
					public void handle(ActionEvent actionEvent)
					{
						moveSprite();
					}
				}));

		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();

		canvas.setOnMouseDragged(event ->
		{
			popup.hide();
			if (event.getButton().compareTo(MouseButton.PRIMARY) == 0)
			{
				if (mousex != -1 && mousey != -1)
				{
					dx = mousex - event.getScreenX();
					dy = mousey - event.getScreenY();
					moveWindow();
				}
				mousex = event.getScreenX();
				mousey = event.getScreenY();
			}
		});
		
		canvas.setOnMouseReleased(event ->
		{
			mousex = -1;
			mousey = -1;
			popup.show(canvas, stage.getX() + 50, stage.getY() - 10);
		});
		
		Timeline timer = new Timeline(
				new KeyFrame(new Duration(200), event -> {}),
				new KeyFrame(new Duration(100), event -> popup.show(canvas, stage.getX() + 50, stage.getY() - 10))
				);
		
		inCanvas = false;
		inPopup = false;
		
		Timeline timer2 = new Timeline(
				new KeyFrame(new Duration(300), event -> {}),
				new KeyFrame(new Duration(100), event -> {if (!inPopup) popup.hide();})
				);
		
		canvas.setOnMouseEntered(event ->
		{
			inCanvas = true;
			timer.play();
		});
		canvas.setOnMouseExited(event ->
		{
			inCanvas = false;
			timer.stop();
			timer2.play();
		});
		
		stage.setOnCloseRequest(event ->
		{
			Platform.exit();
			System.exit(0);
		});
				
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.sizeToScene();
		stage.setScene(scene);
		stage.setAlwaysOnTop(true);
		stage.getIcons().add(sprite[0]);
		stage.show();
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}

	private void moveSprite()
	{
		if (!stage.isIconified())
		{
			count = (count + 1) % 12;
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			gc.drawImage(sprite[count], 0, 0);
			stage.getIcons().clear();
			stage.getIcons().add(sprite[count]);
		}
	}
	
	private void moveWindow()
	{
		stage.setX(stage.getX() - dx);
		stage.setY(stage.getY() - dy);
	}

	private class PopupMenu extends Popup
	{
		private Label close, minimize;
		public PopupMenu()
		{
			int buttonWidth = 30;
			int buttonHeight = 25;

			close = new Label("X");
			close.setPrefSize(buttonWidth, buttonHeight);
			close.setPadding(new Insets(0, 0, 0, 10));
			close.setFont(Font.font("Arial", FontWeight.BOLD, 14));
			close.setStyle("-fx-background-color:lightgray; -fx-text-fill:gray; -fx-border-color:darkgray;");
			close.setOpacity(0.75);
			close.setOnMouseClicked(event -> stage.close());
			
			minimize = new Label("—");
			minimize.setPrefSize(buttonWidth, buttonHeight);
			minimize.setPadding(new Insets(0, 0, 0, 7));
			minimize.setFont(Font.font("Arial", FontWeight.BOLD, 14));
			minimize.setStyle("-fx-background-color:lightgray; -fx-text-fill:gray; -fx-border-color:darkgray;");
			minimize.setOpacity(0.75);
			minimize.setOnMouseClicked(event ->
			{
				hide();
				stage.setIconified(true);
			});
									
			Timeline timer2 = new Timeline(
					new KeyFrame(new Duration(200), event -> {}),
					new KeyFrame(new Duration(100), event -> {if (!inCanvas) hide();})
					);

			HBox box = new HBox(minimize, close);
			
			AnchorPane pane = new AnchorPane();
			pane.getChildren().addAll(box);

//			AnchorPane.setTopAnchor(close, 0.0);
//			AnchorPane.setBottomAnchor(close, 0.0);
//			AnchorPane.setRightAnchor(close, 0.0);
//			AnchorPane.setLeftAnchor(close, (double)buttonWidth);
//
//			AnchorPane.setTopAnchor(minimize, 0.0);
//			AnchorPane.setBottomAnchor(minimize, 0.0);
//			AnchorPane.setRightAnchor(minimize, (double)buttonWidth);
//			AnchorPane.setLeftAnchor(minimize, 0.0);
		
			pane.setOnMouseEntered(event -> inPopup = true);
			pane.setOnMouseExited(event ->
			{
				inPopup = false;
				timer2.play();
			});
			getContent().add(pane);
			pane.setPrefSize(2 * buttonWidth, buttonHeight);
		}
	}
}
