package cfranking.controller;

import cfranking.parser.PreviousWeek;
import cfranking.parser.CurrentWeek;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import cfranking.model.Team;
import cfranking.model.TeamResult;

public class StatLoader {
    List<Team> teams;
	String errorMessage;
	
	public StatLoader(){
		teams = new ArrayList<>();
	}

	public boolean load() throws IOException  {
		PreviousWeek baseTeamData = new PreviousWeek();
		List<Team> baseTeams = baseTeamData.readInput();
		CurrentWeek weekData = new CurrentWeek();
		List<TeamResult> results = weekData.readInput();
		for(Team team : baseTeams){
			String name = team.getName();
			ListIterator<TeamResult> it = results.listIterator();
			Team finalTeam = null;
			while(it.hasNext() && finalTeam == null){
				TeamResult result = it.next();
				if(result.getName().equals(name)){
					finalTeam = addTeamData(team,result);
					results.remove(it.nextIndex() - 1);
				}
			}
			if(finalTeam == null){
				errorMessage = "Results could not be found for: " + name;
				return false;
			} else {
				teams.add(finalTeam);
			}
		}
		return true;
	}
	
	private Team addTeamData(Team team, TeamResult result){
		List<String> opponents = team.getTeamsPlayed();
		String teamPlayed = result.getOpponent();
		if(teamPlayed != null){
			opponents.add(result.getOpponent());

		}
		team.setTeamsPlayed(opponents);
		
		int totalMargin = team.getTotalMargin();
		team.setTotalMargin(totalMargin + result.getMargin());
		
		String gameResult = result.getResult();
		if(gameResult.equals("w")){
			int wins = team.getWins() + 1;
			team.setWins(wins);
		} else if (gameResult.equals("l")){
			int losses = team.getLosses() + 1;
			team.setLosses(losses);
		}
		
		team.setGamesPlayed(team.getLosses() + team.getWins());
		
		return team;
	}
	
	public List<Team> getTeams(){
		return this.teams;
	}
	
	public String getErrorMessage(){
		return errorMessage;
	}
	
}
