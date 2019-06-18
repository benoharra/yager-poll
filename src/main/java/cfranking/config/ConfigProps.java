package cfranking.config;

public class ConfigProps {

    public static final String PARSING_DIRECTORY = System.getProperty("parseDir");
    public static final String OUTPUT_DIRECTORY = System.getProperty("outputDir");

    public static final String CURRENT_WEEK_FILE = PARSING_DIRECTORY + "CurrentWeek.csv";
    public static final String PREVIOUS_WEEK_FILE = PARSING_DIRECTORY + "PreviousWeek.csv";

}
