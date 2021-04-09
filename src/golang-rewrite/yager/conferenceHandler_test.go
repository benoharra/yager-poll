package yager

import (
	"fmt"
	. "github.com/onsi/ginkgo"
	. "github.com/onsi/gomega"
	"sort"
)

var _ = Describe("Test Conference ranking", func() {
	Context("Happy Path", func() {
		It("Should handle all conferences with no independents", func() {
			teams := getTeamsList()
			SetConferenceStrengthRanks(teams)
			confWithTeams := mapTeamsToConferences(teams)
			// Spot check teams in 3 conferences
			for _, team := range confWithTeams["acc"] {
				Expect(team.conferenceStrength).Should(Equal(1))
			}

			// Pac-12, 8th conference in alphabetical order
			for _, team := range confWithTeams["pac-12"] {
				Expect(team.conferenceStrength).Should(Equal(7*13))
			}
		})
		It("Should handle Independent teams", func() {
			teams := getTeamsList()
			teams = append(teams, Team{
					name: "ind-0",
					conference: "independent",
					strengthOfScheduleRaw: 35.1,
			})
			SetConferenceStrengthRanks(teams)
			for _, team := range teams {
				if team.name == "ind-0" {
					Expect(team.conferenceStrength).Should(Equal(5*13))
				}
			}
		})
	})
})

func mapTeamsToConferences(teams Teams) map[string]Teams {
	teamConferences := make(map[string]Teams, len(ConferenceMap))

	for name := range ConferenceMap {
		teamConferences[name] = make(Teams, 0, 10)
	}

	for i := 0; i < len(teams); i++ {
		team := teams[i]
		teamConferences[team.conference] = append(teamConferences[team.conference], team)
	}
	return teamConferences
}

func getTeamsList() Teams {
	conferenceNames := make([]string, 0, len(ConferenceMap))
	for name, _ := range ConferenceMap {
		conferenceNames = append(conferenceNames, name)
	}
	sort.Strings(conferenceNames)
	teams := make([]Team, 0, len(ConferenceMap) * 10)
	for i := 0; i < len(conferenceNames); i++ {
		teams = append(teams, makeMockConferenceTeams(conferenceNames[i], i + 1)...)
	}
	return teams
}

func makeMockConferenceTeams(confName string, rankMultiplier int) Teams {
	teams := make([]Team, 0, 10)
	for i := 0; i < 10; i++ {
		teams = append(teams, Team{
			name: fmt.Sprintf("%s-%d", confName, i),
			conference: confName,
			strengthOfScheduleRaw: float64(i + 1) * float64(rankMultiplier),
			previousRank: (i + 1) * rankMultiplier,
		})
	}
	return teams
}