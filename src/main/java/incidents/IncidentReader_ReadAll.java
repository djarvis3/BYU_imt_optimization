package incidents;

import com.google.inject.Inject;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkChangeEvent.ChangeType;
import org.matsim.core.network.NetworkChangeEvent.ChangeValue;
import org.matsim.core.network.NetworkUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IncidentReader_ReadAll {

	private final Network network;
	private final List<Incident> incidentsAll;

	@Inject
	public IncidentReader_ReadAll(Network network) {
		this.network = network;
		this.incidentsAll = new ArrayList<>();
	}

	// Add this method to get the selected incidents
	public List<Incident> getIncidentsSelected() {
		return incidentsAll;
	}

	public void readIncidents(String csv) {
		List<Incident> incidents = parseIncidents(csv);
		selectAllIncidents(incidents);
		applyIncidentsToNetwork();
	}

	private List<Incident> parseIncidents(String csv) {
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setHeaderExtractionEnabled(true);

		CsvParser parser = new CsvParser(parserSettings);
		List<Record> records = parser.parseAllRecords(new File(csv));

		return records.stream()
				.map(record -> {
					int incidentId = record.getInt("ID");
					String linkId = record.getString("MATSim Link");
					int respondingIMTs = record.getInt("Responding IMTs");
					double start = record.getDouble("Start Time (sec)");
					double end = record.getDouble("End Time (sec)");
					double reduction = record.getDouble("Capacity reduction (w/ TWA)");
					return new Incident(linkId, incidentId, respondingIMTs, start, end, reduction);
				})
				.collect(Collectors.toList());
	}

	private void selectAllIncidents(List<Incident> incidents) {
		incidentsAll.addAll(incidents);
	}

	private void applyIncidentsToNetwork() {
		for (Incident incident : incidentsAll) {
			List<Link> matchingLinks = network.getLinks().values().stream()
					.filter(link -> link.getId().toString().equals(incident.getLinkId()))
					.collect(Collectors.toList());

			for (Link link : matchingLinks) {
				double capacity = link.getCapacity();
				double reducedCapacity = capacity - (capacity*incident.getCapacityReduction());

				NetworkChangeEvent startEvent = new NetworkChangeEvent(incident.getStartTime());
				startEvent.setFlowCapacityChange(new ChangeValue(ChangeType.ABSOLUTE_IN_SI_UNITS, reducedCapacity));
				startEvent.addLink(link);
				NetworkUtils.addNetworkChangeEvent(network, startEvent);

				NetworkChangeEvent endEvent = new NetworkChangeEvent(incident.getEndTime());
				endEvent.setFlowCapacityChange(new ChangeValue(ChangeType.ABSOLUTE_IN_SI_UNITS, capacity));
				endEvent.addLink(link);
				NetworkUtils.addNetworkChangeEvent(network, endEvent);

				// I will be able to get rid of this at some point in the future
				System.out.println("Incident ID " + incident.getIncidentID() + ", Responding IMTs " + incident.getRespondingIMTs() + ", Link Number " + incident.getLinkId() + ", Capacity Reduction " + incident.getCapacityReduction() + ", Start Time " + incident.getStartTime());
			}
		}
	}
}
