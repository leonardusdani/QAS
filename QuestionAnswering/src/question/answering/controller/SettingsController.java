package question.answering.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import question.answering.Constants;
import question.answering.ControlledScreen;
import question.answering.DatabaseConnections;
import question.answering.MainApp;
import question.answering.ModelQAS;
import question.answering.ScreensController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
 

public class SettingsController implements ControlledScreen, Initializable {

	ModelQAS model;
	ScreensController myController;
	DatabaseConnections dbCon;
	
	public SettingsController(){
		model = ModelQAS.getInstance();
	}
	
	@FXML
	AnchorPane ap;

	@FXML
	Button btnStart;
	
	@FXML
	TextField maxSearch;
	
	@FXML
	TextField dataDir;
	
	@FXML
	TextField indexDir;

	@FXML
	Button btnHIW;
	
	@FXML
	CheckBox indonesian;
	
	@FXML
	CheckBox bahasa;
	
	@FXML
	CheckBox gradeY;
	
	@FXML
	CheckBox gradeO;
	
	@FXML
	CheckBox gradeM;


	@FXML
	private void goToMain(ActionEvent event) {
		myController.setScreen(MainApp.MAIN_SCREEN);
	
	}
	
	public void close(){
		Platform.exit();
		System.exit(0);
	}
	
	public void save(){
		Constants.MAX_SEARCH = Integer.valueOf(maxSearch.getText());
		Constants.DATA_DIRECTORY = dataDir.getText();
		Constants.INDEX_DIRECTORY = indexDir.getText();
		
		if(indonesian.isSelected()){
			dbCon.setBahasa("LANGUAGE='I'");
		}else if(bahasa.isSelected()){
			dbCon.setBahasa("LANGUAGE='B'");
		}
		if(bahasa.isSelected() && indonesian.isSelected()){
			dbCon.setBahasa("LANGUAGE='I' OR LANGUAGE='B'");
		}
		
		if(gradeY.isSelected()){
			dbCon.setGrade("GRADE='Y'");
		}else if(gradeO.isSelected()){
			dbCon.setGrade("GRADE='O'");
		}else if(gradeM.isSelected()){
			dbCon.setGrade("GRADE='M'");
		}
		if(gradeM.isSelected() && gradeO.isSelected()){
			dbCon.setGrade("GRADE='M' OR GRADE='O'");
		}
		if(gradeM.isSelected() && gradeY.isSelected()){
			dbCon.setGrade("GRADE='M' OR GRADE='Y'");
		}
		if(gradeY.isSelected() && gradeO.isSelected()){
			dbCon.setGrade("GRADE='Y' OR GRADE='O'");
		}
		if(gradeY.isSelected() && gradeM.isSelected()){
			dbCon.setGrade("GRADE='Y' OR GRADE='M'");
		}
		if(gradeY.isSelected() && gradeO.isSelected() && gradeM.isSelected()){
			dbCon.setGrade("GRADE='Y' OR GRADE='O' OR GRADE='M'");
		}
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Q&A");
		alert.setHeaderText(null);
		alert.setContentText(dbCon.getGrade()+" ; "+dbCon.getBahasa());
		alert.showAndWait();
		
		
	}

	@Override
	public void setScreenParent(ScreensController screenParent) {
		myController = screenParent;

	}
	
	Stage stage;
	
	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void dirChooser(){
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Data directory");
		File defaultDirectory = new File("c:/users/");
		chooser.setInitialDirectory(defaultDirectory);
		File selectedDirectory = chooser.showDialog(stage);
		dataDir.setText(selectedDirectory.getAbsolutePath());
		
	}
	
	public void indexChooser(){
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Index directory");
		File defaultDirectory = new File("c:/users/");
		chooser.setInitialDirectory(defaultDirectory);
		File selectedDirectory = chooser.showDialog(stage);
		indexDir.setText(selectedDirectory.getAbsolutePath());
		
	}
	
	
	public void startIndexing() throws IOException {
		Task<?> indexing = model.createIndex();
		new Thread(indexing).start();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		dbCon = DatabaseConnections.getInstance();
		maxSearch.setText(Integer.toString(Constants.MAX_SEARCH));
		indexDir.setText(Constants.INDEX_DIRECTORY);
		dataDir.setText(Constants.DATA_DIRECTORY);
		indonesian.setSelected(true);
		gradeO.setSelected(true);
		gradeY.setSelected(true);
		dbCon.setBahasa("LANGUAGE='I'");
		dbCon.setGrade("GRADE='Y' OR GRADE='O'");

	}

}
