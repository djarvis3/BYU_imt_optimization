package incidents;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A utility class for parsing incident data from a CSV file.
 */
public class IncidentParser {

	/**
	 * Parses incident data from the specified CSV file.
	 *
	 * @param csvFilePath the path to the CSV file containing the incident data
	 * @return a list of {@Incident} objects representing the parsed data
	 * @throws IllegalArgumentException if the CSV file path is invalid
	 * @throws IllegalStateException if there is an error parsing the CSV file
	 */
	public static List<Incident> parse(String csvFilePath) throws IllegalArgumentException, IllegalStateException {
		// Check if the file exists
		File file = new File(csvFilePath);
		if (!file.exists()) {
			throw new IllegalArgumentException("File not found: " + csvFilePath);
		}

		// Configure the CSV parser settings
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setHeaderExtractionEnabled(true);

		CsvParser csvParser = new CsvParser(parserSettings);
		List<Record> records;
		List<Incident> incidents;

		try {
			// Parse the CSV file and map the records to Incident objects
			records = csvParser.parseAllRecords(file);
			incidents = records.stream()
					.map(record -> {
						String linkId = record.getString("MATSim Link");
						int incidentId = record.getInt("ID");
						int respondingIMTs = record.getInt("Responding IMTs");
						double startTimeSec = record.getDouble("Start Time (sec)");
						double endTimeSec = record.getDouble("End Time (sec)");
						double capacityReductionTWA = record.getDouble("Capacity reduction (w/ TWA)");
						return new Incident(linkId, incidentId, respondingIMTs, startTimeSec, endTimeSec, capacityReductionTWA);
					})
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new IllegalStateException("Error parsing CSV file: " + e.getMessage());
		} finally {
			csvParser.stopParsing();
		}

		return incidents;
	}
}
