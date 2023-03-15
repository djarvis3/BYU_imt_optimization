package byu.incidents;

import byu.IMT.utahIMT.UtahImtRequest;
import byu.IMT.utahIMT.UtahImtRequestCreator;
import com.google.inject.Inject;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.dvrp.optimizer.Request;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkChangeEvent.ChangeType;
import org.matsim.core.network.NetworkChangeEvent.ChangeValue;
import org.matsim.core.network.NetworkUtils;

import java.io.File;
import java.util.List;
import java.util.Random;

public class Read_Incident {

	// incident creation
	Network network;
	Integer incidentNumber;
	Integer incidentClass;
	Integer incidentID;

	// get network from the scenario being run


	@Inject
	public Read_Incident(Scenario scenario){

		this.network = scenario.getNetwork();
	}

	public void Incident_Generator(String csv) {

		// Reads in the number of incidents from the incident generator
		Incident_Generator generator = new Incident_Generator();
		incidentNumber = generator.getIncNum();

		// Generates the incident class for each incident
		for (int i=1; i<=incidentNumber; i++) {
			double classSelector = Math.random();
			if (classSelector <= 0.349206349) incidentClass = 1;
			if (classSelector > 0.349206349 & classSelector <= 0.534391534) incidentClass = 2;
			if (classSelector > 0.534391534 & classSelector <= 0.73015873) incidentClass = 3;
			if (classSelector > 0.73015873 & classSelector <= 0.899470899) incidentClass = 4;
			if (classSelector > 0.899470899) incidentClass = 5;

			// an ID is assigned to the incident based on its class
			switch (incidentClass) {
				case 1 -> incidentID = new Random().nextInt(65) + 1; //[0...65] + 1 = [1...66]
				case 2 -> incidentID = new Random().nextInt(34) + 67; //[0...34] + 67 = [67...101]
				case 3 -> incidentID = new Random().nextInt(36) + 102; //[0...36] + 102 = [102...138]
				case 4 -> incidentID = new Random().nextInt(31) + 139; //[0...31] + 139 = [139...170]
				case 5 -> incidentID = new Random().nextInt(18) + 171; //[0...18] + 171 = [171...191]
			}


			// Now there is an incident selected. Let's say, for example, it's incident #45 the code will parse through the CSV until it finds that incident.
			// This code should be optimized so that it only has to parse through the CSV once and store all of its data. I am not there yet, but it would make the process move along more quickly.

			CsvParserSettings parserSettings = new CsvParserSettings();
			parserSettings.getFormat().setLineSeparator("\n");
			parserSettings.setHeaderExtractionEnabled(true);

			CsvParser parser = new CsvParser(parserSettings);
			List<Record> records = parser.parseAllRecords(new File(csv));

			for (Record record : records) {
				// Process each record
				String MATSimLink = record.getString("MATSim Link");
				Integer incidentCSV = record.getInt("Incident ID");
				Double start = record.getDouble("Start Time (sec)");
				Double end = record.getDouble("End Time (sec)");
				Double reduction = record.getDouble("Capacity reduction (w/ TWA)");


				// When the incidentID equal the ID found on the CSV file, the code sorts through the links until it finds the matching link ID.

				if (incidentID.equals(incidentCSV))

					network.getLinks().values().forEach(link -> {
						Id<Link> linkId = link.getId();
						String linkNetwork = linkId.toString();
						double capacity = link.getCapacity();
						double reducedCapacity = capacity * reduction;

						if (linkNetwork.equals(MATSimLink)) {

							new UtahImtRequest(Id.create(incidentCSV, Request.class), network.getLinks().get(Id.createLinkId(MATSimLink)), start);


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


							// Although not necessary, I like having the system print out this statement to let me know that the code is working properly.

							System.out.println("Total Number of Incidents " + incidentNumber + " ,Incident ID " + incidentID + ",  Link Number " + MATSimLink + ", Incident Class " + incidentClass + ", Capacity Reduction " + reduction);
						}
					});
				}
			}
		}
	}
