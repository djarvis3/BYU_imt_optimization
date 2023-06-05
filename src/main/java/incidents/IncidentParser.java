package incidents;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.vividsolutions.jts.geom.Coordinate;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;

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
	 * @throws IllegalStateException    if there is an error parsing the CSV file
	 */
	public static List<Incident> parse(String csvFilePath, Network network) throws IllegalArgumentException, IllegalStateException {
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
						String incidentId = record.getString("ID");
						double x = record.getDouble("x");
						double y = record.getDouble("y");
						Coord incidentCoord = new Coord(x, y);

						Link incidentLink = NetworkUtils.getNearestLink(network, incidentCoord);
						int linkId = Integer.parseInt(String.valueOf(incidentLink.getId()));


						int respondingIMTs = record.getInt("IMTs");
						double capReduction = record.getDouble("Cap reduction");
						int startTime = record.getInt("Start");
						int endTime = record.getInt("End");

						return new Incident(linkId, incidentId, respondingIMTs, startTime, endTime, capReduction);
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
