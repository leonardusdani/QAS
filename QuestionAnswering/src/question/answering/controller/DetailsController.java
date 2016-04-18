package question.answering.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import question.answering.ControlledScreen;
import question.answering.Eval;
import question.answering.MainApp;
import question.answering.ModelQAS;
import question.answering.ScreensController;

public class DetailsController implements ControlledScreen, Initializable {

	ModelQAS model;

	ScreensController myController;

	@FXML
	Button btnHome;
	
	@FXML
	Label tabelDetail;
	
	@FXML
	TableView<Eval> tableAnswer;
	
	@FXML
	TableColumn<Eval,Integer> columnRank;
	
	@FXML
	TableColumn<Eval, String> columnContent;
	
	@FXML
	TableColumn<Eval, Float> columnSimilarity;
	
	@FXML
	LineChart<String,Integer> lineChart;
	
	@FXML
	NumberAxis yAxis;
	
	@FXML
	CategoryAxis xAxis;
	

	@FXML
	TextArea textArea;

	@FXML
	TextField textField;


	public DetailsController() {
		model = ModelQAS.getInstance();
	}

	
	

	


	@FXML
	private void goToMain(ActionEvent event) {
		myController.setScreen(MainApp.MAIN_SCREEN);

	}
	
	
	public void setChart(){
		xAxis.setLabel("Ranking (Dice Similarity)");
		yAxis.setLabel("Top score (Rule Based)");
		XYChart.Series<String,Integer> series = new XYChart.Series<String,Integer>();
		for(int i=1;i<=20;i++){
			series.getData().add(new XYChart.Data<String,Integer>(Integer.toString(i),model.getListEval().get(i-1).getTopScore()));
			
		}
		series.setName("line");
		lineChart.getData().add(series);
		
		columnContent.setCellValueFactory(new PropertyValueFactory<Eval,String>("fileName"));
		columnRank.setCellValueFactory(new PropertyValueFactory<Eval,Integer>("rank"));
		columnSimilarity.setCellValueFactory(new PropertyValueFactory<Eval,Float>("similarity"));
		ObservableList<Eval> data = FXCollections.observableArrayList();
		tableAnswer.setItems(data);
		data.setAll(model.getListEval());
		tableAnswer.setOpacity(1);
		tabelDetail.setOpacity(1);
		
	}
	
	
	public void close(){
		Platform.exit();
		System.exit(0);
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		tableAnswer.setOpacity(0);
		tabelDetail.setOpacity(0);
		
	}

	@Override
	public void setScreenParent(ScreensController screenPage) {
		myController = screenPage;
		// TODO Auto-generated method stub

	}

}
