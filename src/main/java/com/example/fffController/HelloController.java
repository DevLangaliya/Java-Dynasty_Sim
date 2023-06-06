package com.example.fffController;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

import static javafx.scene.control.Tooltip.install;
import static javafx.scene.control.Tooltip.uninstall;

public class HelloController {

    private ArrayList<String> firstNames, lastNames, teamNames, teamCities;
    private ArrayList<Player> selectedFA;
	private Player selectedPlayer;
    private Map<String, ArrayList<Player>> positionPlayers;
    private Map<String, Map<String, ArrayList<Team>>> allTeams;
    private ArrayList<Team> allTeamsList;
    private ArrayList<Player> allPlayers, selPMany;
    private String[] conferences = {"NFC", "AFC"};
    private String[] divisions = {"NORTH", "SOUTH", "EAST", "WEST"};
    private String[] allPositions = {"QB", "HB", "WR", "OL", "DL", "LB", "CB", "S", "K"};
    private ArrayList<Integer> allPassingJSON, allRushingJSON;
    private Team myTeam;
    private Season mainSeason;
    private int selectedTeam;
    private Map<String, Integer> maxGoodPlayers, baseTotalPlayerCount;
    private ToggleButton[] faFilters;
    private boolean[] isSelected = new boolean[]{false, false, false, false, false, false, false, false, false};
    
    @FXML
    private Button addOffer, removeOffer, addRequest, removeRequest, declineTrade, accTrade, counterTrade;
    @FXML
    private GridPane scheduleGrid;
    @FXML
    private TextField rosterSearch, faSearch, weekNumToSim;
	@FXML
	private Spinner<Integer> filterFAAGE, filterFAOVR;
    @FXML
    private ListView <String> standingTeamRankings, rosterList, faList, oppTradeRoster, userTradeRoster, userTradeOffering, userTradeRequest, passYDLeaders;
    @FXML
    private ListView passTDLeaders;
    @FXML
    private ListView rushYDLeaders;
    @FXML
    private ListView recLeaders;
    @FXML
    private ListView recYDLeaders;
    @FXML
    private ListView tklLeaders;
    @FXML
    private ListView sackLeaders;
    @FXML
    private ListView defLeaders;
    @FXML
    private ComboBox <String> teamSelectStart, tradeRecipient;
    @FXML
    private TabPane allTabs;
    @FXML
    private Tab weekSchedule;
    @FXML
    private ImageView teamLogo, rosterLogo, homeTeamLogo;
    @FXML
    private Label needs, trdStat, homeTeamName, record, teamName, rosterPName, showRatingOVR, showRatingCAL, showAge, showAGI, showSTR, showCAT, showTHP, showTHA, showBLK, showBSH, showTKL, showCOV, showKPW, showKPA, showINJ, PNameFA, FAshowRatingOVR, FAshowRatingCAL, FAshowAge, FAshowAGI, FAshowSTR, FAshowCAT, FAshowTHP, FAshowTHA, FAshowBLK, FAshowBSH, FAshowTKL, FAshowCOV, FAshowKPW, FAshowKPA, FAshowINJ;
    @FXML
    private ProgressBar selectPlayTend, offRat, defRat, ovrRat, playerRating, playerCaliber, playerAGI, playerSTR, playerCAT, playerTHA, playerTHP, playerBLK, playerBSH, playerTKL, playerCOV, playerKPW, playerKPA, playerINJ, FARating, FACaliber, FAAGI, FASTR, FACAT, FATHA, FATHP, FABLK, FABSH, FATKL, FACOV, FAKPW, FAKPA, FAINJ;
    @FXML
    private ToggleButton filterQB, filterHB, filterWR, filterOL, filterDL, filterLB, filterCB, filterS, filterK;
    @FXML
    private CheckBox comp0, comp1, comp2, comp3, comp4, comp5, comp6, comp7;
    
    private ListView[] statComps;
    private CheckBox[] selectedComps;
    private ArrayList<ArrayList<Player>> leaderHolders;
    private int labelSpot;
    
    
    @FXML
    protected void initialize() throws UnsupportedEncodingException, FileNotFoundException {
        declareArrays();
        leagueGeneration();
        prepFilters();
        allTeamsList.forEach(Team::getRatings);
        allTeamsList.forEach(team -> teamSelectStart.getItems().add(team.getName()));
        for (CheckBox cb : selectedComps) {
            cb.setOnMouseClicked(click -> {
                CheckBox c = (CheckBox) click.getSource();
                for (int i = 0; i < selectedComps.length; i++) {
                    selectedComps[i].setSelected(c.getId().equals(selectedComps[i].getId()));
                    if(c.getId().equals(selectedComps[i].getId())) {
                        labelSpot = i;
                    }
                }
            });
        }
        userTradeOffering.setOnMouseClicked(click -> {
            removeOffer.setDisable(false);
            addOffer.setDisable(true);
        });
        userTradeRequest.setOnMouseClicked(click -> {
            removeRequest.setDisable(false);
            addRequest.setDisable(true);
        });
        oppTradeRoster.setOnMouseClicked(click -> {
            removeRequest.setDisable(true);
            addRequest.setDisable(false);
        });
        userTradeRoster.setOnMouseClicked(click -> {
            removeOffer.setDisable(true);
            addOffer.setDisable(false);
        });
    }
    
