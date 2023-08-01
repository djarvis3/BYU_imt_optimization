package IMT.events.eventHanlders;

import IMT.events.IncidentEvent;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.events.handler.BasicEventHandler;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkChangeEvent.ChangeType;
import org.matsim.core.network.NetworkChangeEvent.ChangeValue;
import org.matsim.core.network.NetworkUtils;

/**
 * Handles incident events and generates network change events accordingly.
 * Operates only during the first iteration of the simulation.
 */
public class IncidentEventHandler implements BasicEventHandler {

	private final Scenario scenario;
	private boolean isFirstIteration = true;

	/**
	 * Constructs an IncidentEventHandler object with the given scenario.
	 *
	 * @param scenario the scenario to be used for event handling
	 */
	public IncidentEventHandler(Scenario scenario) {
		this.scenario = scenario;
	}

	/**
	 * Handles the incident event and generates network change events for incident start and end.
	 * Only operates during the first iteration.
	 *
	 * @param event the event to be handled
	 */
	@Override
	public void handleEvent(Event event) {
		if (!isFirstIteration || !(event instanceof IncidentEvent incidentEvent)) return;

		createNetworkChangeEvent(incidentEvent.getTime(), incidentEvent.getReducedCapacity(), incidentEvent.getLinkId());
		createNetworkChangeEvent(incidentEvent.getEndTime(), incidentEvent.getFullCapacity(), incidentEvent.getLinkId());
	}

	/**
	 * Creates and adds a network change event to the scenario's network.
	 *
	 * @param time       the time at which the change occurs
	 * @param capacity   the new capacity value
	 * @param linkId     the link to which the change applies
	 */
	private void createNetworkChangeEvent(double time, double capacity, Id<Link> linkId) {
		NetworkChangeEvent event = new NetworkChangeEvent(time);
		double roundedCapacity = Math.round(capacity);
		event.setFlowCapacityChange(new ChangeValue(ChangeType.ABSOLUTE_IN_SI_UNITS, roundedCapacity));
		event.addLink(scenario.getNetwork().getLinks().get(linkId));
		NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), event);
	}

	/**
	 * Resets the state of the event handler for the first iteration.
	 * After the first iteration, the handler will no longer process events.
	 *
	 * @param iteration the iteration number
	 */
	@Override
	public void reset(int iteration) {
		if (iteration != 0) {
			this.isFirstIteration = false;
		}
	}
}
