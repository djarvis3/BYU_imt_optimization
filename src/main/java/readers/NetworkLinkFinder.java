package readers;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.utils.objectattributes.attributable.Attributes;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NetworkLinkFinder {

	private final static Logger log = LogManager.getLogger(NetworkLinkFinder.class);

	private final Network network;

	public NetworkLinkFinder(String networkXMLPath) {
		this.network = readNetwork(networkXMLPath);
	}

	public void findAndPrintNearestLinksForTrucks(String csvFilePath) {
		List<Record> records = parseCSV(csvFilePath);

		// For each record, find the nearest link and print
		records.forEach(record -> {
			String truckId = record.getString("ID");
			double x = record.getDouble("x");
			double y = record.getDouble("y");
			Coord truckCoord = new Coord(x, y);

			// Convert the truck coordinate
			CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(TransformationFactory.WGS84, "EPSG:32612");
			Coord transformedCoord = ct.transform(truckCoord);

			Link nearestLink = getNearestMotorwayLink(transformedCoord);
			if (nearestLink != null) {
				System.out.println("Truck ID: " + truckId + " | Nearest Motorway Link ID: " + nearestLink.getId());
			} else {
				System.out.println("Truck ID: " + truckId + " | Nearest Motorway Link not found.");
			}
		});
	}

	private Network readNetwork(String networkXMLPath) {
		Network network = org.matsim.core.network.NetworkUtils.createNetwork();
		new MatsimNetworkReader(network).readFile(networkXMLPath);
		return network;
	}

	private List<Record> parseCSV(String csvFilePath) {
		File file = new File(csvFilePath);
		if (!file.exists()) {
			throw new IllegalArgumentException("File not found: " + csvFilePath);
		}

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setHeaderExtractionEnabled(true);

		CsvParser csvParser = new CsvParser(parserSettings);
		List<Record> records;

		try {
			records = csvParser.parseAllRecords(file);
		} catch (Exception e) {
			throw new IllegalStateException("Error parsing CSV file: " + e.getMessage());
		} finally {
			csvParser.stopParsing();
		}

		return records;
	}

	public Link getNearestMotorwayLink(Coord coord) {
		Link nearestLink = null;
		double shortestDistance = Double.MAX_VALUE;

		for (Link link : network.getLinks().values()) {
			Attributes attributes = link.getAttributes();
			String linkType = (String) attributes.getAttribute("type");
			if ("motorway".equals(linkType)) {
				double dist = CoordUtils.distancePointLinesegment(link.getFromNode().getCoord(), link.getToNode().getCoord(), coord);
				if (dist < shortestDistance) {
					shortestDistance = dist;
					nearestLink = link;
				}
			}
		}

		if (nearestLink == null) {
			log.warn("Nearest motorway link not found. Maybe run NetworkCleaner?");
		}

		return nearestLink;
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage: NetworkLinkFinder <path_to_network_XML> <path_to_truck_CSV>");
			return;
		}

		String networkXMLPath = args[0];
		String csvFilePath = args[1];

		NetworkLinkFinder finder = new NetworkLinkFinder(networkXMLPath);
		finder.findAndPrintNearestLinksForTrucks(csvFilePath);
	}
}
