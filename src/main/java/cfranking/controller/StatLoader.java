package cfranking.controller;

import cfranking.config.ErrorPage;
import cfranking.model.FactorWeights;
import cfranking.parser.PreviousWeek;
import cfranking.parser.CurrentWeek;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import cfranking.model.Team;
import cfranking.model.TeamResult;
import org.apache.commons.lang3.StringUtils;

public class StatLoader {
    private List<Team> teams;
	private FactorWeights factorWeights;

	
	public StatLoader(){
		teams = new ArrayList<>();
	}

	public boolean load() throws IOException  {
		PreviousWeek baseTeamData = new PreviousWeek();
		List<Team> baseTeams = baseTeamData.readInput();
		CurrentWeek weekData = new CurrentWeek();
		// Read the results for each team and the week number
		Map<String, TeamResult> results = weekData.readInput();

		// Get the factor weights from the parsed currentWeek input
		factorWeights = weekData.getFactorWeights();

		boolean successfullyLoadedAllTeams = true;
		for(Team team : baseTeams) {
			TeamResult result = results.get(team.getName());
			if(result != null) {
				teams.add(buildTeamData(team, result));
			} else {
				ErrorPage.writeError("Results could not be found for:" + team.getName());
				successfullyLoadedAllTeams = false;
			}
		}

		return successfullyLoadedAllTeams;
	}

	public FactorWeights getFactorWeights() {
		return factorWeights;
	}
	
	private Team buildTeamData(Team team, TeamResult result){
		List<String> opponents = team.getTeamsPlayed();
		String teamPlayed = result.getOpponent();
		if(StringUtils.isNotEmpty(teamPlayed)){
			opponents.add(teamPlayed.toLowerCase());
		}
		team.setTeamsPlayed(opponents);
		
		int totalMargin = team.getTotalMargin();
		team.setTotalMargin(totalMargin + result.getMargin());
		
		String gameResult = result.getResult();
		if(gameResult.equalsIgnoreCase("w")){
			int wins = team.getWins() + 1;
			team.setWins(wins);
		} else if (gameResult.equalsIgnoreCase("l")){
			int losses = team.getLosses() + 1;
			team.setLosses(losses);
		}
		
		team.setGamesPlayed(team.getLosses() + team.getWins());
		
		return team;
	}
	
	public List<Team> getTeams(){
		return this.teams;
	}
	
}
