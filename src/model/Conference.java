package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import controller.ErrorPage;

public class Conference {
	private List<Integer> ranks = new ArrayList<>();
	private float average;
	private float top;
	private float bottom;
	private float strength;
	private List<Float> strengthOfSchedules = new ArrayList<>();
	private float strengthOfSchedule;

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
	
	public void sort(){
		Collections.sort(ranks);
	}
	
	public void setConferenceAverages(){
		int bottomCurrent = 0;
		int total = 0;
		int length = ranks.size();
		if(length == 0){
			ErrorPage.writeError("Missing Conference ranks for a conference");
			return;
		}
		float totalSOS = 0;
		for(int i = 0; i < length; i++){
			total = total + ranks.get(i);
			totalSOS = totalSOS + strengthOfSchedules.get(i);
			if(i == 2){
				top = total / 3;
			} else if (i > length - 4){
				bottomCurrent = bottomCurrent + ranks.get(i);
			}
		}
		average = total/length;
		bottom = bottomCurrent/3;	
		strength = (float) (average*3.25 + top*2.5 + bottom*1.5);
		strengthOfSchedule = totalSOS/length;
	}

}
