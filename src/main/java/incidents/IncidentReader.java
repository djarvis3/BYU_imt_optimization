package incidents;

import java.util.*;

import com.google.inject.Inject;
import org.matsim.api.core.v01.network.Network;

import static incidents.IncidentSelector.*;
/**
 * A class that selects a set of incidents from a given CSV list of incidents
 */
public class IncidentReader {

	private final String csvFilePath;
	private final Network network;

	/**
	 * Constructs a new instance of IncidentReader with the given CSV file and Network.
	 *
	 * @param csvFilePath    the filepath
	 * @param network		 the network to which the incidents are applied
	 */
	@Inject
	public IncidentReader(String csvFilePath, Network network) {
		this.csvFilePath = csvFilePath;
		this.network = network;
	}

	/**
	 * Reads the incidents from the given CSV file, selects all the incidents
	 * with unique link IDs and returns them.
	 *
	 * @return the list of all unique incidents from the CSV file
	 */
	public List<Incident> getAllIncidents() {
		IncidentParser incParse = new IncidentParser(network);
		List<Incident> incidents = incParse.parse(csvFilePath);
		return selectAllUniqueIncidents(incidents);
	}

	/**
	 * Reads the incidents from the given CSV file, selects a random set of incidents
	 * and returns them.
	 *
	 * @return the random list of incidents
	 */
	public List<Incident> getRandomIncidents(int incidentNumber) {
		IncidentParser incParse = new IncidentParser(network);
		List<Incident> incidents = incParse.parse(csvFilePath);
		List<Incident> uniqueIncidents = selectAllUniqueIncidents(incidents);
		return selectRandomSubset(incidents, uniqueIncidents, incidentNumber);
	}

	/**
	 * Reads the incidents from the given CSV file and returns a specific set of incidents
	 * based on the incidentNumber and seed parameters.
	 *
	 * @param incidentNumber the number of incidents to select
	 * @param seed           the seed value to use for randomization
	 * @return the selected incidents
	 */
	public List<Incident> getSeededIncidents(int incidentNumber, long seed) {
		IncidentParser incParse = new IncidentParser(network);
		List<Incident> incidents = incParse.parse(csvFilePath);
		List<Incident> uniqueIncidents = selectAllUniqueIncidents(incidents);
		return selectSeededSubset(incidents, uniqueIncidents, incidentNumber, seed);
	}
}






