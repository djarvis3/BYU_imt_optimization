package incidents;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkChangeEvent.ChangeValue;
import org.matsim.core.network.NetworkUtils;

import java.util.List;
import java.util.stream.Collectors;

public class IncidentApplicator {

	private final Network network;
	private final List<Incident> incidentsSelected;

	public IncidentApplicator(Network network, List<Incident> incidentsSelected) {
		this.network = network;
		this.incidentsSelected = incidentsSelected;
	}

	public void apply() {
		for (Incident incident : incidentsSelected) {
			List<Link> matchingLinks = network.getLinks().values().stream()
					.filter(link -> link.getId().toString().equals(incident.getLinkId()))
					.collect(Collectors.toList());

			for (Link link : matchingLinks) {
				double capacity = link.getCapacity();
				double reducedCapacity = capacity - (capacity * incident.getCapacityReduction());

				NetworkChangeEvent startEvent = new NetworkChangeEvent(incident.getStartTime());
				startEvent.setFlowCapacityChange(new ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, reducedCapacity));
				startEvent.addLink(link);
				NetworkUtils.addNetworkChangeEvent(network, startEvent);

				NetworkChangeEvent endEvent = new NetworkChangeEvent(incident.getEndTime());
				endEvent.setFlowCapacityChange(new ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, capacity));
				endEvent.addLink(link);
				NetworkUtils.addNetworkChangeEvent(network, endEvent);
			}
		}
	}
}

