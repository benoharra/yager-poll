package cfranking.parser;

import cfranking.config.ErrorPage;
import cfranking.model.FactorWeights;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class WeightConfiguration {

    private static final String RECORD = "record.";
    private static final String STRENGTH_OF_SCHEDULE = "sos.";
    private static final String MARGIN_OF_VICTORY = "margin.";
    private static final String OPINION = "opinion.";
    private static final String CONFERENCE_STRENGTH = "conference.";
    private static final String PREVIOUS_WEEK_RANKING = "previous.";

    private static final String DEFAULT_WEEK = "default";

    public static FactorWeights readFactorWeightsConfiguration(String week) {
        try (InputStream input = WeightConfiguration.class.getClassLoader().getResourceAsStream("weights.properties")) {
            Properties props = new Properties();

            if(input == null) {
                ErrorPage.writeError("Unable to read configuration");
                return new FactorWeights();
            }

            props.load(input);
            return buildFactorWeights(week, props);
        } catch (IOException e) {
            ErrorPage.writeError("Exception thrown loading config:" + e.getMessage());
        }
        return new FactorWeights();
    }

    private static FactorWeights buildFactorWeights(String week, Properties props) {
        FactorWeights factorWeights = new FactorWeights();
        factorWeights.setRecord(getConfigValue(RECORD, week, props));
        factorWeights.setStrengthOfSchedule(getConfigValue(STRENGTH_OF_SCHEDULE, week, props));
        factorWeights.setMargin(getConfigValue(MARGIN_OF_VICTORY, week, props));
        factorWeights.setOpinion(getConfigValue(OPINION, week, props));
        factorWeights.setConferenceStrength(getConfigValue(CONFERENCE_STRENGTH, week, props));
        factorWeights.setPreviousWeekRank(getConfigValue(PREVIOUS_WEEK_RANKING, week, props));
        return factorWeights;
    }

    private static float getConfigValue(String prefix, String week, Properties props) {
        return Float.parseFloat(Optional.ofNullable(props.getProperty(prefix + week))
                .orElse(props.getProperty(prefix + DEFAULT_WEEK)));
    }


}
