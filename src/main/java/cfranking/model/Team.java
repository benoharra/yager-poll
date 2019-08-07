package cfranking.model;

import java.util.List;

public class Team {

	private String name;
	private int rank;
	private int wins;
	private int losses;
	private float winPercentage;
	private int gamesPlayed;
	private List<String> teamsPlayed;
	private int winPercentageRank;
	private float strengthOfScheduleRaw;
	private int strengthOfScheduleRank;
	private int totalMargin;
	private int marginRank;
	private String conference;
	private int conferenceStrength;
	private int previousRank;
	private int opinion;
	private float weightedScore;
	
	public String getName(){
		return name;
	}
	
	public int getRank(){
		return rank;
	}
	
	public int getWins(){
		return wins;
	}
	
	public int getLosses(){
		return losses;
	}
	
	public float getWinPercentage(){
		return winPercentage;
	}
	
	public int getGamesPlayed(){
		return gamesPlayed;
	}
	
	public List<String> getTeamsPlayed(){
		return teamsPlayed;
	}
	
	public int getWinPercentageRank(){
		return winPercentageRank;
	}
	
	public float getStrengthOfScheduleRaw(){
		return strengthOfScheduleRaw;
	}
	
	public int getStrengthofScheduleRank(){
		return strengthOfScheduleRank;
	}
	
	public int getTotalMargin(){
		return totalMargin;
	}
	
	public int getMarginRank(){
		return marginRank;
	}
	
	public String getConference(){
		return conference;
	}
	
	public int getConferenceStrength(){
		return conferenceStrength;
	}
	
	public int getPreviousRank(){
		return previousRank;
	}
	
	public int getOpinion(){
		return opinion;
	}

	public float getWeightedScore() {
		return weightedScore;
	}
	
	public void setName(String name){
		this.name = name.toUpperCase();
	}
	
	public void setRank(int rank){
		this.rank = rank;
	}
	
	public void setWins(int wins){
		this.wins = wins;
	}
	
	public void setLosses(int losses){
		this.losses = losses;
	}
	
	public void setWinPercentage(float winPercentage){
		this.winPercentage = winPercentage;
	}
	
	public void setGamesPlayed(int gamesPlayed){
		this.gamesPlayed = gamesPlayed;
	}
	
	public void setTeamsPlayed(List<String> teamsPlayed){
		this.teamsPlayed = teamsPlayed;
	}
	
	public void setWinPercentageRank(int winPercentageRank){
		this.winPercentageRank = winPercentageRank;
	}
	
	public void setStrengthOfScheduleRaw(float strengthOfScheduleRaw){
		this.strengthOfScheduleRaw = strengthOfScheduleRaw;
	}
	
	public void setStrengthOfScheduleRank(int strengthOfScheduleRank){
		this.strengthOfScheduleRank = strengthOfScheduleRank;
	}
	
	public void setTotalMargin(int totalMargin){
		this.totalMargin = totalMargin;
	}
	
	public void setMarginRank(int marginRank){
		this.marginRank = marginRank;
	}
	
	public void setConference(String conference){
		this.conference = conference.toLowerCase();
	}
	
	public void setConferenceStrength(int conferenceStrength){
		this.conferenceStrength = conferenceStrength;
	}
	
	public void setPreviousRank(int previousRank){
		this.previousRank = previousRank;
	}
	
	public void setOpinion(int opinion){
		this.opinion = opinion;
	}

	public void setWeightedScore(float weightedScore) {
		this.weightedScore = weightedScore;
	}
}
