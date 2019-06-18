package cfranking.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import cfranking.config.ConfigProps;
import cfranking.config.ErrorPage;
import cfranking.model.FactorWeights;
import cfranking.model.Team;

public class Rank {

	public static void execute(List<Team> teams, FactorWeights factorWeights) throws IOException {
		// Read the configuration for factor overrides
		readOverrideConfiguration(factorWeights);

		// Calculate the weighted score for each team and sort by highest rank
		teams.forEach(team -> team.setWeightedScore(calculateScore(team, factorWeights)));
		teams.sort(Comparator.comparing(Team::getWeightedScore));

		Printer printer = new Printer();

		int rank = 1;

		// Assign rankings and output each team to the result file
		for(Team team : teams) {
			team.setRank(rank);
			printer.addLine(team);
			rank++;
		}

		printer.close();
	}

	private static float calculateScore(Team team, FactorWeights factorWeights) {
		return factorWeights.getRecord() * team.getWinPercentageRank() +
				factorWeights.getStrengthOfSchedule() * team.getStrengthofScheduleRank() +
				factorWeights.getMargin() * team.getMarginRank() +
				factorWeights.getConferenceStrength() * team.getConferenceStrength() +
				factorWeights.getPreviousWeekRank() * team.getPreviousRank() +
				factorWeights.getOpinion() * team.getOpinion();
	}
	
	private static void readOverrideConfiguration(FactorWeights factorWeights){
		File cfg = new File(ConfigProps.OVERRIDE_FILE);
		if(cfg.exists()){
			ErrorPage.writeError("Override factors found, make sure you did that on purpose!");
			try{
				for(String line : Files.readAllLines(Paths.get(ConfigProps.OVERRIDE_FILE))){
					line = line.toLowerCase();
					if(line.contains("record")){
						factorWeights.setRecord(Float.parseFloat(getValue(line)));
					}else if(line.contains("sos")){
						factorWeights.setStrengthOfSchedule(Float.parseFloat(getValue(line)));
					} else if(line.contains("margin")){
						factorWeights.setMargin(Float.parseFloat(getValue(line)));
					} else if(line.contains("conference")){
						factorWeights.setConferenceStrength(Float.parseFloat(getValue(line)));
					} else if(line.contains("previousRank")){
						factorWeights.setPreviousWeekRank(Float.parseFloat(getValue(line)));
					} else if(line.contains("opinion")){
						factorWeights.setOpinion(Float.parseFloat(getValue(line)));
					} else {
						ErrorPage.writeError("Override config line " + line + " could not be read");
					}
				}
			} catch(IOException e){
				ErrorPage.writeError("Error reading override configuration file: " + e.getMessage());
			}
		}
	}
	
	private static String getValue(String line){
		int index = line.indexOf("=");
		return line.substring(index+1).trim();
	}
}
