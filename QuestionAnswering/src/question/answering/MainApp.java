package question.answering;

import question.answering.controller.DetailsController;
import question.answering.controller.SettingsController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {

	Stage primaryStage;
	AnchorPane rootLayout;
	
	SettingsController settings;
	DetailsController details;
	
	
	private double xOffset = 0;
	private double yOffset = 0;

	public static final String MAIN_SCREEN = "main";
	public static final String MAIN_SCREEN_FXML = "view/Main.fxml";
	public static final String DETAILS_SCREEN = "details";
	public static final String DETAILS_SCREEN_FXML = "view/Details.fxml";
	public static final String SETTINGS_SCREEN = "settings";
	public static final String SETTINGS_SCREEN_FXML = "view/Settings.fxml";

	@Override
	public void start(Stage primaryStage) {
		ScreensController mainContainer = new ScreensController();
		mainContainer.loadScreen(MainApp.MAIN_SCREEN, MainApp.MAIN_SCREEN_FXML);
		mainContainer.loadScreen(MainApp.DETAILS_SCREEN, MainApp.DETAILS_SCREEN_FXML);
		mainContainer.loadScreen(MainApp.SETTINGS_SCREEN, MainApp.SETTINGS_SCREEN_FXML);
		mainContainer.setScreen(MainApp.MAIN_SCREEN);
		//client = new ClientController();
		Group root = new Group();
		root.getChildren().addAll(mainContainer);
		
		settings = mainContainer.myLoader.getController();
		settings.setStage(primaryStage);
		
		
		
		
		Scene scene = new Scene(root);
		primaryStage.initStyle(StageStyle.UNDECORATED);
		
		root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });

		
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	@Override
	public void stop(){
		Platform.exit();
		System.exit(0);
	}

	public static void main(String[] args) {
		launch(args);
		
	}

}
