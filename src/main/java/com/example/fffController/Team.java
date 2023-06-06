package com.example.fffController;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Team {
    private String city, name;
    private Map<String, ArrayList<Player>> roster;
    private Coach coach;
    private double playTend;
    private ArrayList<Double> overallRatings;
    private Image logo;
    private int[] record;
    
    private double standingScore;
    
    private String[] allPositions = {"QB", "HB", "WR", "OL", "DL", "LB", "CB", "S", "K"};
    
    
    public Team(String city, String name, Coach coach, Image logo){
        this.city = city;
        this.name = name;
        this.coach = coach;
        overallRatings = new ArrayList<>();
        roster = new HashMap<>(){{
            put("QB", new ArrayList<>());
            put("HB", new ArrayList<>());
            put("WR", new ArrayList<>());
            put("OL", new ArrayList<>());
            put("DL", new ArrayList<>());
            put("LB", new ArrayList<>());
            put("CB", new ArrayList<>());
            put("S", new ArrayList<>());
            put("K", new ArrayList<>());
        }};
        this.logo = logo;
        record = new int[3];
    }
    
    public Image getLogo(){
        return logo;
    }
    
    public int[] getRecord(){
        return record;
    }
    
    public void updateRecord(int result){
        record[result] += 1;
        standingScore = getStandingScore();
        System.out.println(getName() + " ------ " + getRecordAsString());
    }
    
    public ArrayList<String> generatePositionalNeeds(){
        ArrayList<String> positionalNeeds = new ArrayList <>();
        ArrayList<Player> testRank = new ArrayList <>();
        for(String s : allPositions){
            if(s.equals("K")) {
                break;
            }
            testRank.add(getRoster().get(s).get(0));
        }
        testRank.sort((s1, s2) -> Integer.compare(s1.getOverallRating(), s2.getOverallRating()));
        
        for(int i = 0; i < 3; i++){
            positionalNeeds.add(testRank.get(i).getPosition());
        }
        
        return positionalNeeds;
    }
    
    public ArrayList<ArrayList<Player>> findFairPackage(Team t){
        ArrayList<Player> oppOffer = new ArrayList<>();
        ArrayList<Player> oppRequest = new ArrayList<>();
        ArrayList<ArrayList<Player>> mainPackage = new ArrayList <>();
        
        for(int i = 0; i < 3; i++) {
            oppOffer.add(getRoster().get(t.generatePositionalNeeds().get(i)).get(getRandomNumber(0, getRoster().get(t.generatePositionalNeeds().get(i)).size())));
            oppRequest.add(t.getRoster().get(generatePositionalNeeds().get(i)).get(getRandomNumber(0, t.getRoster().get(generatePositionalNeeds().get(i)).size())));
        }
        
        
        mainPackage.add(oppOffer);
        mainPackage.add(oppRequest);
        return mainPackage;
    }
    
    public String getRecordAsString(){
        return (record[0] + "-" + record[1] + "-" + record[2]);
    }

    public Map<String, ArrayList<Player>> getRoster(){
        return roster;
    }
    
    public ArrayList<Player> getRosterAsList(){
        ArrayList<Player> allPlayers = new ArrayList <>();
        for(String s : allPositions){
            allPlayers.addAll(roster.get(s));
        }
        return allPlayers;
    }

    public String getName(){
        return getCity() + " " + getMainName();
    }
    
    public String getCity(){
        return this.city;
    }
    
    public String getMainName(){
        return this.name;
    }
    
    public void setTendencies(int passYards, int rushYards){
        //playTend = (double)passYards/((double)passYards + (double)rushYards);
        
        playTend = 0.25 + (0.55) * Math.random();
    }
    
    public void getRatings(){
        int sum = 0;
        int count = 1;
        for (int j = 0; j < 2; j++) {
            sum += roster.get(allPositions[j]).get(0).getOverallRating();
            count++;
        }
        for(int k = 2; k < 4; k++){
            for(int i = 0 ; i < roster.get(allPositions[k]).size()/3; i++){
                sum += roster.get(allPositions[k]).get(i).getOverallRating();
                count++;
            }
        }
        overallRatings.add((double) (sum/count));
        sum = 0;
        count = 1;
        for(int k = 4; k < 8; k++){
            for(int i = 0 ; i < roster.get(allPositions[k]).size()/4; i++){
                sum += roster.get(allPositions[k]).get(i).getOverallRating();
                count++;
            }
        }
        overallRatings.add((double) (sum/count));
        overallRatings.add((overallRatings.get(0) + overallRatings.get(1))/2);
        //System.out.println(overallRatings);
    }
    
    public Coach getCoach() {
        return this.coach;
    }
    
    public double getPlayTend() {
        return playTend;
    }
    
    public ArrayList<Double> getOverallRatings(){
        return overallRatings;
    }
    
    public double getStandingScore(){
        return record[0] + .5 * record[2];
    }
    
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    
}