    private String[] prompts;
    private String[] tempPos;
    
    @FXML
    protected void openStatComp() throws IOException {
        ArrayList<Player> topStats = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            topStats.add(leaderHolders.get(labelSpot).get(leaderHolders.get(labelSpot).size() - i - 1));
        }
        StatViewController.start(new Stage(), prompts[labelSpot], topStats, mainSeason.week-1);
        
        
    }
	
    public void prepFilters(){
	    filterFAAGE.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(20, 40, 40, 1));
	    filterFAOVR.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(45, 99, 45, 1));
	    faFilters = new ToggleButton[]{filterQB, filterHB, filterWR, filterOL, filterDL, filterLB, filterCB, filterS, filterK};
        for(ToggleButton tb : faFilters){
            tb.setOnMouseClicked(click -> {
                ToggleButton selected = (ToggleButton) click.getSource();
                for (int i = 0; i < allPositions.length; i++) {
                    if (allPositions[i].equals(selected.getId().substring((6)))) {
                        isSelected[i] = !isSelected[i];
                    }
                }
                showFA(isSelected, faSearch.getText(), filterFAOVR.getValue(), filterFAAGE.getValue());
            });
        }
        faSearch.setOnKeyTyped(type -> showFA(isSelected, faSearch.getText(), filterFAOVR.getValue(), filterFAAGE.getValue()));
	    filterFAAGE.setOnMouseClicked(type -> showFA(isSelected, faSearch.getText(), filterFAOVR.getValue(), filterFAAGE.getValue()));
	    filterFAOVR.setOnMouseClicked(type -> showFA(isSelected, faSearch.getText(), filterFAOVR.getValue(), filterFAAGE.getValue()));
    }
    
    public void showFA(boolean[] isSelected, String name, int minOVR, int maxAGE){
        faList.getItems().clear();
        selectedFA = new ArrayList<>();
        for(int i = 0; i < isSelected.length; i++){
            if(isSelected[i]){
                for(Player p : positionPlayers.get(allPositions[i])){
                    if(p.getName().toUpperCase().contains(name.toUpperCase()) && (p.getOverallRating() >= minOVR) && (p.getAge() <= maxAGE)) {
                        faList.getItems().add(p.getName());
                        selectedFA.add(p);
                    }
                }
            }
        }
    }
    
    @FXML
    protected void selectTeamStart(){
        myTeam = allTeamsList.get(selectedTeam);
        validNames = new ArrayList<>(myTeam.getRosterAsList());
        setUpSeason();
        allTabs.getTabs().remove(0);
        fillRosterViews();
        testGame();
        record.setText(myTeam.getRecordAsString());
        rosterLogo.setImage(myTeam.getLogo());
        for(Team t : allTeamsList){
            if(t.getName().equals(myTeam.getName())){
                continue;
            }
            tradeRecipient.getItems().add(t.getName());
        }
        updateTradeList();
        homeTeamName.setText(myTeam.getName());
        homeTeamLogo.setImage(myTeam.getLogo());
        updateStandings();
        needs.setText("POSITIONAL NEEDS: " + myTeam.generatePositionalNeeds().toString());
    }
    
    private ArrayList<Team> rankTeams;
    
    public void updateStandings(){
        standingTeamRankings.getItems().clear();
        rankTeams = new ArrayList <>(allTeamsList);
        rankTeams.sort((s1, s2) -> Double.compare(s2.getStandingScore(), s1.getStandingScore()));
        for (Team rankTeam : rankTeams) {
            standingTeamRankings.getItems().add(rankTeam.getName() + " - " + rankTeam.getRecordAsString());
        }
        for(String s : standingTeamRankings.getItems()){
            if(myTeam.getName().equals(s)){
                standingTeamRankings.getSelectionModel().select(standingTeamRankings.getItems().indexOf(s));
            }
        }
    }
    
    public void updateTradeList(){
        userTradeRoster.getItems().clear();
        myTeam.getRosterAsList().forEach(p -> userTradeRoster.getItems().add(p.getName() + " - " + p.getOverallRating() +  " - "  + Double.parseDouble(Double.toString(p.getValue()).substring(0, 6))));
    }
    
    @FXML
    protected void chooseRecipient(){
        System.out.println(allTeamsList.size());
        selectedTeam = tradeRecipient.getSelectionModel().getSelectedIndex();
        oppTradeRoster.getItems().clear();
        userTradeRequest.getItems().clear();
        selectedTradeRequests.clear();
        totalRequestVal = 0;
        requestVal.setText("VALUE: ");
        Team t = getT();
        for(Player p : t.getRosterAsList()){
            oppTradeRoster.getItems().add(p.getName() + " - " + p.getOverallRating() +  " - "  + Double.parseDouble(Double.toString(p.getValue()).substring(0, 6)));
        }
    }
    
    
    ArrayList<Player> selectedTradeRequests = new ArrayList<>();
    ArrayList<Player> selectedTradeOfferings = new ArrayList<>();
    
    
    @FXML
    protected void addPlayerToRequest(){
        Team t = getT();
        Player p = t.getRosterAsList().get(oppTradeRoster.getSelectionModel().getSelectedIndex());
        if(!selectedTradeRequests.contains(p)) {
            selectedTradeRequests.add(p);
        }
        userTradeRequest.getItems().clear();
        selectedTradeRequests.forEach(player -> userTradeRequest.getItems().add(player.getName() + " - " + player.getOverallRating() +  " - "  + Double.parseDouble(Double.toString(player.getValue()).substring(0, 6))));
        findOfferVal(selectedTradeRequests, requestVal);
    }
    
    @FXML
    protected void removePlayerFromRequest(){
        selectedTradeRequests.remove(userTradeRequest.getSelectionModel().getSelectedIndex());
        userTradeRequest.getItems().clear();
        selectedTradeRequests.forEach(player -> userTradeRequest.getItems().add(player.getName() + " - " + player.getOverallRating() +  " - "  + Double.parseDouble(Double.toString(player.getValue()).substring(0, 6))));
        findOfferVal(selectedTradeRequests, requestVal);
        
    }
    
    private double totalOfferVal, totalRequestVal;
    
    @FXML
    protected void addPlayerToOffer(){
        Player p = myTeam.getRosterAsList().get(userTradeRoster.getSelectionModel().getSelectedIndex());
        if(!selectedTradeOfferings.contains(p)) {
            selectedTradeOfferings.add(p);
        }
        userTradeOffering.getItems().clear();
        selectedTradeOfferings.forEach(player -> userTradeOffering.getItems().add(player.getName() + " - " + player.getOverallRating() +  " - "  + Double.parseDouble(Double.toString(player.getValue()).substring(0, 6))));
        findOfferVal(selectedTradeOfferings, offerVal);
    }
    
    private void findOfferVal(ArrayList <Player> selectedTradeOfferings, Label offerVal) {
        double totalOfferVal = 0;
        for (Player pla : selectedTradeOfferings) {
            totalOfferVal += pla.getValue();
        }
        offerVal.setText("VALUE: " + Double.parseDouble(Double.toString(totalOfferVal).substring(0, 6)));
    }
    
    @FXML
    protected void removePlayerFromOffer(){
        selectedTradeOfferings.remove(userTradeOffering.getSelectionModel().getSelectedIndex());
        userTradeOffering.getItems().clear();
        selectedTradeOfferings.forEach(player -> userTradeOffering.getItems().add(player.getName() + " - " + player.getOverallRating() +  " - "  + Double.parseDouble(Double.toString(player.getValue()).substring(0, 6))));
        findOfferVal(selectedTradeOfferings, offerVal);
    }
    
    
    @FXML
    protected void offerTrade(){
        Team t = getT();
        if(totalOfferVal >= totalRequestVal - 5.0){
            tradeDecision.setText("The " + t.getMainName() + " accept your offer!!!");
            performTrade(t);
        } else {
            tradeDecision.setText("The " + t.getMainName() + " reject your lousy trade offer. Try again.");
        }
    }
    
    private ArrayList<Trade> allTradeOffers;
    
    public Team getTnotMe(){
        Team t = null;
        while(t == null || t.getName().equals(myTeam.getName())){
            t = allTeamsList.get(getRandomNumber(0, allTeamsList.size()));
        }
        return t;
    }
    
    
    
    public void generateTrades(){
        Team t = getTnotMe();
        ArrayList<ArrayList<Player>> oppTradePackage = t.findFairPackage(myTeam);
        System.out.println(t.getName());
        Trade trade = new Trade(oppTradePackage.get(0), oppTradePackage.get(1), t, myTeam);
        allTradeOffers.add(trade);
        tradeOffers.getItems().add("Trade offer from " + t.getName());
        
    }
    
    private Trade selectedTrade;
    
    @FXML
    private ListView tradeOffers;
    
    @FXML
    private Label tradeInfo;
    
    @FXML
    protected void selectTrade(){
        selectedTrade = allTradeOffers.get(tradeOffers.getSelectionModel().getSelectedIndex());
        accTrade.setDisable(false);
        declineTrade.setDisable(false);
        counterTrade.setDisable(false);
        tradeInfo.setText(selectedTrade.getTradeDetails());
    }
    
    @FXML
    protected void acceptTrade(){
        selectedTradeOfferings.addAll(selectedTrade.getOppRequest());
        selectedTradeRequests.addAll(selectedTrade.getOppOffer());
        
        for(Player p : selectedTradeRequests){
            selectedTrade.getReqTeam().getRoster().get(p.getPosition()).add(p);
            myTeam.getRoster().get(p.getPosition()).remove(p);
            p.assignTeam(selectedTrade.getReqTeam());
        }
        for(Player p : selectedTradeOfferings){
            selectedTrade.getReqTeam().getRoster().get(p.getPosition()).remove(p);
            myTeam.getRoster().get(p.getPosition()).add(p);
            p.assignTeam(myTeam);
        }
        userTradeRequest.getItems().clear();
        userTradeOffering.getItems().clear();
        oppTradeRoster.getItems().clear();
        selectedTradeRequests.clear();
        selectedTradeOfferings.clear();
        totalRequestVal = 0;
        totalOfferVal = 0;
        requestVal.setText("VALUE: ");
        offerVal.setText("VALUE: ");
        for (String s : Arrays.asList("QB", "HB", "WR", "OL", "DL", "LB", "CB", "S", "K")) {
            myTeam.getRoster().get(s).sort((s1, s2) -> Integer.compare(s2.getOverallRating(), s1.getOverallRating()));
            selectedTrade.getReqTeam().getRoster().get(s).sort((s1, s2) -> Integer.compare(s2.getOverallRating(), s1.getOverallRating()));
        }
        updateTradeList();
        fillRosterViews();
        
        accTrade.setDisable(true);
        declineTrade.setDisable(true);
        counterTrade.setDisable(true);
        allTradeOffers.remove(selectedTrade);
        tradeOffers.getItems().remove(tradeOffers.getSelectionModel().getSelectedIndex());
        tradeInfo.setText("");
    }
    
    @FXML
    protected void declineTrade(){
        accTrade.setDisable(true);
        declineTrade.setDisable(true);
        counterTrade.setDisable(true);
        allTradeOffers.remove(selectedTrade);
        tradeOffers.getItems().remove(tradeOffers.getSelectionModel().getSelectedIndex());
        tradeInfo.setText("");
    }
    
    @FXML
    protected void counterTrade(){
        accTrade.setDisable(true);
        declineTrade.setDisable(true);
        counterTrade.setDisable(true);
        allTabs.getSelectionModel().select(3);
        
        tradeRecipient.getSelectionModel().select(selectedTrade.getReqTeam().getName());
        
        selectedTradeRequests.addAll(selectedTrade.getOppRequest());
        selectedTradeOfferings.addAll(selectedTrade.getOppOffer());
        
        selectedTradeOfferings.forEach(player -> userTradeOffering.getItems().add(player.getName() + " - " + player.getOverallRating() +  " - "  + Double.parseDouble(Double.toString(player.getValue()).substring(0, 6))));
        selectedTradeRequests.forEach(player -> userTradeRequest.getItems().add(player.getName() + " - " + player.getOverallRating() +  " - "  + Double.parseDouble(Double.toString(player.getValue()).substring(0, 6))));
        
        findOfferVal(selectedTrade.getOppRequest(), offerVal);
        findOfferVal(selectedTrade.getOppOffer(), requestVal);
        
        for(Player p : selectedTrade.getReqTeam().getRosterAsList()){
            oppTradeRoster.getItems().add(p.getName() + " - " + p.getOverallRating() +  " - "  + Double.parseDouble(Double.toString(p.getValue()).substring(0, 6)));
        }
        
        allTradeOffers.remove(selectedTrade);
        tradeOffers.getItems().remove(tradeOffers.getSelectionModel().getSelectedIndex());
        tradeInfo.setText("");
    }
    
    public Team getT() {
        Team t = null;
        for(Team te: allTeamsList){
            if (te.getName().equals(tradeRecipient.getItems().get(selectedTeam))){
                t = te;
            }
        }
        assert t != null;
        return t;
    }
    
    public void performTrade(Team t){
        for(Player p : selectedTradeOfferings){
            allTeamsList.get(selectedTeam).getRoster().get(p.getPosition()).add(p);
            myTeam.getRoster().get(p.getPosition()).remove(p);
            p.assignTeam(allTeamsList.get(selectedTeam));
        }
        for(Player p : selectedTradeRequests){
            allTeamsList.get(selectedTeam).getRoster().get(p.getPosition()).remove(p);
            myTeam.getRoster().get(p.getPosition()).add(p);
            p.assignTeam(myTeam);
        }
        userTradeRequest.getItems().clear();
        userTradeOffering.getItems().clear();
        oppTradeRoster.getItems().clear();
        selectedTradeRequests.clear();
        selectedTradeOfferings.clear();
        totalRequestVal = 0;
        totalOfferVal = 0;
        requestVal.setText("VALUE: ");
        offerVal.setText("VALUE: ");
        for (String s : Arrays.asList("QB", "HB", "WR", "OL", "DL", "LB", "CB", "S", "K")) {
            myTeam.getRoster().get(s).sort((s1, s2) -> Integer.compare(s2.getOverallRating(), s1.getOverallRating()));
            allTeamsList.get(selectedTeam).getRoster().get(s).sort((s1, s2) -> Integer.compare(s2.getOverallRating(), s1.getOverallRating()));
        }
        updateTradeList();
        fillRosterViews();
        
        
    }
    
    @FXML
    private Label offerVal, requestVal, tradeDecision;
    
    public void fillRosterViews(){
        rosterList.getItems().clear();
        ArrayList<String> names = new ArrayList<>();
        for(Player p : myTeam.getRosterAsList()){
            names.add(p.getName());
        }
        rosterList.getItems().addAll(names);
    }
    
   
	
    
    @FXML
    protected void selectPlayerFromRoster(){
        ProgressBar[] allRatings = new ProgressBar[]{playerAGI, playerSTR, playerCAT, playerTHP, playerTHA, playerBLK, playerBSH, playerTKL, playerCOV, playerKPW, playerKPA, playerINJ};
        Label[] allRatingsShow = new Label[]{showAGI, showSTR, showCAT, showTHP, showTHA, showBLK, showBSH, showTKL, showCOV, showKPW, showKPA, showINJ};
        Player p = validNames.get(rosterList.getSelectionModel().getSelectedIndex());
		selectedPlayer = p;
        if(!selPMany.contains(p)) {
            selPMany.add(p);
        }
        rosterPName.setText(p.getName() + " ----- "  + p.getArchetype() + " " + p.getPosition());
        playerRating.setProgress((double) p.getOverallRating() / 100);
        for(Label l : allRatingsShow){
            l.setStyle("-fx-text-fill: white;");
        }
        for(int i = 0; i < allRatings.length; i++){
            allRatings[i].setProgress((double) p.getPlayerRatings().get(allRatings[i].getId().substring(6)) / 100);
            allRatingsShow[i].setText(allRatingsShow[i].getId().substring(4) + ": " + p.getPlayerRatings().get(allRatingsShow[i].getId().substring(4)));
            if(Arrays.toString(p.getImportantStats()).contains(allRatings[i].getId().substring(6))){
                allRatingsShow[i].setStyle("-fx-text-fill: #add8e6;");
            }
        }
        trdStat.setText("ON TRADE BLOCK: " + selectedPlayer.getTradeBlockStatus());
        showRatingOVR.setText("OVR: " + (p.getOverallRating()));
        playerCaliber.setProgress(p.getCaliber()/3.00005);
        showRatingCAL.setText("CAL: " + Double.toString(p.getCaliber()).substring(0, 5));
        showAge.setText("AGE: " + Double.toString(p.getAge()).substring(0, 2));
        
    }
	
	@FXML
	protected void selectPlayerFromFA(){
		ProgressBar[] allRatings = new ProgressBar[]{FAAGI, FASTR, FACAT, FATHA, FATHP, FABLK, FABSH, FATKL, FACOV, FAKPW, FAKPA, FAINJ};
		Label[] allRatingsShow = new Label[]{FAshowAGI, FAshowSTR, FAshowCAT, FAshowTHP, FAshowTHA, FAshowBLK, FAshowBSH, FAshowTKL, FAshowCOV, FAshowKPW, FAshowKPA, FAshowINJ};
		Player p = selectedFA.get(faList.getSelectionModel().getSelectedIndex());
		selectedPlayer = p;
		PNameFA.setText(p.getName() + " ----- "  + p.getArchetype() + " " + p.getPosition());
		FARating.setProgress((double) p.getOverallRating() / 100);
		for(Label l : allRatingsShow){
			l.setStyle("-fx-text-fill: white;");
		}
		for(int i = 0; i < allRatings.length; i++){
			allRatings[i].setProgress((double) p.getPlayerRatings().get(allRatings[i].getId().substring(2)) / 100);
			allRatingsShow[i].setText(allRatingsShow[i].getId().substring(6) + ": " + p.getPlayerRatings().get(allRatingsShow[i].getId().substring(6)));
			if(Arrays.toString(p.getImportantStats()).contains(allRatings[i].getId().substring(2))){
				allRatingsShow[i].setStyle("-fx-text-fill: #add8e6;");
			}
		}
		FAshowRatingOVR.setText("OVR: " + (p.getOverallRating()));
		FACaliber.setProgress(p.getCaliber()/3.00005);
		FAshowRatingCAL.setText("CAL: " + Double.toString(p.getCaliber()).substring(0, 5));
		FAshowAge.setText("AGE: " + Double.toString(p.getAge()).substring(0, 2));
		
	}
    
    @FXML
    protected void chooseTeam(){
        selectedTeam = teamSelectStart.getSelectionModel().getSelectedIndex();
        teamLogo.setImage(allTeamsList.get(selectedTeam).getLogo());
        offRat.setProgress(allTeamsList.get(selectedTeam).getOverallRatings().get(0)/100);
        defRat.setProgress(allTeamsList.get(selectedTeam).getOverallRatings().get(1)/100);
        ovrRat.setProgress(allTeamsList.get(selectedTeam).getOverallRatings().get(2)/100);
        selectPlayTend.setProgress(allTeamsList.get(selectedTeam).getPlayTend());
        
    }
    
    private void leagueGeneration() throws UnsupportedEncodingException, FileNotFoundException {
        generateLists();
        generateNames();
        generatePlayers();
        generateTeams();
        addPlayersToTeams();
        generateTendenciesForTeams();
    }
    
    private void declareArrays() {
        firstNames = new ArrayList<>();
        lastNames = new ArrayList<>();
        allPlayers = new ArrayList<>();
        teamNames = new ArrayList<>();
        teamCities = new ArrayList<>();
        allTeamsList = new ArrayList<>();
        selPMany = new ArrayList <>();
        allTradeOffers = new ArrayList<>();
        prompts = new String[]{"PASS YD", "PASS TD", "RUSH YD", "REC", "REC YD", "SCK", "TKL", "PASS DEF"};
        tempPos = new String[]{"QB", "QB", "HB", "WR", "WR", "DL", "LB", "S"};
        selectedComps = new CheckBox[]{comp0, comp1, comp2, comp3, comp4, comp5, comp6, comp7};
        statComps = new ListView[]{passYDLeaders, passTDLeaders, rushYDLeaders, recLeaders, recYDLeaders, tklLeaders, sackLeaders, defLeaders};
    }
    
    @FXML
    protected void addToTradeBlock(){
        Player p = myTeam.getRosterAsList().get(rosterList.getSelectionModel().getSelectedIndex());
        p.editTradeBlockStatus();
        selectPlayerFromRoster();
    }
    
    private int myTeamGameSlot;
    public void testGame(){
        teamName.setText(myTeam.getName());
    }
    
    @FXML
    protected void simOtherGames(){
        myTeamGameSlot = mainSeason.playGame(myTeam);
        showSchedule(mainSeason.week);
        
    }
    
    @FXML
    private Tab recap;
    
    @FXML
    private Label placement;
    
    @FXML
    protected void advanceWeek(){
        mainSeason.advanceWeek();
        if(mainSeason.week <= 16) {
            showSchedule(mainSeason.week);

        } else if (mainSeason.week == 17){
            allTabs.getTabs().remove(1, 5);
            updateStats();
            updateStandings();
            tradeInfo.setText("");
            allTradeOffers.clear();
            tradeOffers.getItems().clear();
            tradeOffers.setDisable(true);
            accTrade.setDisable(true);
            declineTrade.setDisable(true);
            counterTrade.setDisable(true);
            allTabs.getTabs().get(2).setDisable(false);
            allTabs.getSelectionModel().select(recap);
            placement.setText("YOU FINISHED IN RANK " + (rankTeams.indexOf(myTeam) + 1) + " WITH A RECORD OF " + myTeam.getRecordAsString());
            return;
        }
        record.setText(myTeam.getRecordAsString());
        updateStats();
        updateStandings();
        generateTrades();
        
    }
    
    @FXML
    protected void simWeeks(){
        int weeks = Integer.parseInt(weekNumToSim.getText());
        if(weeks > 16-mainSeason.week){
            return;
        }
        mainSeason.simWeek(weeks);
        showSchedule(mainSeason.week);
        record.setText(myTeam.getRecordAsString());
        updateStats();
        updateStandings();
        generateTrades();
    }
    
    private void updateStats() {
        leaderHolders = new ArrayList<>();
        for(int i = 0; i < 8; i++){
            leaderHolders.add(new ArrayList <>());
        }
        for (Team t: allTeamsList){
            leaderHolders.get(0).add(t.getRoster().get("QB").get(0));
            leaderHolders.get(1).add(t.getRoster().get("QB").get(0));
            for(int i = 0; i < t.getRoster().get("HB").size(); i++){
                leaderHolders.get(2).add(t.getRoster().get("HB").get(i));
            }
            for(int j = 0 ; j < t.getRoster().get("WR").size(); j++) {
                leaderHolders.get(3).add(t.getRoster().get("WR").get(j));
                leaderHolders.get(4).add(t.getRoster().get("WR").get(j));
                
            }
            for(int j = 0; j < t.getRoster().get("LB").size(); j++) {
                leaderHolders.get(6).add(t.getRoster().get("LB").get(j));
                leaderHolders.get(5).add(t.getRoster().get("LB").get(j));
            }
            for(int j = 0; j < t.getRoster().get("DL").size(); j++){
                leaderHolders.get(5).add(t.getRoster().get("DL").get(j));
                leaderHolders.get(6).add(t.getRoster().get("DL").get(j));
                
            }
            for(int i = 0; i < t.getRoster().get("CB").size(); i++) {
                leaderHolders.get(7).add(t.getRoster().get("CB").get(i));
            }
            for(int i = 0; i < t.getRoster().get("S").size(); i++) {
                leaderHolders.get(7).add(t.getRoster().get("S").get(i));
            }
            int count = 0;
            for (ArrayList <Player> alp : leaderHolders) {
                int finalCount = count;
                alp.sort((s1, s2) -> Integer.compare(Arrays.stream(s1.getStatValues().get(prompts[finalCount])).sum(), Arrays.stream(s2.getStatValues().get(prompts[finalCount])).sum()));
                count++;
            }
            int c = 0;
            int sum;
            for(ListView lv : statComps){
                lv.getItems().clear();
                for(int i = leaderHolders.get(c).size() - 1; i >= 0; i--){
                    sum = 0;
                    for(int j = 0; j < 16; j++) {
                        sum += leaderHolders.get(c).get(i).getStatValues().get(prompts[c])[j];
                    }
                    lv.getItems().add(leaderHolders.get(c).get(i).getName() + " - (" +  leaderHolders.get(c).get(i).getTeam().getMainName() + ") - " + sum);
                    
                }
                c++;
            }
            
        }
    }
    
    @FXML
    protected void userGame() throws IOException {
       Game g = new Game(mainSeason.allMatchUps[mainSeason.week-1][myTeamGameSlot][1], mainSeason.allMatchUps[mainSeason.week-1][myTeamGameSlot][0], mainSeason.week-1);
       mainSeason.allGames[mainSeason.week-1][myTeamGameSlot] = g;
       GameController.start(new Stage(), g);
    }
    
    @FXML
    protected void releaseSelP(){
        selectedPlayer.assignTeam(null);
        myTeam.getRoster().get(selectedPlayer.getPosition()).remove(selectedPlayer);
        positionPlayers.get(selectedPlayer.getPosition()).add(selectedPlayer);
        for (String s : Arrays.asList("QB", "HB", "WR", "OL", "DL", "LB", "CB", "S", "K")) {
            positionPlayers.get(s).sort((s1, s2) -> Integer.compare(s2.getOverallRating(), s1.getOverallRating()));
        }
        if(selectedPlayer.getTradeBlockStatus()){
            selectedPlayer.editTradeBlockStatus();
        }
        validNames = new ArrayList<>(myTeam.getRosterAsList());
        fillRosterViews();
        updateTradeList();
    }
    
    @FXML
    protected void signFA(){
        selectedPlayer.assignTeam(myTeam);
        myTeam.getRoster().get(selectedPlayer.getPosition()).add(selectedPlayer);
        positionPlayers.get(selectedPlayer.getPosition()).remove(selectedPlayer);
        showFA(isSelected, faSearch.getText(), filterFAOVR.getValue(), filterFAAGE.getValue());
        for (String s : Arrays.asList("QB", "HB", "WR", "OL", "DL", "LB", "CB", "S", "K")) {
            myTeam.getRoster().get(s).sort((s1, s2) -> Integer.compare(s2.getOverallRating(), s1.getOverallRating()));
        }
        validNames = new ArrayList<>(myTeam.getRosterAsList());
        fillRosterViews();
        updateTradeList();
    }
    
    private ArrayList<Player> validNames;
    
    @FXML
    protected void filterRosterSearch(){
        rosterList.getItems().clear();
        String name = rosterSearch.getText();
        validNames = new ArrayList<>();
        for(Player p : myTeam.getRosterAsList()){
            if(p.getName().toUpperCase(Locale.ROOT).contains(name.toUpperCase())){
                validNames.add(p);
            }
        }
        for(int i =0; i < validNames.size(); i++) {
            rosterList.getItems().addAll(validNames.get(i).getName());
        }
    }
    
    public void generateTendenciesForTeams(){
       for (Team team : allTeamsList) {
           team.setTendencies(0, 0);
       }
       
    }

    public void setUpSeason() {
        mainSeason = new Season();
        mainSeason.setTeams(allTeamsList);
        mainSeason.createSchedule();
        showSchedule(mainSeason.week);
        weekSchedule.setText("Week " + (mainSeason.week));
        
    }
    
    public void showSchedule(int week){
        for(int i = 0; i < mainSeason.allMatchUps[week-1].length; i++) {
            Tooltip scoreReport = new Tooltip();
            if(mainSeason.allGames[week-1][i] != null) {
                scoreReport.setText("Final: " + mainSeason.allGames[week-1][i].points[1] + " - " + mainSeason.allGames[week-1][i].points[0]);
                install(((GridPane) scheduleGrid.getChildren().get(i)).getChildren().get(2), scoreReport);
            } else {
                uninstall(((GridPane) scheduleGrid.getChildren().get(i)).getChildren().get(2), scoreReport);
            }
            ((ImageView) ((GridPane) scheduleGrid.getChildren().get(i)).getChildren().get(0)).setImage(mainSeason.allMatchUps[week - 1][i][0].getLogo());
            ((Label) ((GridPane) scheduleGrid.getChildren().get(i)).getChildren().get(2)).setText(mainSeason.allMatchUps[week - 1][i][0].getName() + " (" + mainSeason.allMatchUps[week - 1][i][0].getRecordAsString() + ") \n vs. \n " + mainSeason.allMatchUps[week - 1][i][1].getName() + " (" + mainSeason.allMatchUps[week - 1][i][1].getRecordAsString() + ")");
            ((ImageView) ((GridPane) scheduleGrid.getChildren().get(i)).getChildren().get(1)).setImage(mainSeason.allMatchUps[week - 1][i][1].getLogo());
            
        }
        weekSchedule.setText("Week " + (mainSeason.week));
    }

    public void generateTeams() {
        File dir = new File("src/main/resources/Images");
        File[] files = dir.listFiles();
        assert files != null;
        int count = 1;
        for(String teamName: teamNames){
            Image logo = null;
            for(File f : files){
                if (f.getName().toUpperCase().contains(teamName)){
                    try {
                        logo = new Image(new FileInputStream("src/main/resources/Images/" + f.getName()));
                    } catch (FileNotFoundException e){
                        throw new RuntimeException();
                    }
                    break;
                }
            }
            Team t = new Team(teamCities.get(count-1), teamName, new Coach(firstNames.get(getRandomNumber(1, firstNames.size())-1), lastNames.get(getRandomNumber(1, lastNames.size())-1)), logo);
            if(count > 16){
                allTeams.get("AFC").get(divisions[(count-17)/4]).add(t);
            } else {
                allTeams.get("NFC").get(divisions[(count-1)/4]).add(t);
            }
            allTeamsList.add(t);
            count++;
        }
    }

    public void addPlayersToTeams(){
        Map<String, Integer> baseRosterPlayer = new HashMap<>(){{
            put("QB", 3);
            put("HB", 3);
            put("WR", 9);
            put("OL", 9);
            put("DL", 9);
            put("LB", 9);
            put("CB", 6);
            put("S", 4);
            put("K", 1);
        }};
        for(String position : allPositions){
            for(int i = 0; i < baseRosterPlayer.get(position); i++){
                for(String conference : conferences) {
                    for (String division : divisions) {
                        for (Team team : allTeams.get(conference).get(division)) {
                            addAndAssignPlayerToTeam(position, team);
                        }
                    }
                }
            }
        }
    }
    
    public void addAndAssignPlayerToTeam(String position, Team team) {
        Player p;
        if(team.getRoster().get(position).size() >=1) {
            ArrayList <Player> selectedOptions = new ArrayList <>();
            for (int k = 0; k < positionPlayers.get(position).size(); k++) {
                int random = getRandomNumber(1, baseTotalPlayerCount.get(position) - 1) / 2;
                if (positionPlayers.get(position).get(k).getLowerBound() <= random) {
                    selectedOptions.add(positionPlayers.get(position).get(k));
                }
            }
            p = selectedOptions.get(getRandomNumber(1, selectedOptions.size()) - 1);
        } else {
            p = positionPlayers.get(position).remove(0);
        }
        p.assignTeam(team);
        positionPlayers.get(position).remove(p);
        allPlayers.remove(p);
        team.getRoster().get(position).add(p);
        if (team.getRoster().get(position).size() > 1) {
            team.getRoster().get(position).sort((s1, s2) -> Integer.compare(s2.getOverallRating(), s1.getOverallRating()));
        }
    }
    
    public void generateLists(){
        allTeams = new HashMap<>(){{
            put("NFC", new HashMap<>(){{
                put("NORTH", new ArrayList<>());
                put("SOUTH", new ArrayList<>());
                put("EAST", new ArrayList<>());
                put("WEST", new ArrayList<>());
            }});
            put("AFC", new HashMap<>(){{
                put("NORTH", new ArrayList<>());
                put("SOUTH", new ArrayList<>());
                put("EAST", new ArrayList<>());
                put("WEST", new ArrayList<>());
            }});
        }};
        positionPlayers = new HashMap<>() {{
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
        maxGoodPlayers = new HashMap <>() {{
            put("QB", 24);
            put("HB", 24);
            put("WR", 50);
            put("OL", 64);
            put("DL", 64);
            put("LB", 50);
            put("CB", 32);
            put("S", 24);
            put("K", 9);
        }};
        baseTotalPlayerCount = new HashMap<>(){{
            put("QB", 192);
            put("HB", 256);
            put("WR", 576);
            put("OL", 640);
            put("DL", 640);
            put("LB", 512);
            put("CB", 288);
            put("S", 256);
            put("K", 92);
        }};
    }
    public void generateNames() {
        String line;
        String splitBy = ",";
        try
        {
            BufferedReader fn = new BufferedReader(new FileReader("src/main/resources/Names/firstNames.csv"));
            BufferedReader ln = new BufferedReader(new FileReader("src/main/resources/Names/lastNames.csv"));
            BufferedReader tn = new BufferedReader(new FileReader("src/main/resources/Names/teamNames.csv"));
            while ((line = fn.readLine()) != null)
            {
                firstNames.add(line.split(splitBy)[1].toUpperCase());
            }
            while ((line = ln.readLine()) != null)
            {
                lastNames.add(line.split(splitBy)[1].toUpperCase());
            }
            while ((line = tn.readLine()) != null)
            {
                teamNames.add(line.split(splitBy)[2].toUpperCase());
                teamCities.add(line.split(splitBy)[1].toUpperCase());

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generatePlayers() {
        for(String position : allPositions){
            for(int i = 0; i < baseTotalPlayerCount.get(position); i++){
                int val = i <= baseTotalPlayerCount.get(position)/8 ? 1 : baseTotalPlayerCount.get(position)/i;
                Player p = new Player(firstNames.get(getRandomNumber(1, firstNames.size()) - 1), lastNames.get(getRandomNumber(1, lastNames.size() - 1)), getRandomNumber(22, 35), position, maxGoodPlayers.get(position), val);
                maxGoodPlayers.replace(position, maxGoodPlayers.get(position) - 1);
                allPlayers.add(p);
                positionPlayers.get(position).add(p);
            }
        }
	    for (String s : Arrays.asList("QB", "HB", "WR", "OL", "DL", "LB", "CB", "S", "K")) {
		    positionPlayers.get(s).sort((s1, s2) -> Integer.compare(s2.getOverallRating(), s1.getOverallRating()));
	    }
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    
}