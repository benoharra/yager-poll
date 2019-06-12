package cfranking.parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import cfranking.controller.ErrorPage;
import cfranking.model.Team;

public class PreviousWeek {

	private String filePath = ErrorPage.dirHome + "PreviousWeek.csv";

	public List<Team> readInput(){
		File file = new File(filePath);
		ArrayList<Team> teams = new ArrayList<>();
		try {
			CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.EXCEL);
			int count = 0;
			for(CSVRecord record : parser) {
				if(count == 0){
					count++;
					continue;
				}
				Team team =  new Team();
				team.setPreviousRank(Integer.valueOf(record.get(0)));
				team.setName(record.get(1));
				team.setWins(Integer.valueOf(record.get(2)));
				team.setLosses(Integer.valueOf(record.get(3)));
				team.setGamesPlayed(getGamesPlayed(team));
				team.setConference(record.get(4));
				team.setTotalMargin(Integer.valueOf(record.get(5)));
				team.setTeamsPlayed(getTeamsPlayed(record.get(6)));
				team.setOpinion(Integer.valueOf(record.get(7)));
				teams.add(team);
				count++;
			}
		} catch (IOException e) {
			ErrorPage.writeError("Previous week input failed with cfranking.exception: " + e.toString());
		}
		return teams;
	}
	
	private int getGamesPlayed(Team team){
		int wins = team.getWins();
		int losses = team.getLosses();
		return wins + losses;
	}
	
	private List<String> getTeamsPlayed(String teamList){
		ArrayList<String> teams = new ArrayList<>();
		while(teamList.contains(";")){
			String team = teamList.substring(0,teamList.indexOf(";")).toLowerCase();
			teams.add(team);
			teamList = teamList.substring(teamList.indexOf(";") + 1);
		}
		teams.add(teamList.toLowerCase());
		return teams;
	}
}
