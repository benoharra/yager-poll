package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Team;

public class Start {

	public static void main(String[] args) {
		StatLoader stats = new StatLoader();
		try {
			if(stats.load()) {
				calculateRanking(stats.getTeams());
			} else {
				// Adjust this to be in catch block
				ErrorPage.writeError(stats.getErrorMessage());
			}

		} catch (IllegalArgumentException | IOException e) {
			ErrorPage.writeError("Calculation Error: " + e.toString());
		} finally {
			ErrorPage.close();
		}
	}

	private static void calculateRanking(List<Team> teams) throws IOException {
		List<Team> fullStatTeams = AdjustStats.calculate(teams);
		Rank.execute(fullStatTeams);
	}

}
