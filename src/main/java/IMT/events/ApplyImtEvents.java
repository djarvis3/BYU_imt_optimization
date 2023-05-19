package IMT.events;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplyImtEvents {
	private final Scenario scenario;
	private final Map<String, Link> linkMap;
	private final List<ImtEvent> imtEvents;

	/**
	 * Creates an incident applicator for the given network and selected incidents.
	 *
	 * @param scenario           the scenario with the network to apply incidents to
	 * @param imtEvents			 the incidents to apply to the network, will usually be either randomly selected
	 *                          or all of the incidents from a CSV read into the incidentReader class
	 *
	 * @throws IllegalArgumentException if any incident's link ID does not exist in the network
	 */
	public ApplyImtEvents(Scenario scenario, List<ImtEvent> imtEvents) {
		this.scenario = scenario;
		this.imtEvents = imtEvents;

		// Build a map of links by ID for fast lookup
		this.linkMap = new HashMap<>();
		for (Link link : scenario.getNetwork().getLinks().values()) {
			linkMap.put(link.getId().toString(), link);
		}

		// Validate that all incident links exist in the network
		for (ImtEvent imtEvent : imtEvents) {
			if (!linkMap.containsKey(imtEvent.getToLink())) {
				throw new IllegalArgumentException("Incident link ID not found in network: " + imtEvent.getToLink());
			}
		}
	}

	/**
	 * Applies the selected incidents to the network.
	 */
	public void apply() {

		imtEvents.parallelStream().forEach(imtEvent -> {
			Link link = linkMap.get(imtEvent.getToLink());

			// initial networkChangeEvent when the incident occurs @StartTime
			NetworkChangeEvent startEvent = new NetworkChangeEvent(imtEvent.getArriveTime());
			startEvent.setFlowCapacityChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, imtEvent.getRestoredCapacity()));
			startEvent.addLink(link);
			NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), startEvent);

		});
	}
}

