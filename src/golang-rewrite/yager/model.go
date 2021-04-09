package yager

// Equivalent to the whole "model" package in Java implementation

type Conference struct {
	ranks []int
	strength float64
	weightedStrengthOfSchedule float64
	strengthOfSchedules []float64
	conferenceRank int
	strengthOfScheduleRank int
}

type Team struct {
	name string
	rank int
	wins int
	losses int
	winPercentage float64
	gamesPlayed int
	teamsPlayed []string
	winPercentageRank int
	strengthOfScheduleRaw float64
	strengthOfScheduleRank int
	totalMargin int
	marginRank int
	conference string
	conferenceStrength int
	previousRank int
	opinion int
	weightedScore float64
}

type Weights struct {
	record float64
	strengthOfSchedule float64
	margin float64
	opinion float64
	conferenceStrength float64
	previousWeekRank float64
}

type TeamResult struct {
	name string
	opponent string
	margin int
	result string
}


