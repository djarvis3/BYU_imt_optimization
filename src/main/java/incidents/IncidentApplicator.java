package incidents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkChangeEvent.ChangeType;
import org.matsim.core.network.NetworkChangeEvent.ChangeValue;
import org.matsim.core.network.NetworkUtils;

public class IncidentApplicator {

	private static final Logger LOGGER = Logger.getLogger(IncidentApplicator.class.getName());

	// ensure that this value matches the parameter set in the config file
	private static double FLOW_CAPACITY_FACTOR;

	private final Scenario scenario;
	private final Map<String, Link> linkMap;
	private final List<Incident> incidentsSelected;

	/**
	 * Creates an incident applicator for the given network and selected incidents.
	 *
	 * @param scenario           the scenario with the network to apply incidents to
	 * @param incidentsSelected the incidents to apply to the network, will usually be either randomly selected
	 *                          or all of the incidents from a CSV read into the incidentReader class
	 * @throws IllegalArgumentException if any incident's link ID does not exist in the network
	 */
	public IncidentApplicator(Scenario scenario, List<Incident> incidentsSelected) {
		this.scenario = scenario;
		this.incidentsSelected = incidentsSelected;

		FLOW_CAPACITY_FACTOR = scenario.getConfig().qsim().getFlowCapFactor();


		// Build a map of links by ID for fast lookup
		this.linkMap = new HashMap<>();
		for (Link link : scenario.getNetwork().getLinks().values()) {
			linkMap.put(link.getId().toString(), link);
		}

		// Validate that all incident links exist in the network
		for (Incident incident : incidentsSelected) {
			if (!linkMap.containsKey(incident.getLinkId())) {
				throw new IllegalArgumentException("Incident link ID not found in network: " + incident.getLinkId());
			}
		}
	}

	/**
	 * Applies the selected incidents to the network.
	 */
	public void apply() {
		int totalIncidents = incidentsSelected.size();

		incidentsSelected.parallelStream().forEach(incident -> {
			Link link = linkMap.get(incident.getLinkId());
			double capacity = link.getCapacity() * FLOW_CAPACITY_FACTOR;
			double reducedCapacity = capacity - (capacity * incident.getCapacityReduction());

			// initial networkChangeEvent when the incident occurs @StartTime
			NetworkChangeEvent startEvent = new NetworkChangeEvent(incident.getStartTime());
			startEvent.setFlowCapacityChange(new ChangeValue(ChangeType.ABSOLUTE_IN_SI_UNITS, reducedCapacity));
			startEvent.addLink(link);
			NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), startEvent);

			// secondary networkChangeEvent when the incident ends @EndTime
			NetworkChangeEvent endEvent = new NetworkChangeEvent(incident.getEndTime());
			endEvent.setFlowCapacityChange(new ChangeValue(ChangeType.ABSOLUTE_IN_SI_UNITS, capacity));
			endEvent.addLink(link);
			NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), endEvent);

			String incidentInfo = String.format("Incident ID %s, Link ID %s, Full Capacity %.2f, Reduced Capacity %.2f, Start Time %s, End Time %s, Total Incidents %d",
					incident.getIncidentID(), incident.getLinkId(), capacity, reducedCapacity, incident.getStartTime(), incident.getEndTime(), totalIncidents);


			LOGGER.info(incidentInfo);
		});
	}
}


