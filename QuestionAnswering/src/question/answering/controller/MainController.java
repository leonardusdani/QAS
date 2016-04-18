package question.answering.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Stage;


import question.answering.Answer;
import question.answering.ControlledScreen;
import question.answering.MainApp;
import question.answering.ModelQAS;
import question.answering.ScreensController;

public class MainController implements ControlledScreen,
		Initializable {

	ModelQAS model;
	ScreensController myController;
	String log = "";
	
	Integer rank;
	
	@FXML
	AnchorPane root;

	@FXML
	TableView<Answer> tableAnswer;
	
	@FXML
	TableColumn<Answer,Integer> columnRank;
	
	@FXML
	TableColumn<Answer, String> columnContent;
	
	@FXML
	ProgressIndicator pgi;
	
	@FXML
	CheckBox checkBox;
	
	@FXML
	Button btnHome;
	
	@FXML
	Button btnLog;
	
	@FXML
	Button btnChart;
	
	@FXML
	Button btnAnswer;
	
	@FXML
	Button btnClose;
	
	@FXML
	Button btnSetting;
	
	@FXML
	TextField textFieldClient;
	
	@FXML
	Label labelAnswer;
	
	Stage stage;

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public MainController() {
			model = ModelQAS.getInstance();
		}

	@FXML
	private void goToSettings(ActionEvent event) {
		myController.setScreen(MainApp.SETTINGS_SCREEN);

	}	
	private double xOffset = 0;
	private double yOffset = 0;
	
	public void mp(MouseEvent event){
		xOffset = event.getSceneX();
        yOffset = event.getSceneY();
	}
	public void md(MouseEvent event){
		stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
	}
	
	public void info(){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Q&A");
		alert.setHeaderText(null);
		alert.setContentText("Silahkan masukkan pertanyaan factoid(apa,kapan,siapa,dimana,mengapa) mengenai Presiden Indonesia ke-1 hingga ke-7 dan Wakil Presiden Indonesia ke-1 hingga ke-12. Contoh: Kapan Joko Widodo lahir?");
		alert.showAndWait();
	}
	


	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
		
		
	}
	
	
	public void chart(){
		myController.setScreen(MainApp.DETAILS_SCREEN);
	
	}
	
	
	int bool = 0;
	Thread t1;
	Thread t2;
	Thread t3;
	
	public void close(){
		Platform.exit();
		System.exit(0);
	}
	
	public void log(){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Q&A");
		alert.setHeaderText("Log question answering system");


		TextArea textArea = new TextArea(model.getLog());
		textArea.setEditable(false);
		textArea.setWrapText(true);
		textArea.setFont(new Font(14));
		textArea.setMaxWidth(1366);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(1366);
		expContent.setMinWidth(800);
		expContent.add(textArea, 0, 0);
		
		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}
	
	public void answer() {
		
		root.setOpacity(0.5);
		pgi.setOpacity(1);
		pgi.setProgress(-1.0f);
		model.setFinish(true);
		bool = 0;
		
		if(checkBox.isSelected()){
			model.setCheckBox(true);
		}
		else{
			model.setCheckBox(false);
		}
		
		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(
            new Runnable(){    
                @Override
                public void run() {
                        Platform.runLater(new Runnable(){
                            @Override
                            public void run() {
                            	if(bool==0){
                            		Task<?> analyzing = model.analyzing(textFieldClient.getText());
                            		analyzing.messageProperty().addListener(
                            				(ObservableValue<? extends String> observerable,
                            						String oldValue, String newValue) -> {
                            					log = log + newValue;
                            				});
                            		t1 = new Thread(analyzing);
                            		t1.start();
                            		bool = 1;
                            	}
                            	else if(bool==1){
                            		if(!t1.isAlive()){
                            			bool = 2;
                            		}
                            	}
                            	else if(bool == 2){
                            		String query = model.getQuery();
                            	
    									Task<?> scoring = model.scoring(query);
    									t2 = new Thread(scoring);
    									t2.start();
    								
                            		bool = 3;
                            	}
                            	else if(bool==3){
                            		if(!t2.isAlive()){
                            			bool = 4;
                            		}
                            	}
                            	else if(bool == 4){
                            		try {
                            			Task<?> filtering = model.filtering();
        								t3 = new Thread(filtering);
        								t3.start();
    									
    								} catch (IOException e) {
    									
    									e.printStackTrace();
    								}
                            		bool = 5;
                            	}
                            	else if(bool==5){
                            		if(!t3.isAlive()){
                            			labelAnswer.setText(model.getAnswer());
                            			pgi.setOpacity(0);
                            			root.setOpacity(1);
                            			
                            			columnContent.setCellValueFactory(new PropertyValueFactory<Answer,String>("content"));
                            			columnRank.setCellValueFactory(new PropertyValueFactory<Answer,Integer>("rank"));
                            			ObservableList<Answer> data = FXCollections.observableArrayList();
                            			tableAnswer.setItems(data);
                            			data.setAll(model.getListAnswer1());
                            			
                            		
                            			
                            			
                            			
                            			tableAnswer.setRowFactory( tv -> {
                            			    TableRow<Answer> row = new TableRow<>();
                            			    row.setOnMouseClicked(event -> {
                            			        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                            			            Answer rowData = row.getItem();
                            			            Alert alert = new Alert(AlertType.INFORMATION);
                            			    		alert.setTitle("Informasi Q&A");
                            			    		alert.setHeaderText("Detail jawaban");
                            			    		alert.setContentText("Rank: "+ rowData.getRank()+"\nJawaban: " + rowData.getContent()+"\nScore: " + rowData.getScore()+"\nNilai similarity: " + rowData.getSimilarity() + "\nDokumen: " + rowData.getFileName());
                            			    		alert.showAndWait();
                            			        }
                            			    });
                            			    return row ;
                            			});
                            			
                            
                            			
                            		
                            			
                            			scheduler.shutdown();
                            		}
                            	}
                                
                            }
                        });
                }
            }, 1, 3, TimeUnit.SECONDS); 
		
	}

	@Override
	public void setScreenParent(ScreensController screenPage) {
		
		myController = screenPage;
		
		
	}



}
