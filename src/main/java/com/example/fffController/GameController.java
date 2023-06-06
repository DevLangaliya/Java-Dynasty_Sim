package com.example.fffController;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import static javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE;

public class GameController {
	
	public static void start(Stage stage, Game game) throws IOException {
		final long[] timeStart = {System.nanoTime()};
		
		ImageView iv = new ImageView(new Image(new FileInputStream("src/main/resources/FIELD.jpeg")));
		iv.setY(100);
		
		ImageView homePos = new ImageView(new Image(new FileInputStream("src/main/resources/football.jpeg")));
		ImageView awayPos = new ImageView(new Image(new FileInputStream("src/main/resources/football.jpeg")));
		
		ImageView[] possessions = {homePos, awayPos};
		
		Label homeName = new Label("HOME");
		Label awayName = new Label("AWAY");
		Label homeScore = new Label("0");
		Label awayScore = new Label("0");
		
		
		ImageView homeTeam = new ImageView(game.homeTeam.getLogo());
		ImageView awayTeam = new ImageView(game.awayTeam.getLogo());
		
		homeName.setLayoutX(30);
		homeName.setLayoutY(24);
		homeScore.setLayoutX(110);
		homeScore.setLayoutY(40);
		homeTeam.setX(30);
		homeTeam.setY(34);
		homeTeam.setFitWidth(66);
		homeTeam.setFitHeight(66);
		homePos.setY(40);
		homePos.setFitWidth(50);
		homePos.setX(200);
		homePos.setFitHeight(50);
		
		
		
		awayName.setLayoutX(519);
		awayName.setLayoutY(24);
		awayScore.setLayoutX(480);
		awayScore.setLayoutY(40);
		awayTeam.setX(514);
		awayTeam.setY(34);
		awayTeam.setFitWidth(66);
		awayTeam.setFitHeight(66);
		awayPos.setY(40);
		awayPos.setFitWidth(50);
		awayPos.setX(350);
		awayPos.setFitHeight(50);
		
		
		ProgressBar possession = new ProgressBar(.5);
		iv.setOnMouseClicked(click -> System.out.println(click.getX() + ", " + click.getY()));
		
		possession.setMinWidth(417);
		possession.setLayoutX(97);
		possession.setLayoutY(239);
		possession.setMinHeight(60);
		
		ListView <String> playTracker = new ListView <>();
		
		TabPane statPlaySplit = new TabPane();
		Tab plays = new Tab();
		statPlaySplit.getTabs().add(plays);
		plays.setContent(playTracker);
		plays.setText("PLAY-BY-PLAY");
		
		
		statPlaySplit.setLayoutY(410);
		statPlaySplit.setLayoutX(29);
		statPlaySplit.setTabMinWidth(502);
		statPlaySplit.setTabMinHeight(50);
		statPlaySplit.setTabMaxHeight(100);
		
		statPlaySplit.setTabClosingPolicy(UNAVAILABLE);
		final int[] count = {0};
		possession.setProgress(game.getFieldPosition());
		final int[] limit = {game.homeTeam.getRoster().get("QB").get(0).getRandomNumber(80, 110)};
		
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (now - timeStart[0] > 100000000.0) {
					timeStart[0] = System.nanoTime();
					game.doPlay();
					playTracker.getItems().add(0,game.outcome.get(game.outcome.size() - 1).getOutcome());
					possession.setProgress(game.getFieldPosition());
					if(game.possession%2  == 0){
						homePos.setOpacity(0);
						awayPos.setOpacity(1);
					} else{
						homePos.setOpacity(1);
						awayPos.setOpacity(0);
					}
					count[0]++;
					if(count[0] == limit[0]){
						possession.setProgress(.5);
						System.out.println("FINAL SCORE " + Arrays.toString(game.points));
						stop();
						if(game.points[0] > game.points[1]){
							game.getAllTeams()[0].updateRecord(0);
							game.getAllTeams()[1].updateRecord(1);
						} else if (game.points[0] < game.points[1]){
							game.getAllTeams()[0].updateRecord(1);
							game.getAllTeams()[1].updateRecord(0);
						} else {
							game.getAllTeams()[0].updateRecord(2);
							game.getAllTeams()[1].updateRecord(2);
						}
					}
					homeScore.setText(String.valueOf((game.points[1])));
					awayScore.setText(String.valueOf((game.points[0])));
					
				}
			}
		}.start();
		
		Scene scene = new Scene(new Group(iv, possession, statPlaySplit, homeTeam, awayTeam, homeName, awayName, homeScore, awayScore, homePos, awayPos), 600, 700);
		stage.setScene(scene);
		stage.setTitle(game.awayTeam.getName() + " vs. " + game.homeTeam.getName());
		stage.setScene(scene);
		stage.show();
		
		
	}
}
