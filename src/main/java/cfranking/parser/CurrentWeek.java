package cfranking.parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import cfranking.model.FactorWeights;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import cfranking.controller.ErrorPage;
import cfranking.model.TeamResult;

public class CurrentWeek {
	
	private String filePath = ErrorPage.dirHome + "CurrentWeek.csv";

	private FactorWeights factorWeights;

	public List<TeamResult> readInput() throws IOException {
		File file = new File(filePath);
		try {
			CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.EXCEL);
			List<CSVRecord> records = parser.getRecords();

			// Use the week number in the 1st line to get the factor weights configuration
			loadFactorWeights(records.get(0).get(1));

			// Read all the data into TeamResults starting after the week and header rows
			return records.subList(2, records.size()).stream().map(CurrentWeek::buildResult).collect(Collectors.toList());
		} catch (IOException e) {
			ErrorPage.writeError("Current week input failed with cfranking.exception: " + e.toString());
			throw e;
		}
	}

	public FactorWeights getFactorWeights() {
		return  factorWeights;
	}

	private void loadFactorWeights(String week) {
		factorWeights = WeightConfiguration.readFactorWeightsConfiguration(week);
	}

	private static TeamResult buildResult(CSVRecord record) {
		String result = record.get(1);
		return new TeamResult(
				record.get(0),
				result,
				calculateMargin(result, record.get(2)),
				record.get(3)
		);
	}

	private static int calculateMargin(String gameResult, String margin) {
		return !gameResult.equals("b") ? Integer.valueOf(margin) : 0;
	}

}












