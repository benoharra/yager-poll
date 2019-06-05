package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import model.Team;
import model.Conference;

public class AdjustStats {

	private static List<Float> winningPercentage;
	private static List<Float> strengthOfSchedule;
	private static List<Float> marginRank;
	private static ArrayList<Team> teams;
	private static HashMap<String, Integer> previousRank;
	private static List<Float> sosConferences;
	private static HashMap<Float, Integer> sosCrankMap;
	private static HashMap<String, Integer> conferenceRank;
		
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
	

	public static ArrayList<Team> calculate(ArrayList<Team> teamsIn) throws IllegalArgumentException {
		init();
		teams = teamsIn;
		setWinPercentage();
		setStrengthOfSchedule();
		setMarginRank();
		setConferenceStrength();
		return teams;
	}

	private static void init() {
		previousRank = new HashMap<>();
		teams = new ArrayList<>();
		sosCrankMap = new HashMap<>();
		winningPercentage = new ArrayList<>();
		strengthOfSchedule = new ArrayList<>();
		marginRank = new ArrayList<>();
		previousRank = new HashMap<>();
		sosConferences = new ArrayList<>();
		conferenceRank = new HashMap<>();
	}

	private static void setWinPercentage() {
		for (Team team : teams) {
			float winPercentage = (float)team.getWins() / (float)team.getGamesPlayed();
			winningPercentage.add(winPercentage);
			team.setWinPercentage(winPercentage);
		}
		Collections.sort(winningPercentage);
		Collections.reverse(winningPercentage);
		for (Team team : teams) {
			int rank = winningPercentage.indexOf(team.getWinPercentage()) + 1;
			team.setWinPercentageRank(rank);
		}
		return;
	}

	private static void setStrengthOfSchedule() {
		for (Team team : teams) {
			previousRank.put(team.getName(), team.getPreviousRank());
		}
		for (Team team : teams) {
			ArrayList<String> teamsPlayed = team.getTeamsPlayed();
			int totalRank = 0;
			for (String name : teamsPlayed) {
				if (name.contains("fcs")) {
					totalRank = totalRank + Integer.parseInt(name.substring(3, name.length()));
				} else {
					try{
						totalRank = totalRank + previousRank.get(name);
					} catch(NullPointerException e){
						ErrorPage.writeError("Null Pointer Exception for team: " + name);
					}
				}
			}
			float sos = (float)totalRank/(float)team.getGamesPlayed();
			strengthOfSchedule.add(sos);
			team.setStrengthOfScheduleRaw(sos);
		}
		Collections.sort(strengthOfSchedule);
		for (Team team : teams) {
			int rank = strengthOfSchedule.indexOf(team.getStrengthOfScheduleRaw()) + 1;
			team.setStrengthOfScheduleRank(rank);
		}
		return;
	}

	private static void setMarginRank() {
		for (Team team : teams) {
			marginRank.add((float)team.getTotalMargin()/(float)team.getGamesPlayed());
		}
		Collections.sort(marginRank);
		Collections.reverse(marginRank);
		for (Team team : teams) {
			int rank = marginRank.indexOf((float)team.getTotalMargin()/(float)team.getGamesPlayed()) + 1;
			team.setMarginRank(rank);
		}
	}

	private static void setConferenceStrength() throws IllegalArgumentException {
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
		for(Team team : teams) {
			String conference = team.getConference();
			if(conference.equals("independent")){
				team.setConferenceStrength(getIndependentStrength(team.getStrengthOfScheduleRaw()));
			} else {
				team.setConferenceStrength(conferenceRank.get(conference));
			}
		}
	}
	
	private static void sortConferences(){
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
	
	private static void getConferenceAverages(){
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
	
	private static void setSOSRankMap(){
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
	
	private static int getIndependentStrength(float sos){
		int count = 0;
		while(count < sosConferences.size() && sosConferences.get(count) < sos){
			count++;
		}
		if(count == 0){
			return sosCrankMap.get(sosConferences.get(0));
		}
		float lowerSOS = sosConferences.get(count-1);
		float upperSOS = 0.0f;
		if(count != sosConferences.size()){
			upperSOS = sosConferences.get(count);
		}
		if(upperSOS == 0.0f){
			return 117;
		}
		int upperCrank = sosCrankMap.get(upperSOS);
		int lowerCrank = sosCrankMap.get(lowerSOS);
		
		if ((sos - lowerSOS) < (upperSOS - sos)){
			return lowerCrank;
		} else {
			return upperCrank;
		}
	}
	
	private static void buildConferenceStrengths(){
		HashMap<Float, String> data = new HashMap<>();
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
		
		TreeMap<Float, String> sortedData = new TreeMap<Float,String>(data);
		int count = 0;
		for(Entry<Float, String> entry : sortedData.entrySet()){
			if(count == 0){
				conferenceRank.put(entry.getValue(), 1);
			} else {
				conferenceRank.put(entry.getValue(), count*13);
			}
			count++;
		}
		
	}

}
