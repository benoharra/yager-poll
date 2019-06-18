package cfranking.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigProps {

    private static String PARSING_DIRECTORY;
    private static String OUTPUT_DIRECTORY;

    static {
        try (InputStream input = ConfigProps.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties props = new Properties();

            props.load(input);

            PARSING_DIRECTORY = System.getProperty("user.home") + props.getProperty("parseDir");
            OUTPUT_DIRECTORY = System.getProperty("user.home") + props.getProperty("outputDir");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Input Files
    public static final String CURRENT_WEEK_FILE = PARSING_DIRECTORY + "CurrentWeek.csv";
    public static final String PREVIOUS_WEEK_FILE = PARSING_DIRECTORY + "PreviousWeek.csv";
    public static final String OVERRIDE_FILE = PARSING_DIRECTORY + "overrides.txt";

    // Output Files
    public static final String RANKING_FILE = OUTPUT_DIRECTORY + "Ranks.txt";
    public static final String FULL_DATA_FILE = OUTPUT_DIRECTORY + "Full Data.csv";
    public static final String ERROR_FILE = OUTPUT_DIRECTORY + "Errors.txt";

}
