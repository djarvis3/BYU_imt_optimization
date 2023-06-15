package incidents;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.utils.objectattributes.attributable.Attributes;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IncidentParser {

	private final static Logger log = LogManager.getLogger(IncidentParser.class);

	private final Map<String, Link> motorwayLinkMap;

	public IncidentParser(Network network) {

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

		if (nearestLink == null) {
			log.warn("[nearestMotorwayLink not found. Maybe run NetworkCleaner?]");
		}

		return  nearestLink;
	}
}
