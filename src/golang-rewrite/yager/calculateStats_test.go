package yager

import (
	. "github.com/onsi/ginkgo"
	. "github.com/onsi/gomega"
)

var _ = Describe("Test Calculation Methods", func() {
	Context("Happy Path Win Percentage ranking", func() {
		It("should assign ranks correctly", func() {
			testTeams := CreateRawTestTeams()
			SetWinPercentageRankings(testTeams)
			Expect(testTeams[0].winPercentageRank).To(BeEquivalentTo(1))
			Expect(testTeams[0].name).To(BeEquivalentTo("Team5"))

			Expect(testTeams[1].winPercentageRank).To(BeEquivalentTo(2))
			Expect(testTeams[1].name).To(BeEquivalentTo("Team4"))

			Expect(testTeams[2].winPercentageRank).To(BeEquivalentTo(3))
			Expect(testTeams[2].name).To(BeEquivalentTo("Team3"))

			Expect(testTeams[3].winPercentageRank).To(BeEquivalentTo(4))
			Expect(testTeams[3].name).To(BeEquivalentTo("Team2"))

			Expect(testTeams[4].winPercentageRank).To(BeEquivalentTo(5))
			Expect(testTeams[4].name).To(BeEquivalentTo("Team1"))
		})
	})
	Context("Happy Path Strength of Schedule ranking", func() {
		It("should assign ranks correctly", func() {
			testTeams := CreateRawTestTeams()
			SetStrengthOfSchedule(testTeams)
			teamMap := getTeamsMap(testTeams)
			Expect(teamMap["Team1"].strengthOfScheduleRaw).To(BeEquivalentTo(3.5))
			Expect(teamMap["Team1"].strengthOfScheduleRank).To(BeEquivalentTo(5))
			Expect(teamMap["Team2"].strengthOfScheduleRank).To(BeEquivalentTo(4))
			Expect(teamMap["Team3"].strengthOfScheduleRank).To(BeEquivalentTo(3))
			Expect(teamMap["Team4"].strengthOfScheduleRank).To(BeEquivalentTo(2))
			Expect(teamMap["Team5"].strengthOfScheduleRank).To(BeEquivalentTo(1))
		})
	})
	Context("Strength of Schedule with FCS team", func() {
		It("should evaluate FCS ranks with the substring ranking", func() {
			testTeams := CreateRawTestTeams()
			team5GamesPlayed := append(testTeams[4].teamsPlayed, "FCS40")
			testTeams[4].teamsPlayed = team5GamesPlayed
			testTeams[4].gamesPlayed = 5
			SetStrengthOfSchedule(testTeams)
			team5 := getTeamsMap(testTeams)["Team5"]
			Expect(team5.strengthOfScheduleRaw).To(BeEquivalentTo(10.0))
			Expect(team5.strengthOfScheduleRank).To(BeEquivalentTo(5))
		})
	})
	Context("Margin Rank", func() {
		It("should evaluate marginRank correctly", func() {
			testTeams := CreateRawTestTeams()
			SetMarginRank(testTeams)
			teamsMap := getTeamsMap(testTeams)
			Expect(teamsMap["Team1"].marginRank).To(BeEquivalentTo(1))
			Expect(teamsMap["Team2"].marginRank).To(BeEquivalentTo(3))
			Expect(teamsMap["Team3"].marginRank).To(BeEquivalentTo(2))
			Expect(teamsMap["Team4"].marginRank).To(BeEquivalentTo(4))
			Expect(teamsMap["Team5"].marginRank).To(BeEquivalentTo(5))
		})
		It("should give teams with the same raw margin the same rank", func() {
			testTeams := CreateRawTestTeams()
			// Make team 1 have the same margin rank as team 5
			testTeams[0].totalMargin = testTeams[4].totalMargin
			SetMarginRank(testTeams)
			teamsMap := getTeamsMap(testTeams)
			Expect(teamsMap["Team1"].marginRank).To(BeEquivalentTo(4))
			Expect(teamsMap["Team2"].marginRank).To(BeEquivalentTo(2))
			Expect(teamsMap["Team3"].marginRank).To(BeEquivalentTo(1))
			Expect(teamsMap["Team4"].marginRank).To(BeEquivalentTo(3))
			Expect(teamsMap["Team5"].marginRank).To(BeEquivalentTo(4))
		})
	})

})

func getTeamsMap(teams Teams) map[string]Team {
	teamMap := make(map[string]Team, len(teams))
	for _, team := range teams {
		teamMap[team.name] = team
	}
	return teamMap
}

func CreateRawTestTeams() Teams {
	return []Team{
		{
			name: "Team1",
			previousRank: 1,
			wins: 0,
			losses: 4,
			gamesPlayed: 4,
			totalMargin: 10,
			teamsPlayed: []string{"Team2", "Team3", "Team4", "Team5"},
		},
		{
			name: "Team2",
			previousRank: 2,
			wins: 1,
			losses: 3,
			gamesPlayed: 4,
			totalMargin: -4,
			teamsPlayed: []string{"Team1", "Team3", "Team4", "Team5"},
		},
		{
			name:         "Team3",
			previousRank: 3,
			wins:         2,
			losses:       2,
			gamesPlayed:  4,
			totalMargin:  4,
			teamsPlayed:  []string{"Team2", "Team1", "Team4", "Team5"},
				},
		{
			name: "Team4",
			previousRank: 4,
			wins: 3,
			losses: 1,
			gamesPlayed: 4,
			totalMargin: -12,
			teamsPlayed: []string{"Team2", "Team3", "Team1", "Team5"},
		},
		{
			name: "Team5",
			previousRank: 5,
			wins: 4,
			losses: 0,
			gamesPlayed: 4,
			totalMargin: -20,
			teamsPlayed: []string{"Team2", "Team3", "Team4", "Team1"},
		},
	}
}
