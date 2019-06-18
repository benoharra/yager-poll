package cfranking.controller;

import cfranking.config.ErrorPage;
import cfranking.model.Conference;
import cfranking.model.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConferenceMapper {

    private static final String PAC_12 = "pac-12";
    private static final String AMERICAN = "american";
    private static final String ACC = "acc";
    private static final String BIG_12 = "big-12";
    private static final String BIG_10 = "big-10";
    private static final String USA = "conference usa";
    private static final String MID_AMERICAN = "mac";
    private static final String MOUNTAIN_WEST = "mountain west";
    private static final String SEC = "sec";
    private static final String SUN_BELT = "sun belt";
    public static final String INDEPENDENT = "independent";

    private Map<String, Conference> conferences = new HashMap<>();

    public ConferenceMapper() {
        initConferenceMap();
    }


    public Map<String, Conference> getConferenceWithStrengths(List<Team> teams) {
        // Add each team stats to their proper conference
        teams.forEach(this::addTeamToConference);

        // Set the conference stats for each conference
        conferences.forEach((name, conference) -> conference.setConferenceAverages());

        // Create a sorted list of conference strengths
        List<Float> conferenceStrengths = conferences.values().stream()
                .map(Conference::getStrength)
                .sorted()
                .collect(Collectors.toList());

        // Assign each conference's ranking based on it's order in the strengths list
        conferences.forEach((name, conference) -> setConferenceStrengthRank(conferenceStrengths, conference));

        // Create a sorted list of conference strength of schedules
        List<Float> conferenceStrengthOfSchedule = conferences.values().stream()
                .map(Conference::getStrengthOfSchedules)
                .sorted()
                .collect(Collectors.toList());

        // Assign each conference's strength of schedule ranking based on it's order
        conferences.forEach((name, conference) -> setConferenceStrengthOfScheduleRank(conferenceStrengthOfSchedule, conference));

        return conferences;
    }

    private void addTeamToConference(Team team) {
        String teamConference = team.getConference();
        // If a team's conference is found, add their stats to the conference
        if(conferences.containsKey(teamConference)) {
            setTeamIntoConference(team, conferences.get(teamConference));
        } else if(!teamConference.equals(INDEPENDENT)) {
            // If a team's conference isn't found and they're not an independent school then the data is incorrect
            ErrorPage.writeError("Team:" + team.getName() + " has an invalid conference " + teamConference);
        }
    }

    private void setTeamIntoConference(Team team, Conference conference) {
        conference.setRank(team.getPreviousRank());
        conference.setStrengthOfSchedules(team.getStrengthOfScheduleRaw());
    }

    private void setConferenceStrengthRank(List<Float> allConferenceStrengths, Conference conference) {
        conference.setConferenceRank(
                allConferenceStrengths.indexOf(conference.getStrength()) + 1
        );
    }

    private void setConferenceStrengthOfScheduleRank(List<Float> allConferenceSOS, Conference conference) {
        conference.setStrengthOfScheduleRank(
                allConferenceSOS.indexOf(conference.getStrengthOfSchedules()) + 1
        );
    }

    private void initConferenceMap() {
        conferences.put(PAC_12, new Conference());
        conferences.put(AMERICAN, new Conference());
        conferences.put(ACC, new Conference());
        conferences.put(BIG_12, new Conference());
        conferences.put(BIG_10, new Conference());
        conferences.put(USA, new Conference());
        conferences.put(MID_AMERICAN, new Conference());
        conferences.put(MOUNTAIN_WEST, new Conference());
        conferences.put(SEC, new Conference());
        conferences.put(SUN_BELT, new Conference());
    }

}
