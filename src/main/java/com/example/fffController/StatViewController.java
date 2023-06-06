package com.example.fffController;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class StatViewController {
	
	public static void start(Stage stage, String statView, ArrayList <Player> pList, int week) throws IOException {
		
		NumberAxis xAxis = new NumberAxis(1, 16, 1);
		xAxis.setLabel("Week");
		NumberAxis yAxis = new NumberAxis(1, 12, 1);
		yAxis.setAutoRanging(true);
		if (statView.contains("YD")) {
			yAxis.setLabel("Yards");
		} else {
			yAxis.setLabel("#");
		}
		LineChart statComparison = new LineChart <>(xAxis, yAxis);
		for (Player pl : pList) {
			XYChart.Series <Number, Number> series = new XYChart.Series();
			series.setName(pl.getName());
			for(int i = 0; i < week; i++){
				series.getData().add(new XYChart.Data(i + 1, pl.getStatValues().get(statView)[i]));
			}
			statComparison.getData().add(series);
		}
		
		Scene scene = new Scene(new VBox(statComparison), 400, 200);
		stage.setScene(scene);
		stage.setTitle(statView);
		stage.setHeight(300);
		stage.setWidth(1200);
		stage.show();
	}
}
