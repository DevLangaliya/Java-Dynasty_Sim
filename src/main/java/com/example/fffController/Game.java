package com.example.fffController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Game {
    private Team[] allTeams;
    private Map<String, ArrayList<Player>> awayRoster, homeRoster;
    public Team awayTeam, homeTeam;
    private int fieldPosition;
    public ArrayList<Play> outcome;
    public int[] points;
    private int week;
    public int down, ytg, possession;
    
    
    public Game(Team awayTeam, Team homeTeam, int week){
        this.week = week;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        setRosters();
        allTeams = new Team[]{awayTeam, homeTeam};
        possession = (int) (Math.random() + .5);
        kickOff(allTeams[possession%2]);
        outcome = new ArrayList<>();
        points = new int[2];
        down = 1;
        ytg = 10;
    }
    
    public void setRosters() {
        awayRoster = awayTeam.getRoster();
        homeRoster = homeTeam.getRoster();
    }
    
    public void kickOff(Team team){
        Player kos = team.getRoster().get("K").get(0);
        int pow = kos.getPlayerRatings().get("KPW");
        fieldPosition = team.getName().equals(homeTeam.getName()) ? pow : 100 - pow;
        down = 1;
        ytg = 10;
        possession++;
    }
    
    public void doPlay(){
        if(down == 4){
            fieldGoal();
        }
        double playType = Math.random();
        if(playType >= allTeams[possession%2].getPlayTend()){
            run();
        }else {
            pass();
        }
    }
    
    
    
    public void fieldGoal(){
        Play kick = new Play(allTeams[possession%2].getRoster().get("QB").get(0), allTeams[possession%2].getRoster().get("K"), null, "KICK", week, down, ytg);
        outcome.add(kick);
        kick.yardsGained(fieldPosition);
        if(outcome.get(outcome.size()-1).getOutcome().contains("MISSED")){
            allTeams[possession%2].getRoster().get("K").get(0).getStatValues().get("KICK ATT")[week] += 1;
            possession++;
            down = 1;
            ytg =10;
        } else {
            allTeams[possession%2].getRoster().get("K").get(0).getStatValues().get("KICK ATT")[week] += 1;
            allTeams[possession%2].getRoster().get("K").get(0).getStatValues().get("KICK MADE")[week] += 1;
            points[possession%2] += 3;
            kickOff(allTeams[possession%2]);
        }
    }
    
    public void run(){
        List <String> keys = List.of("DL", "LB", "CB", "S");
        ArrayList<Player> tacklers = new ArrayList <>();
        for(String s : keys){
            for(Player p : allTeams[(possession+1)%2].getRoster().get(s)){
                tacklers.add(p);
                tacklers.sort((s1, s2) -> Integer.compare(s2.getOverallRating(), s1.getOverallRating()));
            }
        }
        Play runPlay = new Play(allTeams[possession%2].getRoster().get("QB").get(0), allTeams[possession%2].getRoster().get("HB"), tacklers, "RUN", week, down, ytg);
        runPlay.yardsGained(fieldPosition);
        fieldPosition = possession%2 == 0 ? fieldPosition - runPlay.ydgGain: fieldPosition + runPlay.ydgGain;
        outcome.add(runPlay);
        if (outcome.get(outcome.size()-1).getOutcome().contains("RUSHES")){
            down++;
            if(down > 4){
                down = 1;
                ytg = 10;
                possession++;
            } else if (runPlay.ydgGain >= ytg){
                down = 1;
                ytg = 10;
                if (fieldPosition <= 0 || fieldPosition >= 100){
                    points[possession%2] += 7;
                    kickOff(allTeams[possession%2]);
                    allTeams[possession%2].getRoster().get("HB").get(getRandomNumber(0,allTeams[possession%2].getRoster().get("HB").size())).getStatValues().get("RUSH TD")[week] += 1;
                }
            }
        }
        
    }
    
    public void pass(){
        List <String> keys = List.of("DL", "LB", "CB", "S");
        ArrayList<Player> tacklers = new ArrayList <>();
        for(String s : keys){
            for(Player p : allTeams[(possession+1)%2].getRoster().get(s)){
                tacklers.add(p);
                tacklers.sort((s1, s2) -> Integer.compare(s2.getOverallRating(), s1.getOverallRating()));
            }
        }
        Play passPlay = new Play(allTeams[possession%2].getRoster().get("QB").get(0), allTeams[possession%2].getRoster().get("WR"), tacklers, "PASS", week, down, ytg);
        passPlay.yardsGained(fieldPosition);
        fieldPosition = possession%2 == 0 ? fieldPosition - passPlay.ydgGain: fieldPosition + passPlay.ydgGain;
        outcome.add(passPlay);
        if (outcome.get(outcome.size()-1).getOutcome().contains("SACKED")){
            ytg += getRandomNumber(1, 15);
            down++;
            if(down > 4){
                down = 1;
                ytg = 10;
                possession++;
            }
        } else if(outcome.get(outcome.size()-1).getOutcome().contains("INTERCEPTED")){
            possession++;
            down = 1;
            ytg = 10;
        } else if (outcome.get(outcome.size()-1).getOutcome().contains("INCOMPLETE")){
            down++;
            if(down > 4){
                down = 1;
                ytg = 10;
                possession++;
            }
        } else if (outcome.get(outcome.size()-1).getOutcome().contains("COMPLETED")){
            allTeams[possession%2].getRoster().get("QB").get(0).getStatValues().get("PASS ATT")[week] += 1;
            allTeams[possession%2].getRoster().get("QB").get(0).getStatValues().get("PASS YD")[week] += passPlay.ydgGain;
            if(fieldPosition <= 0 || fieldPosition >= 100){
                allTeams[possession%2].getRoster().get("QB").get(0).getStatValues().get("PASS TD")[week] += 1;
                down = 1;
                ytg = 10;
                points[possession%2] += 7;
                kickOff(allTeams[possession%2]);
            } else if (passPlay.ydgGain >= ytg){
                down = 1;
                if(fieldPosition >= 90 || fieldPosition <= 10) {
                    ytg = possession % 2 == 0 ? fieldPosition : 100 - fieldPosition;
                } else{
                    ytg = 10;
                }
                
            }
        } else if (outcome.get(outcome.size()-1).getOutcome().contains("TURNOVER")){
            possession++;
            down = 1;
            ytg = 10;
        }
    }
    
    public double getFieldPosition(){
        return (double) fieldPosition / 100;
    }
    
    public void sim(){
        kickOff(allTeams[possession%2]);
        int limit = getRandomNumber(70,100);
        for(int i = 0; i < limit; i++){
            doPlay();
        }
        
    }
    
    public Team[] getAllTeams(){
        return allTeams;
    }
    
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    
}
