package incidents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkChangeEvent.ChangeType;
import org.matsim.core.network.NetworkChangeEvent.ChangeValue;
import org.matsim.core.network.NetworkUtils;

public class IncidentApplicator {

	private static final Logger LOGGER = Logger.getLogger(IncidentApplicator.class.getName());

	private static final double FLOW_CAPACITY_FACTOR = 0.01;

	private final Network network;
	private final Map<String, Link> linkMap;
	private final List<Incident> incidentsSelected;

	/**
	 * Creates an incident applicator for the given network and selected incidents.
	 *
	 * @param network           the network to apply incidents to
	 * @param incidentsSelected the incidents to apply to the network
	 * @throws IllegalArgumentException if any incident's link ID does not exist in the network
	 */
	public IncidentApplicator(Network network, List<Incident> incidentsSelected) {
		this.network = network;
		this.incidentsSelected = incidentsSelected;

		// Build a map of links by ID for fast lookup
		this.linkMap = new HashMap<>();
		for (Link link : network.getLinks().values()) {
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

			NetworkChangeEvent startEvent = new NetworkChangeEvent(incident.getStartTime());
			startEvent.setFlowCapacityChange(new ChangeValue(ChangeType.ABSOLUTE_IN_SI_UNITS, reducedCapacity));
			startEvent.addLink(link);
			NetworkUtils.addNetworkChangeEvent(network, startEvent);

			NetworkChangeEvent endEvent = new NetworkChangeEvent(incident.getEndTime());
			endEvent.setFlowCapacityChange(new ChangeValue(ChangeType.ABSOLUTE_IN_SI_UNITS, capacity));
			endEvent.addLink(link);
			NetworkUtils.addNetworkChangeEvent(network, endEvent);

			String incidentInfo = String.format("Incident ID %s, Link ID %s, Full Capacity %.2f, Reduced Capacity %.2f, Start Time %s, End Time %s, Total Incidents %d",
					incident.getIncidentID(), incident.getLinkId(), capacity, reducedCapacity, incident.getStartTime(), incident.getEndTime(), totalIncidents);


			LOGGER.info(incidentInfo);
		});
	}
}


