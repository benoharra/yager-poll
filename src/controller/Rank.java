package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

import model.Team;

public class Rank {

	// TODO:Move to config file
	private static float winPercentMult = 5.55f;
	private static float sosMult = 2.92f;
	private static float marginMult = 2.5f;
	private static float confMult = 0.485f;
	private static float previousRankMult = 3f;
	private static float opinionMult = 1.5f;

	private static Map<Float, Team> teamScores;

	public static void execute(List<Team> teams) throws IOException{
		readConfiguration();
		teamScores = new HashMap<>();
		teams.forEach(team -> teamScores.put(calculateScore(team), team));
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

	private static float calculateScore(Team team) {
		return winPercentMult*team.getWinPercentageRank() + sosMult*team.getStrengthofScheduleRank() + marginMult*team.getMarginRank() + confMult*team.getConferenceStrength()
				+ previousRankMult*team.getPreviousRank() + opinionMult*team.getOpinion();
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
		return line.substring(index+1).trim();
	}
}
