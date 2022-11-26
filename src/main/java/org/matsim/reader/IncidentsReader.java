package org.matsim.reader;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

import java.io.File;

public class IncidentsReader {
	// used to find the nearest links
	Network network;
	// used for CSV parsing
	Node crashFrom;
	Coord fromCoord;
	Node crashTo;
	Coord toCoord;
	Double x;
	Double y;
	Coord crashCoord;
	CoordinateTransformation ct;

	// used for Network parsing
	Id<Link> crashID;
	Link crashLink;


	public IncidentsReader(Scenario scenario) {
		this.network = scenario.getNetwork();
		this.ct = TransformationFactory.getCoordinateTransformation(TransformationFactory.WGS84, "EPSG:26912");
	}

	public void parseCrashCSV(String csv) {
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setHeaderExtractionEnabled(true);

		CsvParser parser = new CsvParser(parserSettings);
		parser.beginParsing(new File(csv));

		Record record;
		parser.getRecordMetadata();

		while ((record = parser.parseNextRecord()) != null) {

			// get linkId from csv file

			x = record.getDouble("Longitude");
			y = record.getDouble("Lattitude");
			crashCoord = CoordUtils.createCoord(x, y);
			crashCoord = ct.transform(crashCoord);
			crashLink = NetworkUtils.getNearestLinkExactly(network, crashCoord);
			crashID = crashLink.getId();
			crashFrom = crashLink.getFromNode();
			fromCoord = crashFrom.getCoord();
			crashTo = crashLink.getToNode();
			toCoord = crashTo.getCoord();

			System.out.println(crashID);
			// System.out.println(fromCoord);
			// System.out.println(toCoord);
			}
		}
	}
