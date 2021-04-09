package yager

import (
	"fmt"
	"sort"
	"strings"
)

// This file is the equivalent of the Java controller.ConferenceMapper + some controller.AdjustStats top level conf code

const pac12 = "pac-12"
const american = "american"
const acc = "acc"
const big12 = "big-12"
const big10 = "big-10"
const usa = "conference usa"
const midAmerican = "mac"
const mountainWest = "mountain west"
const sec = "sec"
const sunBelt = "sun belt"
const independent = "independent"

var ConferenceMap = map[string]*Conference{
	pac12:        {},
	american:     {},
	acc:          {},
	big12:        {},
	big10:        {},
	usa:          {},
	midAmerican:  {},
	mountainWest: {},
	sec:          {},
	sunBelt:      {},
}

const conferenceRankMultiplier = 13

type confWithName struct {
	conference Conference
	name       string
}

// See java original at AdjustStats.setConference strength + ConferenceMapper logic
func SetConferenceStrengthRanks(teams Teams) []error {
	// Put each team's stats into the conference objects
	errs := addTeamsToConferences(teams)
	if len(errs) > 0 {
		return errs
	}

	// Calculate averages for each conference
	confList := make([]confWithName, 0, len(ConferenceMap))
	for key, conf := range ConferenceMap {
		conf.setConferenceAverage()
		ConferenceMap[key] = conf
		confList = append(confList, confWithName{
			name: key,
			conference: *conf,
		})
	}

	// Sort conferences by strength of their team's rankings, assign strength
	sort.SliceStable(confList, func(i, j int) bool {
		return confList[i].conference.strength < confList[j].conference.strength
	})

	for i := 1; i <= len(confList); i++ {
		conf := confList[i-1].conference
		conf.conferenceRank = i
		ConferenceMap[confList[i-1].name] = &conf
	}

	confStrengthSetList := make([]confWithName, 0, len(ConferenceMap))
	for key, conf := range ConferenceMap {
		confStrengthSetList = append(confStrengthSetList, confWithName{
			name: key,
			conference: *conf,
		})
	}

	// Sort conferences by strength of schedules, assign sosRanks. These are only used for calculating independents
	sort.SliceStable(confStrengthSetList, func(i, j int) bool {
		return confStrengthSetList[i].conference.weightedStrengthOfSchedule < confStrengthSetList[j].conference.weightedStrengthOfSchedule
	})

	for i := 1; i <= len(confList); i++ {
		confName := confList[i-1].name
		ConferenceMap[confName].strengthOfScheduleRank = i
	}

	// Assign each team its conferenceRank value, handle independents
	for i := 0; i < len(teams); i++ {
		team := &teams[i]
		if team.conference == independent {
			setIndependentTeamStrengthOfSchedule(team, confStrengthSetList)
		} else {
			team.setConferenceStrength(ConferenceMap[team.conference].conferenceRank)
		}
	}

	return nil
}

func (team *Team) setConferenceStrength(confRank int) {
	if confRank == 1 {
		team.conferenceStrength = 1
	} else {
		team.conferenceStrength = (confRank - 1) * conferenceRankMultiplier
	}
}

func setIndependentTeamStrengthOfSchedule(team *Team, sortedBySosConferences []confWithName) {
	for i := 0; i < len(sortedBySosConferences); i++ {
		if team.strengthOfScheduleRaw < sortedBySosConferences[i].conference.weightedStrengthOfSchedule {
			if i == 0 {
				// Team has a higher raw Strength of Schedule than even the best conference, rank them 1
				team.conferenceStrength = 1
			} else {
				team.setConferenceStrength(
					pickClosestSosRanking(
						team.strengthOfScheduleRaw,
						sortedBySosConferences[i-1].conference,
						sortedBySosConferences[i].conference))
			}
			return
		}
	}
	// If the teams Strength of Schedule was lower than all conferences (yikes) assign them the bottom
	team.conferenceStrength = sortedBySosConferences[len(sortedBySosConferences) - 1].conference.conferenceRank * conferenceRankMultiplier
}
func pickClosestSosRanking(teamRawSos float64, upperBoundConf Conference, lowerBoundConf Conference) int {
	upperDiff := teamRawSos - upperBoundConf.weightedStrengthOfSchedule
	lowerDiff := lowerBoundConf.weightedStrengthOfSchedule - teamRawSos
	if upperDiff < lowerDiff  {
		return upperBoundConf.conferenceRank
	}
	return lowerBoundConf.conferenceRank
}

func addTeamsToConferences(teams Teams) []error {
	errors := make([]error, 0)
	for _, team := range teams {
		teamConference := strings.ToLower(team.conference)
		if teamConference == independent {
			continue
		}
		conference, ok := ConferenceMap[teamConference]
		if !ok {
			errors = append(errors, fmt.Errorf("team %s has invalid conference %s", team.name, team.conference))
		} else {
			conference.strengthOfSchedules = append(conference.strengthOfSchedules, team.strengthOfScheduleRaw)
			conference.ranks = append(conference.ranks, team.previousRank)
			ConferenceMap[teamConference] = conference
		}
	}
	return errors
}

func (conference *Conference) setConferenceAverage() {
	ranks := conference.ranks

	sort.SliceStable(ranks, func(i, j int) bool {
		return ranks[i] < ranks[j]
	})

	top := 0.0
	total := 0
	bottomCurrent := 0
	totalSos := 0.0
	teamCount := len(ranks)
	for i := 0; i < teamCount; i++ {
		total += ranks[i]
		totalSos += conference.strengthOfSchedules[i]
		if i == 2 {
			top = float64(total) / 3.0
		} else if i > teamCount - 4 {
			bottomCurrent += ranks[i]
		}
	}

	average := float64(total) / float64(teamCount)
	bottom := float64(bottomCurrent) / 3.0
	conference.strength = average * 3.25 + top * 2.5 + bottom * 1.5
	conference.weightedStrengthOfSchedule = totalSos / float64(teamCount)
}
