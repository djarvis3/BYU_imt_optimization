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
	 * @param csvFilePath
	 */
	@Inject
	public IncidentReader(String csvFilePath) {
		this.csvFilePath = csvFilePath;}

	/**
	 * Reads the incidents from the given CSV file, selects a random set of incidents
	 * and returns them.
	 *
	 * @return the random list of incidents
	 */
	public List<Incident> getRandomIncidents() {
		List<Incident> incidents = IncidentParser.parse(csvFilePath);
		return selectRandomIncidentsFromList(incidents);
	}

	/**
	 * Selects a random number of incidents determined by
	 * the probabilistic functions defined in the IncidentNumber class
	 *
	 * @param incidents the list of incidents to select from
	 * @return the random list of incidents
	 */
	private List<Incident> selectRandomIncidentsFromList(List<Incident> incidents) {
		IncidentNumber generator = new IncidentNumber();
		int incidentNumber = generator.getIncNum();
		Random random = new Random();
		List<Incident> randomIncidents = new ArrayList<>();
		for (int i = 1; i <= incidentNumber; i++) {
			int randomIndex = random.nextInt(incidents.size());
			randomIncidents.add(incidents.get(randomIndex));
		}
		return randomIncidents;
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
}


