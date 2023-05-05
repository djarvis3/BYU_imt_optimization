package incidents;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.inject.Inject;
import org.matsim.api.core.v01.network.Network;

/**
 * A class that selects a random set of incidents from a given list of incidents
 * and applies them to a network.
 */
public class RandomIncidentSelector {

	private final Network network;
	private final List<Incident> incidentsSelected;

	/**
	 * Constructs a new instance of RandomIncidentSelector with the given network.
	 *
	 * @param network the network to apply the incidents on
	 */
	@Inject
	public RandomIncidentSelector(Network network) {
		this.network = network;
		this.incidentsSelected = new ArrayList<>();


	}

	/**
	 * Reads the incidents from the given CSV file, selects a random set of incidents
	 * and applies them to the network.
	 *
	 * @param csv the path of the CSV file containing the incidents
	 */
	public void readIncidents(String csv) {
		List<Incident> incidents = IncidentParser.parse(csv);
		selectRandomIncidents(incidents);
		applySelectedIncidents();
	}

	/**
	 * Selects a random set of incidents from the given list of incidents.
	 *
	 * @param incidents the list of incidents to select from
	 */
	private void selectRandomIncidents(List<Incident> incidents) {
		IncidentGenerator generator = new IncidentGenerator();
		int incidentNumber = generator.getIncNum();
		Random random = new Random();
		for (int i = 1; i <= incidentNumber; i++) {
			int randomIndex = random.nextInt(incidents.size());
			incidentsSelected.add(incidents.get(randomIndex));
		}
	}

	/**
	 * Returns the list of incidents that were selected and applied to the network.
	 *
	 * @return the list of incidents
	 */
	public List<Incident> getIncidentsSelected() {
		return incidentsSelected;
	}

	/**
	 * Applies the selected set of incidents to the network.
	 */
	private void applySelectedIncidents() {
		IncidentApplicator applicator = new IncidentApplicator(network, incidentsSelected);
		applicator.apply();
	}
}

