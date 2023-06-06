package com.example.fffController;

import java.util.*;

public class Player {
    
    private Team team;
    private String firstName, lastName, position, archetype;
    private int overallRating, maxCount, lowerBound;
    private HashMap<String, Integer> playerRatings;
    private HashMap<String, int[]> playerStats;
    private double xp, caliber, age, value;
    private String[] ratingNames = {"AGI", "STR", "CAT", "THP", "THA", "BLK", "BSH", "TKL", "COV", "KPW", "KPA", "INJ"};
    private String[] importantRatings;
    private boolean isOnTradeBlock;
    private final Map<String, String[]> types = new HashMap<>() {{
        put("QB", new String[]{"Elusive", "Strong Arm", "Accurate"});
        put("HB", new String[]{"Elusive", "Power", "Receiving"});
        put("WR", new String[]{"Playmaker", "Physical", "Possession"});
        put("OL", new String[]{"Agile", "Pass Blocker", "Run Blocker"});
        put("DL", new String[]{"Hard Hitter", "Run Stopper", "Stonewall"});
        put("LB", new String[]{"Hard Hitter", "Run Stopper", "Stonewall", "Ball Hawk"});
        put("CB", new String[]{"Hard Hitter", "Stonewall", "Ball Hawk"});
        put("S", new String[]{"Hard Hitter", "Run Stopper", "Stonewall", "Ball Hawk"});
        put("K", new String[]{"Power", "Accurate"});
    }};

    public Player(String firstName, String lastName, int age, String position, int maxNum, int lowerBound) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.position = position;
        this.lowerBound = lowerBound;
        maxCount = maxNum;
        playerRatings = new HashMap<>();
        playerStats = new HashMap <>();
        xp = 0;
        initializingFunctions(position);
        value = getOverallRating() * getCaliber();
        isOnTradeBlock = false;
    }
    
    public double getValue() {
        return value;
    }
    
    public void editTradeBlockStatus(){
        isOnTradeBlock = !isOnTradeBlock;
    }
    
    public boolean getTradeBlockStatus(){
        return isOnTradeBlock;
    }
    
    public void initializingFunctions(String position) {
        determineRatings(position);
        setUpStats();
        setCaliber();
        setUpRatings();
        giveRatings();
        setOverallRating();
    }
    
    public void setUpStats(){
        playerStats.clear();
        playerStats.put("PASS ATT", new int[17]);
        playerStats.put("PASS CMP", new int[17]);
        playerStats.put("PASS YD", new int[17]);
        playerStats.put("PASS TD", new int[17]);
        playerStats.put("PASS INT", new int[17]);
        playerStats.put("RUSH ATT", new int[17]);
        playerStats.put("RUSH YD", new int[17]);
        playerStats.put("RUSH TD", new int[17]);
        playerStats.put("REC", new int[17]);
        playerStats.put("REC YD", new int[17]);
        playerStats.put("REC TD", new int[17]);
        playerStats.put("SCK", new int[17]);
        playerStats.put("TKL", new int[17]);
        playerStats.put("PASS DEF", new int[17]);
        playerStats.put("DEF INT", new int[17]);
        playerStats.put("KICK ATT", new int[17]);
        playerStats.put("KICK MADE", new int[17]);
    }
    
    public HashMap<String, int[]> getStatValues (){
        return playerStats;
    }
    
    public void assignTeam(Team team){
        this.team = team;
    }
    
    public Team getTeam(){
        return team;
    }
    
    public int getLowerBound() {
        return lowerBound;
    }
    
    public String[] getImportantStats(){
        return importantRatings;
    }

    public void setUpRatings(){
        for(String s : ratingNames){
            playerRatings.put(s, null);
        }
    }

    public void determineRatings(String position) {
        switch (position) {
            case "QB" -> importantRatings = new String[]{"AGI", "THP", "THA"};
            case "HB", "WR" -> importantRatings = new String[]{"AGI", "STR", "CAT"};
            case "OL" -> importantRatings = new String[]{"AGI", "STR", "BLK"};
            case "DL" -> importantRatings = new String[]{"STR", "BSH", "TKL"};
            case "LB", "S" -> importantRatings = new String[]{"STR", "BSH", "TKL", "COV"};
            case "CB" -> importantRatings = new String[]{"STR", "TKL", "COV"};
            case "K" -> importantRatings = new String[]{"KPW", "KPA"};
        }
        
    }

    public void giveRatings(){
        for(int i = 0 ; i < ratingNames.length - 1 ;i++){
            if(ratingNames[i].equals("INJ")){
                break;
            }
            if(Arrays.toString(importantRatings).contains(ratingNames[i])) {
                if(maxCount > 0) {
                    playerRatings.replace(ratingNames[i], getRandomNumber(80, 99));
                } else {
                    playerRatings.replace(ratingNames[i], getRandomNumber(55, 80));
                }
            } else {
                if(maxCount > 0) {
                    playerRatings.replace(ratingNames[i], getRandomNumber(35, 65));
                } else {
                    playerRatings.replace(ratingNames[i], getRandomNumber(15, 45));
                }
            }
        }
        playerRatings.replace("INJ", getRandomNumber(1, 99));
    }

    public HashMap<String, Integer> getPlayerRatings(){
        return playerRatings;
    }

    public void setOverallRating(){
        ArrayList<Integer> impRatings = new ArrayList<>();
        for(String s : importantRatings){
            impRatings.add(playerRatings.get(s));
        }
        int max = Collections.max(impRatings);
        int sum = 0;
        for(Integer i : impRatings){
            sum += Math.abs(max - i);
        }
        if(position.equals("OL") || position.equals("QB")){
            sum /= 3;
        } else {
            sum /= 2;
        }
        overallRating = max - sum;
        archetype = types.get(position)[impRatings.indexOf(max)];
    }

    public int getOverallRating(){
        return overallRating;
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    
    public void setCaliber() {
        caliber = (-1 + Math.pow(2, 1+(40-age)/5.05789))/10;
    }

    public double getCaliber(){
        return caliber;
    }

    public String getArchetype(){
        return archetype;
    }

    public String getName(){
        return firstName + " " + lastName;
    }

    public String getPosition(){
        return position;
    }

    public double getAge(){
        return age;
    }

    public double getXp() {
        return xp;
    }

    public void changePosition(String position) {
        this.position = position;
        determineRatings(position);
        setCaliber();
        setUpRatings();
        giveRatings();
        setOverallRating();
    }
    
    public ArrayList<Integer> getRatingsAsList(){
        ArrayList<Integer> allRatings = new ArrayList <>();
        for(String key : playerRatings.keySet()){
            allRatings.add(playerRatings.get(key));
        }
        return allRatings;
    }
}
