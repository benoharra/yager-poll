package controller;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import model.Team;
import model.Conference;

public class AdjustStats {

    private static List<Float> strengthOfSchedule = new ArrayList<>();
    private static List<Float> marginRank = new ArrayList<>();
    private static Map<String, Integer> previousRank = new HashMap<>();
    private static List<Float> sosConferences = new ArrayList<>();
    private static Map<Float, Integer> sosCrankMap = new HashMap<>();
    private static Map<String, Integer> conferenceRank = new HashMap<>();


    private static Conference pac12 = new Conference();
    private static Conference american = new Conference();
    private static Conference acc = new Conference();
    private static Conference big12 = new Conference();
    private static Conference big10 = new Conference();
    private static Conference usa = new Conference();
    private static Conference midAmerican = new Conference();
    private static Conference mountainWest = new Conference();
    private static Conference sec = new Conference();
    private static Conference sunBelt = new Conference();


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


        for (Team team : teams) {
            String conference = team.getConference();
            switch (conference) {
                case "pac-12":
                    pac12.setRank(team.getPreviousRank());
                    pac12.setStrengthOfSchedules(team.getStrengthOfScheduleRaw());
                    break;
                case "big-12":
                    big12.setRank(team.getPreviousRank());
                    big12.setStrengthOfSchedules(team.getStrengthOfScheduleRaw());
                    break;
                case "acc":
                    acc.setRank(team.getPreviousRank());
                    acc.setStrengthOfSchedules(team.getStrengthOfScheduleRaw());
                    break;
                case "sun belt":
                    sunBelt.setRank(team.getPreviousRank());
                    sunBelt.setStrengthOfSchedules(team.getStrengthOfScheduleRaw());
                    break;
                case "conference usa":
                    usa.setRank(team.getPreviousRank());
                    usa.setStrengthOfSchedules(team.getStrengthOfScheduleRaw());
                    break;
                case "big-10":
                    big10.setRank(team.getPreviousRank());
                    big10.setStrengthOfSchedules(team.getStrengthOfScheduleRaw());
                    break;
                case "american":
                    american.setRank(team.getPreviousRank());
                    american.setStrengthOfSchedules(team.getStrengthOfScheduleRaw());
                    break;
                case "mac":
                    midAmerican.setRank(team.getPreviousRank());
                    midAmerican.setStrengthOfSchedules(team.getStrengthOfScheduleRaw());
                    break;
                case "mountain west":
                    mountainWest.setRank(team.getPreviousRank());
                    mountainWest.setStrengthOfSchedules(team.getStrengthOfScheduleRaw());
                    break;
                case "sec":
                    sec.setRank(team.getPreviousRank());
                    sec.setStrengthOfSchedules(team.getStrengthOfScheduleRaw());
                    break;
                case "independent":
                    break;
                default:
                    throw new IllegalArgumentException("Team conference " + conference + " for " + team.getName() + " is invalid");
            }
        }
        sortConferences();
        getConferenceAverages();
        buildConferenceStrengths();
        setSOSRankMap();
        for (Team team : teams) {
            String conference = team.getConference();
            if (conference.equals("independent")) {
                team.setConferenceStrength(getIndependentStrength(team.getStrengthOfScheduleRaw()));
            } else {
                team.setConferenceStrength(conferenceRank.get(conference));
            }
        }
    }

    private static void sortConferences() {
        pac12.sort();
        big12.sort();
        usa.sort();
        american.sort();
        midAmerican.sort();
        acc.sort();
        sunBelt.sort();
        big10.sort();
        mountainWest.sort();
        sec.sort();
    }

    private static void getConferenceAverages() {
        pac12.setConferenceAverages();
        big12.setConferenceAverages();
        usa.setConferenceAverages();
        american.setConferenceAverages();
        midAmerican.setConferenceAverages();
        acc.setConferenceAverages();
        sunBelt.setConferenceAverages();
        big10.setConferenceAverages();
        mountainWest.setConferenceAverages();
        sec.setConferenceAverages();

        sosConferences.add(pac12.getStrengthOfSchedules());
        sosConferences.add(big12.getStrengthOfSchedules());
        sosConferences.add(usa.getStrengthOfSchedules());
        sosConferences.add(american.getStrengthOfSchedules());
        sosConferences.add(midAmerican.getStrengthOfSchedules());
        sosConferences.add(acc.getStrengthOfSchedules());
        sosConferences.add(sunBelt.getStrengthOfSchedules());
        sosConferences.add(big10.getStrengthOfSchedules());
        sosConferences.add(mountainWest.getStrengthOfSchedules());
        sosConferences.add(sec.getStrengthOfSchedules());

        Collections.sort(sosConferences);
    }

    private static void setSOSRankMap() {
        sosCrankMap.put(pac12.getStrengthOfSchedules(), conferenceRank.get("pac-12"));
        sosCrankMap.put(big12.getStrengthOfSchedules(), conferenceRank.get("big-12"));
        sosCrankMap.put(usa.getStrengthOfSchedules(), conferenceRank.get("conference usa"));
        sosCrankMap.put(american.getStrengthOfSchedules(), conferenceRank.get("american"));
        sosCrankMap.put(midAmerican.getStrengthOfSchedules(), conferenceRank.get("mac"));
        sosCrankMap.put(acc.getStrengthOfSchedules(), conferenceRank.get("acc"));
        sosCrankMap.put(sunBelt.getStrengthOfSchedules(), conferenceRank.get("sun belt"));
        sosCrankMap.put(big10.getStrengthOfSchedules(), conferenceRank.get("big-10"));
        sosCrankMap.put(mountainWest.getStrengthOfSchedules(), conferenceRank.get("mountain west"));
        sosCrankMap.put(sec.getStrengthOfSchedules(), conferenceRank.get("sec"));
    }

    private static int getIndependentStrength(float sos) {
        int count = 0;
        while (count < sosConferences.size() && sosConferences.get(count) < sos) {
            count++;
        }
        if (count == 0) {
            return sosCrankMap.get(sosConferences.get(0));
        }
        float lowerSOS = sosConferences.get(count - 1);
        float upperSOS = 0.0f;
        if (count != sosConferences.size()) {
            upperSOS = sosConferences.get(count);
        }
        if (upperSOS == 0.0f) {
            return 117;
        }
        int upperCrank = sosCrankMap.get(upperSOS);
        int lowerCrank = sosCrankMap.get(lowerSOS);

        if ((sos - lowerSOS) < (upperSOS - sos)) {
            return lowerCrank;
        } else {
            return upperCrank;
        }
    }

    private static void buildConferenceStrengths() {
         Map<Float, String> data = new HashMap<>();
        data.put(pac12.getStrength(), "pac-12");
        data.put(big12.getStrength(), "big-12");
        data.put(usa.getStrength(), "conference usa");
        data.put(american.getStrength(), "american");
        data.put(midAmerican.getStrength(), "mac");
        data.put(acc.getStrength(), "acc");
        data.put(sunBelt.getStrength(), "sun belt");
        data.put(big10.getStrength(), "big-10");
        data.put(mountainWest.getStrength(), "mountain west");
        data.put(sec.getStrength(), "sec");

        TreeMap<Float, String> sortedData = new TreeMap<Float, String>(data);
        int count = 0;
        for (Entry<Float, String> entry : sortedData.entrySet()) {
            if (count == 0) {
                conferenceRank.put(entry.getValue(), 1);
            } else {
                conferenceRank.put(entry.getValue(), count * 13);
            }
            count++;
        }

    }

}
