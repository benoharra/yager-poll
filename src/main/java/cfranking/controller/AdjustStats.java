package cfranking.controller;

import java.util.*;
import java.util.stream.Collectors;

import cfranking.config.ErrorPage;
import cfranking.model.Team;
import cfranking.model.Conference;

public class AdjustStats {

    private static final int CONFERENCE_RANK_MULTIPLIER = 13;


    public static List<Team> calculate(List<Team> teams) throws IllegalArgumentException {
        setWinPercentage(teams);
        setStrengthOfSchedule(teams);
        setMarginRank(teams);
        setConferenceStrength(teams);
        return teams;
    }

    private static void setWinPercentage(List<Team> teams) {
        // Calculate the winning percentage for each team
        teams.forEach(team -> team.setWinPercentage(
                (float) team.getWins() / team.getGamesPlayed()));

        // Sort the list of all winning percentages from highest to lowest
        List<Float> winningPercentages = teams.stream()
                .map(Team::getWinPercentage)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        // Assign the winning percentage rank to each team
        teams.forEach(team ->
                team.setWinPercentageRank(
                        winningPercentages.indexOf(team.getWinPercentage()) + 1
                ));
    }

    private static void setStrengthOfSchedule(List<Team> teams) {
        // Create a map of team rankings for building strength of schedule
        Map<String, Integer> previousWeekRank = teams.stream()
                .collect(Collectors.toMap(Team::getName, Team::getPreviousRank));

        // Calculate strength of schedule for each team
        teams.forEach(team -> team.setStrengthOfScheduleRaw(
                getOpponentsAverageRanking(team, previousWeekRank)));

        // Map all strength of schedules to a list
        List<Float> strengthOfSchedules = teams.stream()
                .map(Team::getStrengthOfScheduleRaw)
                .sorted()
                .collect(Collectors.toList());

        // Set each team's strength of schedule ranking
        teams.forEach(team -> team.setStrengthOfScheduleRank(
                strengthOfSchedules.indexOf(team.getStrengthOfScheduleRaw()) + 1
        ));
    }

    private static float getOpponentsAverageRanking(Team team, Map<String, Integer> teamRanks) {
        int total = 0;
        // Loop through the opponents for each team and add their ranks
        for(String opponent : team.getTeamsPlayed()) {
            if(opponent.contains("fcs")) {
                total += Integer.parseInt(opponent.substring(3));
            } else {
                try {
                    total += Optional.ofNullable(teamRanks.get(opponent))
                            .orElseThrow(IllegalArgumentException::new);
                } catch (IllegalArgumentException e) {
                    ErrorPage.writeError("Opponent:" + opponent + " not found for " + team.getName());
                }
            }
        }
        // return the average ranking of a teams opponents
        return (float) total / team.getGamesPlayed();
    }

    private static void setMarginRank(List<Team> teams) {
        // Create a sorted list of teams' average margin from highest to lowest
        List<Float> averageMargins = teams.stream()
                .map(team -> (float) team.getTotalMargin() / team.getGamesPlayed())
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        // Assign the rank of each team's average margin
        teams.forEach(team -> team.setMarginRank(
                averageMargins.indexOf((float) team.getTotalMargin() / team.getGamesPlayed()) + 1
        ));
    }

    private static void setConferenceStrength(List<Team> teams) throws IllegalArgumentException {
        ConferenceMapper conferenceMapper = new ConferenceMapper();

        // Build the map of conferences with all teams input
        Map<String, Conference> conferences = conferenceMapper.getConferenceWithStrengths(teams);

        // Assign each team's conference ranking
        teams.forEach(team -> {
            if(!team.getConference().equals(ConferenceMapper.INDEPENDENT)) {
                setTeamConferenceRankings(team, conferences);
            } else {
                setIndependentTeamStrength(team, conferences.values());
            }}
        );
    }

    private static void setTeamConferenceRankings(Team team, Map<String, Conference> conferences) {
        int teamConferenceRank = conferences.get(team.getConference()).getConferenceRank();
        if(teamConferenceRank == 1) {
            team.setConferenceStrength(1);
        } else {
            team.setConferenceStrength(teamConferenceRank * CONFERENCE_RANK_MULTIPLIER);
        }
    }

    private static void setIndependentTeamStrength(Team team, Collection<Conference> conferences) {
        List<Conference> conferencesSortedByStrengthOfSchedule = conferences.stream()
                .sorted(Comparator.comparing(Conference::getStrengthOfScheduleRank))
                .collect(Collectors.toList());

        for(int i = 0; i < conferences.size(); i++) {
            // Find the first conference where the team's average opponent rank is less than the conference average opp weight
            if(team.getStrengthOfScheduleRaw() < conferencesSortedByStrengthOfSchedule.get(i).getStrengthOfSchedules()) {
                // Return the whichever conference has the closest opponent rank to the team being analyzed
                if(i == 0) {
                    // Independent team has a stronger schedule than the average for any conference
                    team.setConferenceStrength(1);
                } else if(isStrengthClosestToHigherConference(conferencesSortedByStrengthOfSchedule.get(i-1),
                                                              conferencesSortedByStrengthOfSchedule.get(i),
                                                              team)) {
                    // If the team's strength is closer to the conference ranked just above, return the previous conference rank
                    team.setConferenceStrength(conferencesSortedByStrengthOfSchedule.get(i-1).getConferenceRank() * CONFERENCE_RANK_MULTIPLIER);
                } else {
                    team.setConferenceStrength(conferencesSortedByStrengthOfSchedule.get(i).getConferenceRank() * CONFERENCE_RANK_MULTIPLIER);
                }
                return;
            }
        }

    }

    private static boolean isStrengthClosestToHigherConference(Conference higherRankedConference,
                                                               Conference lowerRankedConference,
                                                               Team team) {
        return (team.getStrengthOfScheduleRaw() - higherRankedConference.getStrengthOfSchedules()) <
                (lowerRankedConference.getStrengthOfSchedules() - team.getStrengthOfScheduleRaw());
    }

}
