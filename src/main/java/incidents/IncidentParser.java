package incidents;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.utils.objectattributes.attributable.Attributes;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A utility class for parsing incident data from a CSV file.
 */
public class IncidentParser {

	private static final Logger log = Logger.getLogger(IncidentParser.class);
	private final Map<String, Link> motorwayLinkMap;

	public IncidentParser(Network network) {

		// maps incidents with type == "motorway" to ensure that incidents only occur on freeway links
		motorwayLinkMap = new HashMap<>();
		for (Link link : network.getLinks().values()) {
			Attributes attributes = link.getAttributes();
			String linkType = (String) attributes.getAttribute("type");
			if ("motorway".equals(linkType)) {
				String linkId = String.valueOf(link.getId());
				motorwayLinkMap.put(linkId, link);
			}
		}
	}

	/**
	 * Parses incident data from the specified CSV file.
	 *
	 * @param csvFilePath the path to the CSV file containing the incident data
	 * @return a list of {@Incident} objects representing the parsed data
	 * @throws IllegalArgumentException if the CSV file path is invalid
	 * @throws IllegalStateException    if there is an error parsing the CSV file
	 */
	public List<Incident> parse(String csvFilePath) throws IllegalArgumentException, IllegalStateException {

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
						Link nearestMotorwayLink = getNearestMotorwayLink(incidentCoord);
						String linkId = String.valueOf(nearestMotorwayLink.getId());

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

	/**
	 * Returns the nearest motorway link to the specified coordinate.
	 *
	 * @param coord the coordinate for which to find the nearest motorway link
	 * @return the nearest motorway {@link Link} to the specified coordinate
	 */
	public Link getNearestMotorwayLink(Coord coord){
		Link nearestLink = null;
		double shortDistance = Double.MAX_VALUE;

		for (Link link : motorwayLinkMap.values()){
			double dist = CoordUtils.distancePointLinesegment(link.getFromNode().getCoord(), link.getToNode().getCoord(), coord);
			if (dist < shortDistance) {
				shortDistance = dist;
				nearestLink = link;
			}
		}

		if (nearestLink == null){
			log.warn("[nearestMotorwayLink not found. Maybe run NetworkCleaner?]");
		}

		return  nearestLink;
	}
}
