# The College Football Yager Poll
This app follows the algorithm designed by Connor Yager to statistically rank all 130 NCAA Football teams. Input and output files are held in .csv format for easy user viewing through Excel. The algorithm is built to store rankings and data after each week, allowing a user to simply input weekly results to keep rankings up to date. The algorithm is calibrated to weight factors differently as the season progresses, giving more weight to factors like win percentage and strength of schedule later in the season.

Teams are analyzed based on the following factors:
- Win percentage
- Margin of Victory
- Conference Strength
- Strength of Schedule
- Previous week rank
- Ranking author's opinion

## Running the App
- Ensure Java is installed on your computer
- Download the YagerPoll.jar wherever you would like to keep it
- Create a Rankings folder under your user home directory 
    - Windows: C:\Users\YourUsername\Rankings   
    - Mac: /Users/YourUsername/Rankings
- Input Files: .../Rankings/InputFiles/
    - PreviuosWeek.csv
    
      Format: This file is originally built from an author's own preseason ranking factors, then the data with all teams listed in table format as below. For week 1 all numerical factors besides Rank and opinion should be 0.
      
      | Rank | Team | Wins | Losses | Conference | Total Margin | Teams Played |Opinion | Win Percentage | etc... |
      | ---- | ---- | ---- | ------ | ---------- | ------------ | ------------ | ------ | -------------- | ------ |
      |   1|Utah|   4|     1|    Pac-12|         151|Byu;Navy;etc|      5|           .80|      |
      |   2|Iowa|   3|     2|    Big-10|         122|Iowa St;Troy|      4|           .60|      |
    
      - Opinion column should be used knowledgably if the author suspects a team may be under or overperforming based on their rank. Opinion ranks should go from 1-130 just like the regular rankings
    
      - Check the sample files folder for exact conference names and full column list. The FullData.csv file becomes the PreviousWeek.csv input for next week, just rename and move the file into InputFiles when ready for the next week's rankings.
    
    - CurrentWeek.csv
    
      Format: This file should be rebuilt each week with the most recent results
      
      |  Week       |  1       |          |            |
      | ---------   |  ------  |  ------  |  --------  |
      |__Team Name__|__Result__|__Margin__|__Opponent__|
      |Clemson  |  W   | 32   |Marshall|
      |Miami    |  L   | -2   |FCS65   |
      |NC State |  B   |      |        |
       
      - Update the Week cell for each week of results, this affects factor weights
      - Team results can be listed in any order, team name and opponent must match name from PreviousWeek.csv
      - W denotes win, L for loss, B for bye
      - Margin value should be negative for a loss
      - Any FCS opponents are listed with the author's estimate ranking for the opponent as FCS<estimate rank>. See ex above
      
- Output Files: .../Rankings/OutputFiles/
    - FullData.csv
      Format: Same as PreviousWeek.csv, move this file and rename to PreviousWeek.csv when done with the current week
    - Ranks.txt
      Format: List of Rank to Team for easy viewing. No other data included in this file
    - Errors.txt
      Format: Errors in list format
      This file should be blank when the rankings have been executed without any errors
      
 - When input files are complete, run the YagerPoll.jar with java and check your OutputFiles directory for results. There is no UI while the progam is running, but it should take only a couple seconds to complete.

## Solving Errors
__NEVER CHANGE THE PREVIOUS WEEK FILE DURING THE SEASON TO SOLVE ERRORS__

After a week of successful rankings, the PreviousWeek.csv file should be a perfect copy of team names and current ranking info. Historical data such as teams played, total margin, wins/losses are maintained in this file so try to only edit CurrentWeek.csv to solve errors in input data. CurrentWeek.csv inputs should be updated to match PreviousWeek.csv values when solving errors
- Program can be rerun as many times as needed when solving errors, all OutputFiles will be regenerated fresh from the InputFiles on each run
- Ensure the PreviousWeek and CurrentWeek files aren't open when running, as they may cause permission issues reading the data on some machines
- When troubleshooting check the rightmost data columns in the FullData output file to ensure they make sense from the PreviousWeek input

## Configuration and Overrides
__Configuration__

Input and output directories are specified in the config.properties file within the project resources folder. You may edit these and rebuild the jar if a different directory structure is desired.

Ranking factors are calibrated to each week of rankings. Ensure the "Week" cell entry in the CurrentWeek file is up to date each week to get the proper values. Factor weightings for each week are kept in the weights.properties file within the project resources folder.

__Overriding Factor Weights__

The ranking system has been designed to override the weekly factor weights if the author desires. This will allow a user to test new weekly weights for algorithm adjustments and/or run custom rankings for pre or bowl season insights.

Overrides will be listed as key/value pairs separated by "=" in an overrides.txt file within the InputFiles directory under the Rankings folder. Users can override as many weights as they would like, keys (not case-sensitive) for adjusting each are listed below with a key/value sample:
  - Record: override the weight on teams' winning percentage
  - Sos: override the weight on teams' strength of schedule
  - Margin: override the weight on teams' margin of victory
  - Conference: override the weight on conference strength
  - PreviousRank: override the weight on teams' previous week ranking
  - Opinion: override the weight on the author's ranking opinion for each team
  
Sample overrides.txt:

record=2.1

margin=5.3

previousrank=0

## Build Instructions
The app builds through gradle wrapper, simple instructions are below. For more information check the build.gradle.kts file.
- Clone the master branch
- Run ./gradlew clean build from the project directory
- If successful, the YagerPoll.jar will be in build/libs
