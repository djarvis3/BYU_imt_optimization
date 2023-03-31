package byu.incidents;

import com.google.inject.Inject;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkChangeEvent.ChangeType;
import org.matsim.core.network.NetworkChangeEvent.ChangeValue;
import org.matsim.core.network.NetworkUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IncidentReader {

	// incident creation
	private Network network;
	private Integer incidentNumber;
	public List<Incident> incidentsSelected;

	// get network from the scenario being run


	@Inject
	public IncidentReader(Scenario scenario) {

		this.network = scenario.getNetwork();
	}

	public void readIncidents(String csv) {

		// Reads in the number of incidents from the incident generator
		IncidentGenerator generator = new IncidentGenerator();
		incidentNumber = generator.getIncNum();

		// Parse the CSV and generate and ArrayList of Incidents
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setHeaderExtractionEnabled(true);

		CsvParser parser = new CsvParser(parserSettings);
		List<Record> records = parser.parseAllRecords(new File(csv));

		List<Incident> incidents = new ArrayList<>();
		for (Record record : records) {
			// Create an incident object for each row
			String MATSimLink = record.getString("MATSim Link");
			Integer incidentID = record.getInt("Incident ID");
			Integer respondingIMTs = record.getInt("Responding IMTs");
			Double start = record.getDouble("Start Time (sec)");
			Double end = record.getDouble("End Time (sec)");
			Double reduction = record.getDouble("Capacity reduction (w/ TWA)");

			Incident incident = new Incident(MATSimLink, incidentID, respondingIMTs, start, end, reduction);
			incidents.add(incident);
		}

		// Randomly select incidents from the list
		// Select an incident for each integer in incidentNumber
		incidentsSelected = new ArrayList<>();
		Random random = new Random();
		for (int i = 1; i <= incidentNumber; i++) {
			int randomIndex = random.nextInt(incidents.size());
			incidentsSelected.add(incidents.get(randomIndex));
		}

		for (Incident incident : incidentsSelected) {

			network.getLinks().values().stream()
					.filter(link -> link.getId().toString().equals(incident.getLinkId()))
					.forEach(link -> {
						double capacity = link.getCapacity();
						double reducedCapacity = capacity * incident.getCapacityReduction();

						{
							NetworkChangeEvent startEvent = new NetworkChangeEvent(incident.getStartTime());
							startEvent.setFlowCapacityChange(new ChangeValue(ChangeType.ABSOLUTE_IN_SI_UNITS, reducedCapacity));
							startEvent.addLink(link);
							NetworkUtils.addNetworkChangeEvent(network, startEvent);
						}
						{
							NetworkChangeEvent endEvent = new NetworkChangeEvent(incident.getEndTime());
							endEvent.setFlowCapacityChange(new ChangeValue(ChangeType.ABSOLUTE_IN_SI_UNITS, capacity));
							endEvent.addLink(link);
							NetworkUtils.addNetworkChangeEvent(network, endEvent);
						}

						System.out.println("Total Number of Incidents " + incidentNumber + " ,Incident ID " + incident.getIncidentID() + " ,Responding IMTs " + incident.getRespondingIMTs() + ",  Link Number " + incident.getLinkId() + ", Capacity Reduction " + incident.getCapacityReduction());
					});
		}
	}

	//Getter method for incidentsSelected list
	public List<Incident> getIncidentsSelected(){
		return incidentsSelected;
	}
}
