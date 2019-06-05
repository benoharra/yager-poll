package parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import controller.ErrorPage;
import model.TeamResult;

public class CurrentWeek {
	
	private String filePath = ErrorPage.dirHome + "CurrentWeek.csv";

	public List<TeamResult> readInput() throws IOException {
		File file = new File(filePath);
		try {
			CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.EXCEL);
			List<CSVRecord> records = parser.getRecords();
			// Remove header row
			records.remove(0);
			return records.stream().map(CurrentWeek::buildResult).collect(Collectors.toList());
		} catch (IOException e) {
			ErrorPage.writeError("Current week input failed with exception: " + e.toString());
			throw e;
		}
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












