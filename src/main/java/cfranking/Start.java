package cfranking;

import java.io.IOException;
import java.util.List;

import cfranking.controller.AdjustStats;
import cfranking.config.ErrorPage;
import cfranking.controller.Rank;
import cfranking.controller.StatLoader;
import cfranking.model.FactorWeights;
import cfranking.model.Team;

public class Start {

	public static void main(String[] args) {
		StatLoader stats = new StatLoader();
		try {
			if(stats.load()) {
				calculateRanking(stats.getTeams(), stats.getFactorWeights());
			}
		} catch (IllegalArgumentException | IOException e) {
			ErrorPage.writeError("Calculation Error: " + e.toString());
		} finally {
			ErrorPage.close();
		}
	}

	private static void calculateRanking(List<Team> teams, FactorWeights factorWeights) throws IOException {
		List<Team> fullStatTeams = AdjustStats.calculate(teams);
		Rank.execute(fullStatTeams, factorWeights);
	}

}
