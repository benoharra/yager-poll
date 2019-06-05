package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import model.Team;

public class Rank {

	private static float winPercentMult = 5.55f;
	private static float sosMult = 2.92f;
	private static float marginMult = 2.5f;
	private static float confMult = 0.485f;
	private static float previousRankMult = 3f;
	private static float opinionMult = 1.5f;

	private static Map<Float, Team> teamScores;

	public static void execute(ArrayList<Team> teams) throws IOException{
		readConfiguration();
		teamScores = new HashMap<>();
		for(Team team : teams){
			float score = winPercentMult*team.getWinPercentageRank() + sosMult*team.getStrengthofScheduleRank() + marginMult*team.getMarginRank() + confMult*team.getConferenceStrength() 
						  	+ previousRankMult*team.getPreviousRank() + opinionMult*team.getOpinion();
			teamScores.put(score, team);
		}
		teamScores = new TreeMap<Float,Team>(teamScores);
		Printer printer = new Printer();
		int rank = 1;
		for(Entry<Float, Team> entry : teamScores.entrySet()){
			Team current = entry.getValue();
			current.setRank(rank);
			printer.addLine(current);
			rank++;
		}
		printer.close();
	}
	
	private static void readConfiguration(){
		File cfg = new File(ErrorPage.dirHome + "cfg.txt");
		if(!cfg.exists()){
			ErrorPage.writeError("Configuration could not be found");
			return;
		}
		try{
			for(String line : Files.readAllLines(Paths.get(ErrorPage.dirHome + "cfg.txt"))){
				line = line.toLowerCase();
				if(line.contains("win")){
					winPercentMult = Float.parseFloat(getValue(line));
				}else if(line.contains("schedule")){
					sosMult = Float.parseFloat(getValue(line));
				} else if(line.contains("margin")){
					marginMult = Float.parseFloat(getValue(line));
				} else if(line.contains("conference")){
					confMult = Float.parseFloat(getValue(line));
				} else if(line.contains("previous")){
					previousRankMult = Float.parseFloat(getValue(line));
				} else if(line.contains("opinion")){
					opinionMult = Float.parseFloat(getValue(line));
				} else {
					ErrorPage.writeError("Configuration line " + line + " could not be read");
				}
			}
		} catch(IOException e){
			ErrorPage.writeError("Error reading configuration file: " + e.toString());
		}	
	}
	
	private static String getValue(String line){
		int index = line.indexOf("=");
		return line.substring(index+1, line.length()).trim();
	}
}
