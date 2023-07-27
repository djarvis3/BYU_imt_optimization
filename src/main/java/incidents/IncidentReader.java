package incidents;

import java.util.*;

import com.google.inject.Inject;
import org.matsim.api.core.v01.network.Network;

import static incidents.IncidentSelector.*;
public class IncidentReader {

	private final String csvFilePath;
	private final Network network;

	@Inject
	public IncidentReader(String csvFilePath, Network network) {
		this.csvFilePath = csvFilePath;
		this.network = network;
	}

	public List<Incident> getAllIncidents() {
		IncidentParser incParse = new IncidentParser(network);
		List<Incident> incidents = incParse.parse(csvFilePath);
		return selectAllIncidents(incidents);
	}

	public List<Incident> getRandomIncidents(int incidentNumber) {
		IncidentParser incParse = new IncidentParser(network);
		List<Incident> incidents = incParse.parse(csvFilePath);
		List<Incident> uniqueIncidents = selectAllUniqueIncidents(incidents);
		return selectRandomSubset(incidents, uniqueIncidents, incidentNumber);
	}

	public List<Incident> getSeededIncidents(int incidentNumber, long seed) {
		IncidentParser incParse = new IncidentParser(network);
		List<Incident> incidents = incParse.parse(csvFilePath);
		List<Incident> uniqueIncidents = selectAllUniqueIncidents(incidents);
		return selectSeededSubset(incidents, uniqueIncidents, incidentNumber, seed);
	}
}
