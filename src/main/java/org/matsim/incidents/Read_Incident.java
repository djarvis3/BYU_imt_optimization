package org.matsim.incidents;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkChangeEvent.ChangeType;
import org.matsim.core.network.NetworkChangeEvent.ChangeValue;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;


import java.io.File;
import java.util.Random;

public class Read_Incident {

	Network network;
	Integer incidentNumber;
	Integer incidentClass;
	Integer incidentID;
	CoordinateTransformation ct;

	public Read_Incident(Scenario scenario){
		this.network = scenario.getNetwork();
	}

	public void Incident_Generator(String csv) {

		ct = TransformationFactory.getCoordinateTransformation(TransformationFactory.WGS84, "EPSG:26912");

		Double incidentNumberSelector = Math.random();
		if (incidentNumberSelector <= 0.0451127819548872)
			incidentNumber = 1;
		if (incidentNumberSelector > 0.0451127819548872 & incidentNumberSelector <= 0.0601503759398496)
			incidentNumber = 3;
		if (incidentNumberSelector > 0.0601503759398496 & incidentNumberSelector <= 0.12781954887218)
			incidentNumber = 4;
		if (incidentNumberSelector > 0.12781954887218 & incidentNumberSelector <= 0.180451127819549)
			incidentNumber = 5;
		if (incidentNumberSelector > 0.180451127819549 & incidentNumberSelector <= 0.323308270676692)
			incidentNumber = 6;
		if (incidentNumberSelector > 0.323308270676692 & incidentNumberSelector <= 0.466165413533835)
			incidentNumber = 7;
		if (incidentNumberSelector > 0.466165413533835 & incidentNumberSelector <= 0.593984962406015)
			incidentNumber = 8;
		if (incidentNumberSelector > 0.593984962406015 & incidentNumberSelector <= 0.654135338345865)
			incidentNumber = 9;
		if (incidentNumberSelector > 0.654135338345865 & incidentNumberSelector <= 0.759398496240602)
			incidentNumber = 10;
		if (incidentNumberSelector > 0.759398496240602 & incidentNumberSelector <= 0.849624060150376)
			incidentNumber = 11;
		if (incidentNumberSelector > 0.849624060150376 & incidentNumberSelector <= 0.932330827067669)
			incidentNumber = 12;
		if (incidentNumberSelector > 0.932330827067669 & incidentNumberSelector <= 0.969924812030075)
			incidentNumber = 13;
		if (incidentNumberSelector > 0.969924812030075 & incidentNumberSelector <= 0.984962406015038)
			incidentNumber = 14;
		if (incidentNumberSelector > 0.984962406015038) incidentNumber = 18;

		for (int i=1; i<=incidentNumber; i++) {
			Double incidentClassSelector = Math.random();
			if (incidentClassSelector <= 0.349206349) incidentClass = 1;
			if (incidentClassSelector > 0.349206349 & incidentClassSelector <= 0.534391534) incidentClass = 2;
			if (incidentClassSelector > 0.534391534 & incidentClassSelector <= 0.73015873) incidentClass = 3;
			if (incidentClassSelector > 0.73015873 & incidentClassSelector <= 0.899470899) incidentClass = 4;
			if (incidentClassSelector > 0.899470899) incidentClass = 5;

			//Random Incident Number
			if (incidentClass == 1) {
				incidentID = new Random().nextInt(65) + 1; //[0...65] + 1 = [1...66]
			}
			if (incidentClass == 2) {
				incidentID = new Random().nextInt(34) + 67; //[0...34] + 67 = [67...101]
			}
			if (incidentClass == 3) {
				incidentID = new Random().nextInt(36) + 102; //[0...36] + 102 = [102...138]
			}
			if (incidentClass == 4) {
				incidentID = new Random().nextInt(31) + 139; //[0...31] + 139 = [139...170]
			}
			if (incidentClass == 5) {
				incidentID = new Random().nextInt(18) + 171; //[0...18] + 171 = [171...191]
			}

			for (Link link : network.getLinks().values()) {
				Id<Link> id = link.getId();
				String linkNetwork = id.toString();
				double capacity = link.getCapacity();

				CsvParserSettings parserSettings = new CsvParserSettings();
				parserSettings.getFormat().setLineSeparator("\n");
				parserSettings.setHeaderExtractionEnabled(true);

				CsvParser parser = new CsvParser(parserSettings);
				parser.beginParsing(new File(csv));

				Record record;
				parser.getRecordMetadata();

				while ((record = parser.parseNextRecord()) != null) {
					String MATSimLink = record.getString("MATSim Link");
					Integer incidentCSV = record.getInt("Number");
					Double start = record.getDouble("start");
					Double end = record.getDouble("End");
					Double reduction = record.getDouble("Capacity reduction");
					Double x = record.getDouble("Longitude");
					Double y = record.getDouble("Lattitude");
					Coord incidentCoord = CoordUtils.createCoord(x, y);
					incidentCoord = ct.transform(incidentCoord);
					Double reducedCapacity = (capacity * reduction);

					if (incidentID.equals(incidentCSV) && linkNetwork.equals(MATSimLink)) {
						{
							NetworkChangeEvent event = new NetworkChangeEvent(start); // start: 9:13:30 am
							event.setFlowCapacityChange(new ChangeValue(ChangeType.ABSOLUTE_IN_SI_UNITS, reducedCapacity));
							// reducedCapacity: capacity * 0.2778
							event.addLink(link); // link 24342 (5800 S I15 SB)
							NetworkUtils.addNetworkChangeEvent(network, event);
						}
						{
							NetworkChangeEvent event = new NetworkChangeEvent(end); // end 9:23:30 am
							event.setFlowCapacityChange(new ChangeValue(ChangeType.ABSOLUTE_IN_SI_UNITS, capacity)); // capacity
							event.addLink(link); // link 24342 (5800 S I15 SB)
							NetworkUtils.addNetworkChangeEvent(network, event);
						}
						System.out.println("Total Number of Incidents " + incidentNumber + " ,Incident ID " + incidentID + ",  Link Number " + MATSimLink + ", Incident Class " + incidentClass + ", Capacity Reduction " + reduction);
					}
				}
			}
		}
	}
}

