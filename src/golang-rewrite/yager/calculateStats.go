package yager

import (
	"fmt"
	"sort"
	"strconv"
	"strings"
)

// This file is the equivalent of the Java controller.AdjustStats class
type Teams []Team

// See Java original at AdjustStats.setWinPercentage
func SetWinPercentageRankings(teams Teams) {
	// Set win percentages
	for i, team := range teams {
		if team.gamesPlayed > 0 {
			teams[i].winPercentage = float64(team.wins) / float64(team.gamesPlayed)
		} else {
			teams[i].winPercentage = 0
		}
	}

	teams.sortAndRank(
		true,
		func(team Team) float64 {
			return team.winPercentage
		},
		func(team *Team, rank int) {
			team.winPercentageRank = rank
		})
}

// See Java original at AdjustStats.setStrengthOfSchedule
func SetStrengthOfSchedule(teams Teams) {
	previousRankings := getMappedRankings(teams)

	for i := 0; i < len(teams); i++ {
		teams[i].setRawStrengthOfSchedule(previousRankings)
	}

	teams.sortAndRank(
		false,
		func(team Team) float64 {
			return team.strengthOfScheduleRaw
		},
		func(team *Team, rank int) {
			team.strengthOfScheduleRank = rank
		})
}

// See Java original at AdjustStats.setMarginRank
func SetMarginRank(teams Teams) {
	teams.sortAndRank(
		true,
		func(team Team) float64 {
			return float64(team.totalMargin) / float64(team.gamesPlayed)
		},
		func(team *Team, rank int) {
			team.marginRank = rank
		})
}

func getMappedRankings(teams Teams) map[string]int {
	rankings := make(map[string]int)
	for _, team := range teams {
		rankings[team.name] = team.previousRank
	}
	return rankings
}

func (team *Team) setRawStrengthOfSchedule(teamRanks map[string]int) error {
	total := 0
	for _, opponent := range team.teamsPlayed {
		if strings.Contains(opponent, "FCS") {
			fcsRank, err := strconv.Atoi(opponent[3:])
			if err != nil {
				return fmt.Errorf("fcs opponent %s not in correct format", opponent)
			}
			total += fcsRank
		} else {
			oppRank, ok := teamRanks[opponent]
			if !ok {
				return fmt.Errorf("opponent %s not found in team rankings map", opponent)
			}
			total += oppRank
		}
	}
	team.strengthOfScheduleRaw = float64(total) / float64(team.gamesPlayed)
	return nil
}

func (teams Teams) sortAndRank(
	sortBiggestFirst bool,
	rawFactor func (team Team) float64,
	setRanking func (team *Team, rank int)) {
	// Sort the teams based on the factor being ranked
	sort.SliceStable(teams, func(i, j int) bool {
		if sortBiggestFirst {
			return rawFactor(teams[i]) > rawFactor(teams[j])
		} else {
			return rawFactor(teams[i]) < rawFactor(teams[j])
		}
	})

	// Loop through the sorted teams assigning the correct ranking function
	previousRank := 1
	var previousRawValue float64
	for i := 1; i <= len(teams); i++ {
		team := teams[i-1]
		if rawFactor(team) == previousRawValue {
			setRanking(&teams[i-1], previousRank)
		} else {
			setRanking(&teams[i-1], i)
			previousRank = i
			previousRawValue = rawFactor(team)
		}
	}
}
