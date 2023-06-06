package com.example.fffController;

import java.util.ArrayList;

public class Trade {
	private ArrayList<Player> oppOffer, oppRequest;
	private Team offerTeam, requestTeam;
	private double offerVal, requestVal;
	
	public Trade(ArrayList<Player> oppOffer, ArrayList<Player> oppRequest, Team offerTeam, Team requestTeam){
		this.offerTeam = offerTeam;
		this.oppOffer = oppOffer;
		this.oppRequest = oppRequest;
		this.requestTeam = requestTeam;
		offerVal = 0;
		requestVal = 0;
		calcVals();
	
	}
	
	public void calcVals(){
		for (Player player : oppOffer) {
			offerVal += player.getValue();
		}
		for (Player player : oppRequest) {
			requestVal += player.getValue();
		}
	}
	
	public String getTradeDetails(){
		String off = "";
		String req = "";
		for(Player p : oppOffer){
			off += (p.getName() + (" (" +  p.getOverallRating() +  ") ") + " ");
		}
		for(Player p : oppRequest){
			req += (p.getName() + (" (" +  p.getOverallRating() +  ") ") + " ");
		}
		return (offerTeam.getName() + " is offering " + off + " (" + Double.parseDouble(Double.toString(offerVal).substring(0, 3)) +  ")\nin exchange for " + req + " (" + Double.parseDouble(Double.toString(requestVal).substring(0, 3)) + ")\nWhat are your thoughts on this trade?");
	}
	
	public ArrayList<Player> getOppRequest(){
		return oppOffer;
	}
	
	public ArrayList<Player> getOppOffer(){
		return oppRequest;
	}
	
	public Team getReqTeam(){
		return offerTeam;
	}
	
}
