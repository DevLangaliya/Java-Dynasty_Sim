package com.example.fffController;

import java.util.ArrayList;
import java.util.Arrays;

public class Play {
	private ArrayList<Player> offensivePOIs, defensivePOIs;
	private Player qb;
	private String type, res;
	private int week, down, ytg, fp;
	public int ydgGain;
	
	public Play(Player qb, ArrayList<Player> offensivePOIs, ArrayList <Player> defensivePOIs, String type, int week, int down, int ytg){
		this.offensivePOIs = offensivePOIs;
		this.defensivePOIs = defensivePOIs;
		this.qb = qb;
		this.type = type;
		this.week = week;
		this.down = down;
		this.ytg = ytg;
	}
	
	public String getRunner(){
		return offensivePOIs.get(tar).getName();
	}
	
	private int index, tar;
	
	public int weightedTarget(){
		double[] allRatings = new double[offensivePOIs.size()];
		for(int i = 0; i < offensivePOIs.size(); i++){
			allRatings[i] = offensivePOIs.get(i).getOverallRating();
		}
		
		double totalWeight = 0.0;
		for (double i : allRatings) {
			totalWeight += i;
		}

		int target = 0;
		for (double r = Math.random() * totalWeight; target < allRatings.length - 1; target++) {
			r -= allRatings[target];
			if (r <= 0){
				break;
			}
		}
		return target;
	}
	
	public int weightedDefender(){
		double[] allRatings = new double[defensivePOIs.size()];
		for(int i = 0; i < defensivePOIs.size(); i++){
			allRatings[i] = defensivePOIs.get(i).getOverallRating();
		}
		
		double totalWeight = 0.0;
		for (double i : allRatings) {
			totalWeight += i;
		}
		
		int target = 0;
		for (double r = Math.random() * totalWeight; target < allRatings.length - 1; target++) {
			r -= allRatings[target];
			if (r <= 0){
				break;
			}
		}
		return target;
	}
	
	public void yardsGained (int fp){
		int gain = 0;
		if(defensivePOIs != null) {
			index = weightedDefender();
			tar = weightedTarget();
			
		}
		switch (type) {
			case "RUN" -> {
				gain = offensivePOIs.get(tar).getOverallRating() / qb.getRandomNumber(4, 19);
				offensivePOIs.get(tar).getStatValues().get("RUSH YD")[week] += gain;
				offensivePOIs.get(tar).getStatValues().get("RUSH ATT")[week] += gain;
			}
			case "PASS" -> {
				ArrayList <Player> oline = new ArrayList <>(qb.getTeam().getRoster().get("OL"));
				int totalBlock = 0;
				for (Player player : oline) {
					totalBlock += player.getPlayerRatings().get("BLK");
				}
				int totalShed = 0;
				for (Player poIs : defensivePOIs) {
					totalShed += poIs.getPlayerRatings().get("BSH");
				}
				int sum = totalBlock + totalShed;
				if(((double) totalBlock) /2 > qb.getRandomNumber(1, sum)){
					defensivePOIs.get(index).getStatValues().get("SCK")[week] += 1;
					defensivePOIs.get(index).getStatValues().get("TKL")[week] += 1;
					type = "SACK";
					break;
				}
				if (qb.getPlayerRatings().get("THA") <= defensivePOIs.get(index).getPlayerRatings().get("COV") - 20) {
					qb.getStatValues().get("PASS INT")[week] += 1;
					defensivePOIs.get(index).getStatValues().get("DEF INT")[week] += 1;
					type = "INT";
				} else {
					if (offensivePOIs.get(tar).getPlayerRatings().get("CAT") - 15 >= (Math.random() * 100)) {
						gain = offensivePOIs.get(tar).getOverallRating() / qb.getRandomNumber(2, 9);
						offensivePOIs.get(tar).getStatValues().get("REC YD")[week] += gain;
						System.out.println(Arrays.toString(offensivePOIs.get(tar).getStatValues().get("REC YD")));
						offensivePOIs.get(tar).getStatValues().get("REC")[week] += 1;
						defensivePOIs.get(index).getStatValues().get("TKL")[week] += 1;
						if(fp - gain <= 0 || fp + gain >= 100){
							offensivePOIs.get(tar).getStatValues().get("REC TD")[week] += 1;
						}
						type = "COMP";
					} else {
						defensivePOIs.get(index).getStatValues().get("PASS DEF")[week] += 1;
						type = "INC";
					}
				}
			}
			case "KICK" -> {
				if (inRange() && ifAccurate()) {
					type = "GOOD";
				} else {
					type = "NOT";
				}
			}
		}
		
		ydgGain = gain;
	}
	
	public boolean ifAccurate(){
		int acc = (int) (15000000/Math.pow(offensivePOIs.get(0).getPlayerRatings().get("KPA"), 3));
		return acc > (int) (100.0 * Math.random());
	}
	
	public boolean inRange(){
		int pow = offensivePOIs.get(0).getPlayerRatings().get("KPW");
		int maxRange = (int) (6.634*Math.sqrt(pow));
		return maxRange > 50;
		
	}
	
	public String getOutcome(){
		switch (type) {
			case "RUN" ->
					res = (getRunner() + " RUSHES " + " who gains " + ydgGain + " yards. Tackle made by " + defensivePOIs.get(index).getName() + ".");
			case "COMP" -> {
				res = (qb.getName() + " COMPLETED pass to " + offensivePOIs.get(tar).getName() + " for " + ydgGain + " yards. Tackle made by " + defensivePOIs.get(index).getName() + ".");
				if (down + 1 > 4 && ydgGain < ytg) {
					res += " TURNOVER ON DOWNS!!!";
				}
			}
			case "INT" ->
					res = (qb.getName() + " passes the ball quite poorly, and is INTERCEPTED BY " + defensivePOIs.get(index).getName());
			case "INC" -> {
				res = (qb.getName() + " INCOMPLETE pass to " + offensivePOIs.get(tar).getName() + ", well defended by " + defensivePOIs.get(index).getName());
				if (down + 1 > 4) {
					res += " TURNOVER ON DOWNS!!!";
				}
			}
			case "GOOD" -> res = (offensivePOIs.get(0).getName() + " field goal try is GOOD!");
			case "NOT" -> res = (offensivePOIs.get(0).getName() + " field goal try is MISSED!");
			case "SACK" -> {
				res = ("Pass attempt... SACKED by " + defensivePOIs.get(index).getName() + "!!!");
				if (down + 1 > 4) {
					res += " TURNOVER ON DOWNS!!!";
				}
			}
 		}
		return res;
	}
	
}
