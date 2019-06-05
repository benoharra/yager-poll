package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.text.WordUtils;

import model.Team;

public class Printer {
	
	FileWriter fw_rank;
	FileWriter fw_data;
	
	BufferedWriter bw_data;
	BufferedWriter bw_rank;
	
	File rank;
	File data;
	
	public Printer() throws IOException{
		rank = new File("C:\\Users\\ospre\\Rankings\\Ranks.txt");
		data = new File("C:\\Users\\ospre\\Rankings\\Full Data.csv");
		
		rank.createNewFile();
		data.createNewFile();
		
		fw_rank = new FileWriter(rank.getAbsoluteFile());
		fw_data = new FileWriter(data.getAbsoluteFile());
		
		bw_rank = new BufferedWriter(fw_rank);
		bw_data = new BufferedWriter(fw_data);
		addHeaders();
	}
	
	private void addHeaders(){
		String rankTitle = "Rank   Team";
		String dataTitle = "Rank,Team,Wins,Losses,Conference,Total Margin,Teams Played,Opinion,Win Percentage,Win Percentage Rank,Strength of Schedule Rank,SOS Raw,"
				+ "Margin Rank,Conference Strength,Previous";
		try{
			bw_rank.write(rankTitle);
			bw_data.write(dataTitle);
			bw_rank.write(System.getProperty("line.separator"));
			bw_data.write(System.getProperty("line.separator"));
		} catch(IOException e){
			ErrorPage.writeError("Headers couldn't be added" + e.toString());
		}
	}
	
	public void addLine(Team team){
		String line = "";
		String data = "";
		line = line + team.getRank() + ".     " + WordUtils.capitalize(team.getName());
		data = data + team.getRank() + "," + WordUtils.capitalize(team.getName()) + "," + team.getWins() + "," + team.getLosses() + "," + 
				WordUtils.capitalize(team.getConference()) + "," + team.getTotalMargin() + "," + getTeamsPlayed(team) + "," + team.getOpinion() + "," + team.getWinPercentage() + "," +
				team.getWinPercentageRank() + "," + team.getStrengthofScheduleRank() + "," + team.getStrengthOfScheduleRaw() + "," + team.getMarginRank() + "," + 
				team.getConferenceStrength() + "," + team.getPreviousRank();
		try {
			bw_rank.write(line);
			bw_rank.write(System.getProperty("line.separator"));
			bw_data.write(data);
			bw_data.write(System.getProperty("line.separator"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ErrorPage.writeError("Exception with Team: " + team.getName() + " Exception: " + e.toString());
		}
		
	}
	
	private String getTeamsPlayed(Team team){
		ArrayList<String> teams = team.getTeamsPlayed();
		String ret = "";
		for(String played : teams){
			ret = ret + WordUtils.capitalize(played) + ";";
		}
		return ret.substring(0,(ret.length()-1));
	}
	
	public void close(){
		try {
			bw_rank.close();
			bw_data.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ErrorPage.writeError("Buffered Writers didn't close, " + e.toString());;
		}
	}

}
