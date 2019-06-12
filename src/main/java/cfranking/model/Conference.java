package cfranking.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cfranking.controller.ErrorPage;

public class Conference {
	private List<Integer> ranks = new ArrayList<>();
	private float average;
	private float top;
	private float bottom;
	private float strength;
	private List<Float> strengthOfSchedules = new ArrayList<>();
	private float strengthOfSchedule;
	private int conferenceRank;
	private int strengthOfScheduleRank;

	public List<Integer> getRanks() {
		return ranks;
	}

	public float getAverage() {
		return average;
	}

	public float getTop() {
		return top;
	}

	public float getBottom() {
		return bottom;
	}
	
	public float getStrength() {
		return strength;
	}
	
	public float getStrengthOfSchedules(){
		return strengthOfSchedule;
	}

	public int getConferenceRank() {
		return conferenceRank;
	}

	public int getStrengthOfScheduleRank() {
		return strengthOfScheduleRank;
	}

	public void setRank(int rank) {
		ranks.add(rank);
	}

	public void setAverage(float average) {
		this.average = average;
	}

	public void setTop(float top) {
		this.top = top;
	}

	public void setBottom(float bottom) {
		this.bottom = bottom;
	}
	
	public void setStrengthOfSchedules(float sos){
		strengthOfSchedules.add(sos);
	}

	public void setConferenceRank(int conferenceRank) {
		this.conferenceRank = conferenceRank;
	}

	public void setStrengthOfScheduleRank(int strengthOfScheduleRank) {
		this.strengthOfScheduleRank = strengthOfScheduleRank;
	}
	
	public void setConferenceAverages(){
		// Sort ranks from top to bottom
		Collections.sort(ranks);

		int bottomCurrent = 0;
		float total = 0;
		int numberOfTeams = ranks.size();
		if(numberOfTeams == 0){
			ErrorPage.writeError("Missing team ranks for conference");
			return;
		}
		float totalSOS = 0;
		for(int i = 0; i < numberOfTeams; i++){
			total = total + ranks.get(i);
			totalSOS = totalSOS + strengthOfSchedules.get(i);
			// Set the average ranking of top 3 teams
			if(i == 2){
				top = total / 3;
			} else if (i > numberOfTeams - 4){
				bottomCurrent = bottomCurrent + ranks.get(i);
			}
		}
		average = total/numberOfTeams;
		// Set the average ranking of bottom 3 teams
		bottom = bottomCurrent/3f;
		strength = (float) (average*3.25 + top*2.5 + bottom*1.5);
		strengthOfSchedule = totalSOS/numberOfTeams;
	}

}
