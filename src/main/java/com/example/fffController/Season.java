package com.example.fffController;

import java.util.ArrayList;
import java.util.Arrays;

public class Season {

    public int week;
    public Team[][][] allMatchUps = new Team[16][16][2];
    private ArrayList<Team> allTeams;
    public Game[][] allGames = new Game[16][16];

    public Season(){
        week = 1;
    }

    public void createSchedule(){
        for(int i = 0; i < 16 ; i++){
            ArrayList<Team> tempTeams = new ArrayList<>(allTeams);
            for(int j = 0; j < 16; j++){
                for(int k = 0; k < 2; k++){
                    if(tempTeams.size() == 1){
                        allMatchUps[i][j][k] = tempTeams.remove(0);
                    } else {
                        allMatchUps[i][j][k] = tempTeams.remove(getRandomNumber(0, tempTeams.size()));
                    }
                }
            }
        }
    }

    public void setTeams(ArrayList<Team> allTeams){
        this.allTeams = allTeams;
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    
    public int playGame(Team t){
        int slot = 0;
        for(int i = 0; i < allMatchUps[week-1].length; i++){
            if(allMatchUps[week-1][i][1] == t || allMatchUps[week-1][i][0] == t){
                slot = i;
            } else {
                Game g = new Game(allMatchUps[week - 1][i][1], allMatchUps[week - 1][i][0], week-1);
                g.sim();
                System.out.println(Arrays.toString(g.points));
                if(g.points[0] > g.points[1]){
                    System.out.println(allMatchUps[week - 1][i][0].getName() + "WINS");
                    allMatchUps[week - 1][i][1].updateRecord(0);
                    allMatchUps[week - 1][i][0].updateRecord(1);
                } else if (g.points[0] < g.points[1]){
                    System.out.println(allMatchUps[week - 1][i][1].getName() + "WINS");
                    allMatchUps[week - 1][i][1].updateRecord(1);
                    allMatchUps[week - 1][i][0].updateRecord(0);
                } else {
                    System.out.println("tie");
                    allMatchUps[week - 1][i][0].updateRecord(2);
                    allMatchUps[week - 1][i][1].updateRecord(2);
                }
                allGames[week-1][i] = g;
            }
        }
        return slot;
    }
    
    public void simWeek(int weeks){
        for(int k = 0; k < weeks; k++) {
            for (int i = 0; i < allMatchUps[week - 1].length; i++) {
                Game g = new Game(allMatchUps[week - 1][i][1], allMatchUps[week - 1][i][0], week - 1);
                g.sim();
                System.out.println(Arrays.toString(g.points));
                if (g.points[0] > g.points[1]) {
                    System.out.println(allMatchUps[week - 1][i][0].getName() + "WINS");
                    allMatchUps[week - 1][i][1].updateRecord(0);
                    allMatchUps[week - 1][i][0].updateRecord(1);
                } else if (g.points[0] < g.points[1]) {
                    System.out.println(allMatchUps[week - 1][i][1].getName() + "WINS");
                    allMatchUps[week - 1][i][1].updateRecord(1);
                    allMatchUps[week - 1][i][0].updateRecord(0);
                } else {
                    System.out.println("tie");
                    allMatchUps[week - 1][i][0].updateRecord(2);
                    allMatchUps[week - 1][i][1].updateRecord(2);
                }
                allGames[week - 1][i] = g;
                
            }
            advanceWeek();
        }
    }
    
    public Team[][][] getAllMatchUps() {
        return allMatchUps;
    }
    
    public void advanceWeek(){
        week++;
    }
}
