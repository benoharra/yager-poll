package yager

import (
	"encoding/csv"
	"log"
	"os"
	"strconv"
	"strings"
)

// This file is the equivalent of the Java parser package

const PREVIOUS_WEEK_FILE = "PreviousWeek.csv"
const LATEST_RESULTS_FILE = "CurrentWeek.csv"


func GetPreviousWeekTeams() (Teams, []error) {
	reader := openCsv(PREVIOUS_WEEK_FILE)
	teams := make(Teams, 0, 150)
	errs := make([]error, 0)
	for {
		record, err := reader.Read()
		if err != nil {
			errs = append(errs, err)
			return nil, errs
		}
		newTeam, err := teamFromRecord(record)
		if err != nil {
			errs = append(errs, err)
			continue
		}
		teams = append(teams, newTeam)
	}
	return teams, errs
}

func ReadLatestResultsAndDecorateTeams(Teams) []error {
	return make([]error, 0)
}

func teamFromRecord(record []string) (Team, error) {
	var err error
	var previousRank, wins, losses, totalMargin, opinion int
	var recordValue, name, conference string
	var teamsPlayed []string
	for i := 0; i < 8 && err == nil; i++ {
		recordValue = record[i]
		switch i {
		case 0:
			previousRank, err = strconv.Atoi(recordValue)
		case 1:
			name = recordValue
		case 2:
			wins, err = strconv.Atoi(recordValue)
		case 3:
			losses, err = strconv.Atoi(recordValue)
		case 4:
			conference = recordValue
		case 5:
			totalMargin, err = strconv.Atoi(recordValue)
		case 6:
			teamsPlayed = strings.Split(recordValue, ";")
		case 7:
			opinion, err = strconv.Atoi(recordValue)
		}
	}
	if err != nil {
		return Team{}, err
	}

	return Team{
		previousRank: previousRank,
		name: name,
		wins: wins,
		losses: losses,
		gamesPlayed: wins + losses,
		conference: conference,
		totalMargin: totalMargin,
		teamsPlayed: teamsPlayed,
		opinion: opinion,
	}, nil

}

func openCsv(filename string) *csv.Reader {
	csvfile, err := os.Open(filename)
	if err != nil {
		log.Fatalln("Couldn't open the csv file", err)
	}
	return csv.NewReader(csvfile)
}
