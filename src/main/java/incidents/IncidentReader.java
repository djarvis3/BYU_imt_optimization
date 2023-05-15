package incidents;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.inject.Inject;

/**
 * A class that selects a set of incidents from a given CSV list of incidents
 */
public class IncidentReader {

	private final String csvFilePath;

	/**
	 * Constructs a new instance of IncidentReader with the given CSV file.
	 *
	 * @param csvFilePath    the filepath
	 */
	@Inject
	public IncidentReader(String csvFilePath) {
		this.csvFilePath = csvFilePath;
	}


	/**
	 * Reads the incidents from the given CSV file, selects all the incidents
	 * and returns them.
	 *
	 * @return the list of all incidents from the CSV file
	 */
	public List<Incident> getAllIncidents() {
		return IncidentParser.parse(csvFilePath);
	}

	/**
	 * Reads the incidents from the given CSV file, selects a random set of incidents
	 * and returns them.
	 *
	 * @return the random list of incidents
	 */
	public List<Incident> getRandomIncidents(int incidentNumber) {
		List<Incident> incidents = IncidentParser.parse(csvFilePath);
		return selectRandomSubset(incidents, incidentNumber);
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
		List<Incident> incidents = IncidentParser.parse(csvFilePath);
		return selectRandomSubset(incidents, incidentNumber, seed);
	}


	private List<Incident> selectRandomSubset(List<Incident> incidents, int incidentNumber) {
		if (incidentNumber > incidents.size()) {
			throw new IllegalArgumentException("incidentNumber is greater than the size of incidents");
		}
		Random random = new Random();
		List<Incident> selectedIncidents = new ArrayList<>();
		while (selectedIncidents.size() < incidentNumber) {
			int randomIndex = random.nextInt(incidents.size());
			Incident randomIncident = incidents.get(randomIndex);
			if (!selectedIncidents.contains(randomIncident)) {
				selectedIncidents.add(randomIncident);
			}
		}
		return selectedIncidents;
	}


	private List<Incident> selectRandomSubset(List<Incident> incidents, int incidentNumber, long seed) {
		if (incidentNumber > incidents.size()) {
			throw new IllegalArgumentException("incidentNumber is greater than the size of incidents");
		}
		Random random = new Random(seed);
		List<Incident> selectedIncidents = new ArrayList<>();
		while (selectedIncidents.size() < incidentNumber) {
			int randomIndex = random.nextInt(incidents.size());
			Incident randomIncident = incidents.get(randomIndex);
			if (!selectedIncidents.contains(randomIncident)) {
				selectedIncidents.add(randomIncident);
			}
		}
		return selectedIncidents;
	}
}






