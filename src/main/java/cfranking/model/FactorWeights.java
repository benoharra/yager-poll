package cfranking.model;

public class FactorWeights {

    private float record;
    private float strengthOfSchedule;
    private float margin;
    private float opinion;
    private float conferenceStrength;
    private float previousWeekRank;

    public float getRecord() {
        return record;
    }

    public void setRecord(float record) {
        this.record = record;
    }

    public float getStrengthOfSchedule() {
        return strengthOfSchedule;
    }

    public void setStrengthOfSchedule(float strengthOfSchedule) {
        this.strengthOfSchedule = strengthOfSchedule;
    }

    public float getMargin() {
        return margin;
    }

    public void setMargin(float margin) {
        this.margin = margin;
    }

    public float getOpinion() {
        return opinion;
    }

    public void setOpinion(float opinion) {
        this.opinion = opinion;
    }

    public float getConferenceStrength() {
        return conferenceStrength;
    }

    public void setConferenceStrength(float conferenceStrength) {
        this.conferenceStrength = conferenceStrength;
    }

    public float getPreviousWeekRank() {
        return previousWeekRank;
    }

    public void setPreviousWeekRank(float previousWeekRank) {
        this.previousWeekRank = previousWeekRank;
    }
}
