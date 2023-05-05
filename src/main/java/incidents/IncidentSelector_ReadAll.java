package incidents;

import com.google.inject.Inject;
import org.matsim.api.core.v01.network.Network;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that selects a set of incidents from a given list of incidents
 * and applies them to a network.
 */
public class IncidentSelector_ReadAll {

	private final Network network;
	private final List<Incident> incidentsSelected;

	/**
	 * Constructs a new instance of IncidentSelector_ReadAll with the given network.
	 *
	 * @param network the network to apply the incidents on
	 */
	@Inject
	public IncidentSelector_ReadAll(Network network) {
		this.network = network;
		this.incidentsSelected = new ArrayList<>();
	}

	/**
	 * Reads the incidents from the given CSV file, selects all the incidents
	 * and applies them to the network.
	 *
	 * @param csv the path of the CSV file containing the incidents
	 */
	public void readIncidents(String csv) {
		List<Incident> incidents = IncidentParser.parse(csv);
		selectAllIncidents(incidents);
		applySelectedIncidents();
	}

	/**
	 * Selects all the incidents from the given list of incidents.
	 *
	 * @param incidents the list of incidents to select from
	 */
	private void selectAllIncidents(List<Incident> incidents) {
		incidentsSelected.addAll(incidents);
	}

	/**
	 * Applies the selected set of incidents to the network.
	 */
	private void applySelectedIncidents() {
		IncidentApplicator applicator = new IncidentApplicator(network, incidentsSelected);
		applicator.apply();
	}
}
