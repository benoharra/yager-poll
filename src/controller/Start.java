package controller;

import java.io.IOException;
import java.util.ArrayList;

import model.Team;

public class Start {

	public static void main(String[] args) {
		StatLoader stats = new StatLoader();
		try {
			if(stats.load()) {
				Rank.execute(AdjustStats.calculate(stats.getTeams()));
			} else {
				
			}

		}
		ArrayList<Team> teams;
		if (loaded) {
			try {
				teams = AdjustStats.calculate(stats.getTeams());
				Rank.execute(teams);
			} catch (IllegalArgumentException | IOException e) {
				ErrorPage.writeError("Calculation Error: " + e.toString());
			}
		} else {
			ErrorPage.writeError(stats.getErrorMessage());
		}
		ErrorPage.close();
	}

}
