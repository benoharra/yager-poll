package cfranking.parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cfranking.config.ConfigProps;
import cfranking.model.FactorWeights;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import cfranking.config.ErrorPage;
import cfranking.model.TeamResult;
import org.apache.commons.lang3.StringUtils;

public class CurrentWeek {

	private FactorWeights factorWeights;

	public Map<String, TeamResult> readInput() throws IOException {
		File file = new File(ConfigProps.CURRENT_WEEK_FILE);
		try {
			CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.EXCEL);
			List<CSVRecord> records = parser.getRecords();

			// Use the week number in the 1st line to get the factor weights configuration
			loadFactorWeights(records.get(0).get(1));

			// Read all the data into TeamResults starting after the week and header rows
			return records.subList(2, records.size())
					.stream().map(CurrentWeek::buildResult)
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		} catch (IOException e) {
			ErrorPage.writeError("Current week input failed with exception: " + e.toString());
			throw e;
		}
	}

	public FactorWeights getFactorWeights() {
		return  factorWeights;
	}

	private void loadFactorWeights(String week) {
		factorWeights = WeightConfiguration.readFactorWeightsConfiguration(week);
	}

	private static Map.Entry<String, TeamResult> buildResult(CSVRecord record) {
		String result = record.get(1);
		// Return an entry mapped team name -> result
		return new AbstractMap.SimpleEntry<>(StringUtils.upperCase(record.get(0)),
				new TeamResult(
						StringUtils.upperCase(record.get(0)),
						result,
						calculateMargin(result, record.get(2)),
						record.get(3)));
	}

	private static int calculateMargin(String gameResult, String margin) {
		return !gameResult.equalsIgnoreCase("b") ? Integer.valueOf(margin) : 0;
	}

}












